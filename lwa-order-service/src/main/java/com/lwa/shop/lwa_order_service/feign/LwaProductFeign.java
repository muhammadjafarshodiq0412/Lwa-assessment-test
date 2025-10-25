package com.lwa.shop.lwa_order_service.feign;

import com.lwa.shop.lwa_order_service.feign.fallback.LwaProductFallback;
import com.lwa.shop.lwa_order_service.model.Variant;
import com.lwa.shop.lwa_order_service.model.GeneralResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "lwa-product-service", fallback = LwaProductFallback.class)
public interface LwaProductFeign {

    @PutMapping("/product/variants/{id}/reduce-stock")
    @CircuitBreaker(name = "variantService", fallbackMethod = "reduceStockFallback")
    @Retry(name = "variantService")
    GeneralResponse<Variant> reduceStock(@PathVariable("id") Long id, @RequestParam int quantity);

    @GetMapping("/product/variants/{id}")
    @CircuitBreaker(name = "variantService", fallbackMethod = "getVariantFallback")
    @Retry(name = "variantService")
    GeneralResponse<Variant> getVariant(@PathVariable("id") Long id);

    @PutMapping("/product/variants/{id}/increase-stock")
    @CircuitBreaker(name = "variantService", fallbackMethod = "increaseStockFallback")
    @Retry(name = "variantService")
    GeneralResponse<Variant> increaseStock(@PathVariable("id") Long id, @RequestParam int quantity);

}

