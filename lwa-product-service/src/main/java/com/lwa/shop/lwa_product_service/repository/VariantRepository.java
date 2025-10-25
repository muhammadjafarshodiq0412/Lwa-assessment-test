package com.lwa.shop.lwa_product_service.repository;

import com.lwa.shop.lwa_product_service.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {

    /* This atomic handle for concurency*/
    @Transactional
    @Modifying
    @Query("UPDATE Variant v SET v.stock = v.stock - :quantity WHERE v.id = :id AND v.stock >= :quantity")
    int reduceStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Transactional
    @Modifying
    @Query("UPDATE Variant v SET v.stock = v.stock + :quantity WHERE v.id = :id")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}

