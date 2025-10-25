package com.lwa.shop.lwa_order_service.model.response;

import lombok.Data;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private String customerName;
    private String status;
    private Double totalAmount;
    private List<OrderItemResponseDTO> orderItems;
}
