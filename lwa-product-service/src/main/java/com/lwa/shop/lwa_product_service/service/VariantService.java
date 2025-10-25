package com.lwa.shop.lwa_product_service.service;


import com.lwa.shop.lwa_product_service.entity.Item;
import com.lwa.shop.lwa_product_service.entity.Variant;
import com.lwa.shop.lwa_product_service.exception.CustomException;
import com.lwa.shop.lwa_product_service.model.request.SaveVariant;
import com.lwa.shop.lwa_product_service.repository.ItemRepository;
import com.lwa.shop.lwa_product_service.repository.VariantRepository;
import com.lwa.shop.lwa_product_service.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VariantService {

    private final VariantRepository variantRepository;
    private final ItemRepository itemRepository;

    public List<Variant> getAllVariants() {
        log.info("Fetching all variants");
        return variantRepository.findAll();
    }

    public Variant getVariant(Long id) {
        log.info("Fetching variant with id {}", id);
        return variantRepository.findById(id)
                .orElseThrow(() -> new CustomException("Variant not found with id " + id));
    }

    public Variant saveVariant(SaveVariant data, Long id) {
        try {
            Item savedItem = itemRepository.findById(data.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found with id: " + data.getItemId()));

            Variant variant =  id == null ? new Variant() : variantRepository.findById(id).orElse(new Variant());
            variant.setColor(data.getColor());
            variant.setSize(data.getSize());
            variant.setPrice(data.getPrice());
            variant.setStock(data.getStock());
            variant.setItem(savedItem); // link parent

            Variant savedVariant = variantRepository.save(variant);
            log.info("Saved variant: {}", JsonUtil.toJson(savedVariant));
            return savedVariant;
        } catch (Exception e) {
            log.error("Error saving variant: {}", data, e);
            throw new RuntimeException("Failed to save variant", e);
        }
    }

    @Transactional
    public Variant reduceStock(Long variantId, int quantity) {
        int updated = variantRepository.reduceStock(variantId, quantity);

        if (updated == 0) {
            log.warn("Failed to reduce stock: variantId={}, quantity={}", variantId, quantity);
            throw new RuntimeException("Insufficient stock for variant id: " + variantId);
        }

        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found: " + variantId));

        log.info("Reduced stock: variantId={}, quantity={}, newStock={}", variantId, quantity, variant.getStock());
        return variant;
    }

    @Transactional
    public Variant increaseStock(Long variantId, int quantity) {
        int updated = variantRepository.increaseStock(variantId, quantity);

        if (updated == 0) {
            log.warn("Failed to increase stock: variantId={}, quantity={}", variantId, quantity);
            throw new RuntimeException("Variant not found: " + variantId);
        }

        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found: " + variantId));

        log.info("Increased stock: variantId={}, quantity={}, newStock={}", variantId, quantity, variant.getStock());
        return variant;
    }

    @Transactional
    public void deleteVariant(Long id) {
        log.info("Deleting variant with id {}", id);

        Variant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found with id: " + id));

        // Break the link to parent to avoid FK issues
        Item parentItem = variant.getItem();
        if (parentItem != null) {
            parentItem.getVariants().remove(variant);
            variant.setItem(null);
        }

        // Explicit delete
        variantRepository.delete(variant);
    }


}

