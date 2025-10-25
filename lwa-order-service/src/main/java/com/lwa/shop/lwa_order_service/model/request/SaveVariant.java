package com.lwa.shop.lwa_order_service.model.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveVariant {
    private String color;
    private String size;
    private Double price;
    private Integer stock;
    private long itemId;
}
