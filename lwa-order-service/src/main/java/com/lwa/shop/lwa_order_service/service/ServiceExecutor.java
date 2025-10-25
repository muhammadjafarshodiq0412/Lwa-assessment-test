package com.lwa.shop.lwa_order_service.service;

import com.lwa.shop.lwa_order_service.model.GeneralResponse;
import org.springframework.http.ResponseEntity;

@FunctionalInterface
public interface ServiceExecutor<T> {
    ResponseEntity<GeneralResponse<T>> execute() throws Exception;
}

