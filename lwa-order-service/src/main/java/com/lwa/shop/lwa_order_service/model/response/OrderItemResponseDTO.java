package com.lwa.shop.lwa_order_service.model.response;

import lombok.Data;

@Data
public class OrderItemResponseDTO {
    private Long id;
    private String variantColor;
    private String variantSize;
    private Double price;
    private Integer quantity;
}
