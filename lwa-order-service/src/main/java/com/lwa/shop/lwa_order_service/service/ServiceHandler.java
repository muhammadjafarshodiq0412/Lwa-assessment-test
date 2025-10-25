package com.lwa.shop.lwa_order_service.service;

import com.lwa.shop.lwa_order_service.exception.CustomException;
import com.lwa.shop.lwa_order_service.model.GeneralResponse;
import com.lwa.shop.lwa_order_service.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ServiceHandler {
    public static <T> ResponseEntity<GeneralResponse<T>> handle(ServiceExecutor<T> executor) {
        try {
            return executor.execute();
        } catch (CustomException e) {
            log.error("Business error: {}", e.getMessage());
            return ResponseUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return ResponseUtil.error("Internal server error");
        }
    }
}

