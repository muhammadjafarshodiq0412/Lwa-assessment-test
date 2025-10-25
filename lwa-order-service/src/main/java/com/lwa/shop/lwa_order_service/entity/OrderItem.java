package com.lwa.shop.lwa_order_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class OrderItem extends BaseEntity {
    private Integer quantity;
    private Double price;
    private Long variantId;  // store variant reference only
    private String color;    // optional, snapshot of variant info
    private String size;     // optional, snapshot of variant info

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;
}



