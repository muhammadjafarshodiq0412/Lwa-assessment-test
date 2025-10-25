package com.lwa.shop.lwa_order_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Variant {
    private Long id;
    private String color;
    private String size;
    private Double price;
    private Integer stock;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private String updatedBy;
}

