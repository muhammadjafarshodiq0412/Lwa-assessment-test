package com.lwa.shop.lwa_product_service.repository;

import com.lwa.shop.lwa_product_service.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
