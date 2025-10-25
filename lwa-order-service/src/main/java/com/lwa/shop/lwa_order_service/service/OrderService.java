package com.lwa.shop.lwa_order_service.service;

import com.lwa.shop.lwa_order_service.model.request.OrderRequestDTO;
import com.lwa.shop.lwa_order_service.model.response.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    /**
     * Create a new order with customer and order items.
     *
     * @param orderRequestDTO the order request DTO
     * @return the saved order response DTO
     */
    OrderResponseDTO saveOrder(OrderRequestDTO orderRequestDTO);

    /**
     * Get all orders.
     *
     * @return list of order response DTOs
     */
    List<OrderResponseDTO> getAllOrders();

    /**
     * Get a single order by ID.
     *
     * @param id the order ID
     * @return the order response DTO
     */
    OrderResponseDTO getOrder(Long id);

    /**
     * Mark an order as completed.
     *
     * @param id the order ID
     * @return the updated order response DTO
     */
    OrderResponseDTO completeOrder(Long id);

    /**
     * Delete an order by ID.
     *
     * @param id the order ID
     */
    void deleteOrder(Long id);
}
