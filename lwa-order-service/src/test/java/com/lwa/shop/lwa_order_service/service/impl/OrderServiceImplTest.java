package com.lwa.shop.lwa_order_service.service.impl;

import com.lwa.shop.lwa_order_service.entity.Order;
import com.lwa.shop.lwa_order_service.exception.CustomException;
import com.lwa.shop.lwa_order_service.feign.LwaProductFeign;
import com.lwa.shop.lwa_order_service.model.GeneralResponse;
import com.lwa.shop.lwa_order_service.model.Variant;
import com.lwa.shop.lwa_order_service.model.request.OrderItemRequestDTO;
import com.lwa.shop.lwa_order_service.model.request.OrderRequestDTO;
import com.lwa.shop.lwa_order_service.model.response.OrderResponseDTO;
import com.lwa.shop.lwa_order_service.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private LwaProductFeign lwaProductFeign;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // === SUCCESS CASE ===
    @Test
    void saveOrder_success() {
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setVariantId(1L);
        itemDto.setQuantity(2);

        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setCustomerName("John Doe");
        dto.setOrderItems(List.of(itemDto));

        Variant variant = new Variant();
        variant.setId(1L);
        variant.setPrice(100.0);
        variant.setStock(10);
        variant.setColor("Red");
        variant.setSize("M");

        when(lwaProductFeign.getVariant(1L))
                .thenReturn(new GeneralResponse<>("200", "OK", "Fetched", variant));

        when(lwaProductFeign.reduceStock(1L, 2))
                .thenReturn(new GeneralResponse<>("200", "OK", "Stock reduced", variant));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });

        OrderResponseDTO response = orderService.saveOrder(dto);

        assertNotNull(response);
        assertEquals("John Doe", response.getCustomerName());
        assertEquals(200.0, response.getTotalAmount());
        assertEquals(1, response.getOrderItems().size());
        verify(lwaProductFeign, times(1)).reduceStock(1L, 2);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    // === INSUFFICIENT STOCK CASE ===
    @Test
    void saveOrder_insufficientStock() {
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setVariantId(1L);
        itemDto.setQuantity(5);

        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setCustomerName("John Doe");
        dto.setOrderItems(List.of(itemDto));

        Variant variant = new Variant();
        variant.setId(1L);
        variant.setStock(2);
        variant.setPrice(100.0);

        when(lwaProductFeign.getVariant(1L))
                .thenReturn(new GeneralResponse<>("200", "OK", "Fetched", variant));

        assertThrows(CustomException.class, () -> orderService.saveOrder(dto));
        verify(orderRepository, never()).save(any());
    }

    // === CONCURRENCY TEST ===
    @Test
    void saveOrder_concurrentOrders() throws InterruptedException {
        int initialStock = 5;
        AtomicInteger stock = new AtomicInteger(initialStock);
        AtomicInteger successCount = new AtomicInteger();
        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        Variant variant = new Variant();
        variant.setId(1L);
        variant.setPrice(100.0);
        variant.setColor("Blue");
        variant.setSize("L");
        variant.setStock(initialStock);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId((long) (Math.random() * 1000));
            return order;
        });

        // Always return a valid variant first
        when(lwaProductFeign.getVariant(anyLong())).thenAnswer(inv ->
                new GeneralResponse<>("200", "OK", "Fetched", variant)
        );

        // Simulate atomic stock update
        when(lwaProductFeign.reduceStock(anyLong(), anyInt())).thenAnswer(invocation -> {
            int qty = invocation.getArgument(1);
            synchronized (stock) {
                if (stock.get() >= qty) {
                    stock.addAndGet(-qty);
                    successCount.incrementAndGet();
                    log.info("✅ Stock reduced by {}. Remaining: {}", qty, stock.get());
                    return new GeneralResponse<>("200", "OK", "Stock reduced", variant);
                } else {
                    log.warn("❌ Insufficient stock. Requested: {}, Available: {}", qty, stock.get());
                    throw new CustomException("Insufficient stock");
                }
            }
        });

        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
                    itemDto.setVariantId(1L);
                    itemDto.setQuantity(1);

                    OrderRequestDTO dto = new OrderRequestDTO();
                    dto.setCustomerName("User-" + Thread.currentThread().getId());
                    dto.setOrderItems(List.of(itemDto));

                    try {
                        orderService.saveOrder(dto);
                        log.info("✅ Order succeeded for {}", dto.getCustomerName());
                    } catch (CustomException e) {
                        log.warn("❌ Order failed for {}: {}", dto.getCustomerName(), e.getMessage());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        log.info("✅ Final successCount: {}, remaining stock: {}", successCount.get(), stock.get());
        assertEquals(initialStock, successCount.get(), "Only " + initialStock + " orders should succeed");
    }

}
