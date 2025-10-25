package com.lwa.shop.lwa_order_service.service.impl;

import com.lwa.shop.lwa_order_service.entity.Order;
import com.lwa.shop.lwa_order_service.entity.OrderItem;
import com.lwa.shop.lwa_order_service.model.Variant;
import com.lwa.shop.lwa_order_service.exception.CustomException;
import com.lwa.shop.lwa_order_service.feign.LwaProductFeign;
import com.lwa.shop.lwa_order_service.model.GeneralResponse;
import com.lwa.shop.lwa_order_service.model.request.OrderItemRequestDTO;
import com.lwa.shop.lwa_order_service.model.request.OrderRequestDTO;
import com.lwa.shop.lwa_order_service.model.response.OrderItemResponseDTO;
import com.lwa.shop.lwa_order_service.model.response.OrderResponseDTO;
import com.lwa.shop.lwa_order_service.repository.OrderRepository;
import com.lwa.shop.lwa_order_service.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final LwaProductFeign lwaProductFeign;

    private static final String STOCK_CB = "stockService";

    @Override
    @Transactional
    @Retry(name = STOCK_CB, fallbackMethod = "fallbackSaveOrder")
    @CircuitBreaker(name = STOCK_CB, fallbackMethod = "fallbackSaveOrder")
    public OrderResponseDTO saveOrder(OrderRequestDTO dto) {
        log.info("==== START creating order for customer: {} ====", dto.getCustomerName());

        Order order = new Order();
        order.setCustomerName(dto.getCustomerName());
        order.setStatus("PENDING");

        double totalAmount = 0.0;

        List<OrderItem> orderItems = dto.getOrderItems().stream()
                .map(itemDto -> {
                    log.info("Processing order item for variantId: {} with quantity: {}",
                            itemDto.getVariantId(), itemDto.getQuantity());
                    return createOrderItem(itemDto);
                })
                .collect(Collectors.toList());

        for (OrderItem item : orderItems) {
            totalAmount += item.getPrice() * item.getQuantity();
            item.setOrder(order);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        log.info("==== FINISHED creating order with id: {} for customer: {} ====",
                savedOrder.getId(), savedOrder.getCustomerName());

        return mapToResponse(savedOrder);
    }

    private OrderItem createOrderItem(OrderItemRequestDTO itemDto) {
        // 1. Fetch variant
        GeneralResponse<Variant> variant = lwaProductFeign.getVariant(itemDto.getVariantId());

        // 2. Check stock
        if (variant.getData().getStock() < itemDto.getQuantity()) {
            throw new CustomException("Insufficient stock for variant id: " + itemDto.getVariantId());
        }

        // 3. Reduce stock atomically via Product Service
        lwaProductFeign.reduceStock(itemDto.getVariantId(), itemDto.getQuantity());

        // 4. Create order item
        OrderItem orderItem = new OrderItem();
        orderItem.setVariantId(variant.getData().getId());
        orderItem.setQuantity(itemDto.getQuantity());
        orderItem.setPrice(variant.getData().getPrice());
        orderItem.setColor(variant.getData().getColor());
        orderItem.setSize(variant.getData().getSize());
        return orderItem;
    }


    // Fallback for Resilience4j with detailed logging
    private OrderResponseDTO fallbackSaveOrder(OrderRequestDTO dto, Throwable t) {
        log.error("==== FALLBACK triggered for saveOrder ====");
        log.error("Customer: {}", dto.getCustomerName());

        if (dto.getOrderItems() != null) {
            dto.getOrderItems().forEach(item ->
                    log.error("OrderItem variantId: {}, quantity: {}", item.getVariantId(), item.getQuantity())
            );
        } else {
            log.warn("No order items provided in the request");
        }

        log.error("Reason: {}", t.getMessage(), t);
        log.error("==== END FALLBACK ====");

        throw new CustomException("Unable to create order at this time, please retry later.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        log.info("==== START fetching all orders ====");
        List<OrderResponseDTO> orders = orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.info("==== FINISHED fetching all orders, total: {} ====", orders.size());
        return orders;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrder(Long id) {
        log.info("==== START fetching order with id: {} ====", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException("Order not found: " + id));
        OrderResponseDTO response = mapToResponse(order);
        log.info("==== FINISHED fetching order with id: {} ====", id);
        return response;
    }

    @Override
    @Transactional
    public OrderResponseDTO completeOrder(Long id) {
        log.info("==== START completing order with id: {} ====", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException("Order not found: " + id));
        order.setStatus("COMPLETED");
        Order saved = orderRepository.save(order);
        log.info("==== FINISHED completing order with id: {}, status: {} ====", id, saved.getStatus());
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteOrder(Long id) {
        log.info("==== START deleting order with id: {} ====", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new CustomException("Order not found: " + id));

        // Rollback stock via Product Service
        for (OrderItem item : order.getOrderItems()) {
            lwaProductFeign.increaseStock(item.getVariantId(), item.getQuantity());
            log.info("Rolled back stock for variantId: {}, quantity: {}", item.getVariantId(), item.getQuantity());
        }

        orderRepository.delete(order);
        log.info("==== FINISHED deleting order with id: {} ====", id);
    }

    private OrderResponseDTO mapToResponse(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponseDTO> items = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponseDTO itemDto = new OrderItemResponseDTO();
                    itemDto.setId(item.getId());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPrice(item.getPrice());
                    itemDto.setVariantColor(item.getColor()); // use snapshot
                    itemDto.setVariantSize(item.getSize());   // use snapshot
                    return itemDto;
                }).collect(Collectors.toList());

        dto.setOrderItems(items);
        return dto;
    }

}
