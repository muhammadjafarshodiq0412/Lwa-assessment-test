package com.lwa.shop.lwa_product_service.util;

import com.lwa.shop.lwa_product_service.model.GeneralResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class ResponseUtil {

    public static <T> ResponseEntity<GeneralResponse<T>> success(String message, T data) {
        log.info("Success: {}", message);
        GeneralResponse<T> response = new GeneralResponse<>("200", "Success", message, data);
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<GeneralResponse<T>> created(String message, T data) {
        log.info("Created: {}", message);
        GeneralResponse<T> response = new GeneralResponse<>("201", "Success", message, data);
        return ResponseEntity.status(201).body(response);
    }

    public static ResponseEntity<GeneralResponse<Void>> deleted(String message) {
        log.info("Deleted: {}", message);
        GeneralResponse<Void> response = new GeneralResponse<>("200", "Success", message, null);
        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<GeneralResponse<T>> error(String message) {
        log.error("Error: {}", message);
        GeneralResponse<T> response = new GeneralResponse<>("500", "Error", message, null);
        return ResponseEntity.status(500).body(response);
    }
}
