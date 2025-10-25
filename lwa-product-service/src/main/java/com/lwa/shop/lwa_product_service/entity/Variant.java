package com.lwa.shop.lwa_product_service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Variant extends BaseEntity {
    private String color;
    private String size;
    private Double price;
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonBackReference
    @ToString.Exclude
    private Item item;
}

