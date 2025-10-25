package com.lwa.shop.lwa_order_service.feign.fallback;

import com.lwa.shop.lwa_order_service.feign.LwaProductFeign;
import com.lwa.shop.lwa_order_service.model.GeneralResponse;
import com.lwa.shop.lwa_order_service.model.Variant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LwaProductFallback implements LwaProductFeign {

    @Override
    public GeneralResponse<Variant> reduceStock(Long id, int quantity) {
        log.error("Fallback triggered: reduceStock failed for variantId={} quantity={}", id, quantity);
        return GeneralResponse.<Variant>builder()
                .code("500")
                .status("FAILED")
                .message("Product Service unavailable. Could not reduce stock.")
                .data(null)
                .build();
    }

    @Override
    public GeneralResponse<Variant> getVariant(Long id) {
        log.error("Fallback triggered: getVariant failed for variantId={}", id);
        return GeneralResponse.<Variant>builder()
                .code("500")
                .status("FAILED")
                .message("Product Service unavailable. Could not fetch variant.")
                .data(null)
                .build();
    }

    @Override
    public GeneralResponse<Variant> increaseStock(Long id, int quantity) {
        log.error("Fallback triggered: increaseStock failed for variantId={} quantity={}", id, quantity);
        return GeneralResponse.<Variant>builder()
                .code("500")
                .status("FAILED")
                .message("Product Service unavailable. Could not increase stock.")
                .data(null)
                .build();
    }
}


