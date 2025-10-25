package com.lwa.shop.lwa_product_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralResponse<T> {
    private String code;
    private String status;
    private String message;
    private T data;
}
