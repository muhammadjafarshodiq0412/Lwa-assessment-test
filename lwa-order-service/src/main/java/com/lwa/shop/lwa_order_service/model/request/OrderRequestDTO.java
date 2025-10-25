package com.lwa.shop.lwa_order_service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private String customerName;
    private List<OrderItemRequestDTO> orderItems;
}
