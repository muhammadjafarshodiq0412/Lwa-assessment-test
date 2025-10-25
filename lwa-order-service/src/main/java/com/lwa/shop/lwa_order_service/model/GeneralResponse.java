package com.lwa.shop.lwa_order_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralResponse<T> {
    private String code;
    private String status;
    private String message;
    private T data;
}
