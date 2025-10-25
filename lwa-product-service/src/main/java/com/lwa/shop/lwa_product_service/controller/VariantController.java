package com.lwa.shop.lwa_product_service.controller;


import com.lwa.shop.lwa_product_service.entity.Variant;
import com.lwa.shop.lwa_product_service.model.GeneralResponse;
import com.lwa.shop.lwa_product_service.model.request.SaveVariant;
import com.lwa.shop.lwa_product_service.service.VariantService;
import com.lwa.shop.lwa_product_service.util.JsonUtil;
import com.lwa.shop.lwa_product_service.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/variants")
@Tag(name = "Variant API", description = "Operations for managing item variants")
public class VariantController {

    private final VariantService variantService;

    @Operation(summary = "Get all variants")
    @ApiResponse(
            responseCode = "200",
            description = "Fetched all variants",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GeneralResponse.class),
                    examples = @ExampleObject(
                            name = "AllVariantsExample",
                            value = "{\"code\":\"200\",\"message\":\"Fetched all variants\",\"data\":[{\"id\":1,\"createdAt\":\"2025-10-25T16:19:30\",\"updatedAt\":\"2025-10-25T16:22:03\",\"createdBy\":\"SYSTEM\",\"updatedBy\":\"SYSTEM\",\"color\":\"yellow\",\"size\":\"S\",\"price\":10000,\"stock\":4}]}"
                    )
            )
    )
    @GetMapping
    public ResponseEntity<GeneralResponse<List<Variant>>> getAllVariants() {
        log.info("incoming getAllVariants request");
        List<Variant> variants = variantService.getAllVariants();
        return ResponseUtil.success("Fetched all variants", variants);
    }


    @Operation(summary = "Get variant by ID")
    @ApiResponse(
            responseCode = "200",
            description = "Fetched variant by ID",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = GeneralResponse.class),
                    examples = @ExampleObject(
                            name = "VariantByIdExample",
                            value = "{\"code\":\"200\",\"message\":\"Fetched variant with id 1\",\"data\":{\"id\":1,\"createdAt\":\"2025-10-25T16:19:30\",\"updatedAt\":\"2025-10-25T16:22:03\",\"createdBy\":\"SYSTEM\",\"updatedBy\":\"SYSTEM\",\"color\":\"yellow\",\"size\":\"S\",\"price\":10000,\"stock\":4}}"
                    )
            )
    )
    @GetMapping("{id}")
    public ResponseEntity<GeneralResponse<Variant>> getVariant(@PathVariable Long id) {
        log.info("incoming getVariant request");
        Variant variant = variantService.getVariant(id);
        return ResponseUtil.success("Fetched variant with id " + id, variant);
    }

    @Operation(
            summary = "Create a new variant",
            requestBody = @RequestBody(
                    description = "Variant object to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SaveVariant.class),
                            examples = @ExampleObject(
                                    name = "CreateVariantExample",
                                    value = "{\n" +
                                            "  \"color\": \"Blue\",\n" +
                                            "  \"size\": \"M\",\n" +
                                            "  \"price\": 100,\n" +
                                            "  \"stock\": 5,\n" +
                                            "  \"itemId\": 3\n" +
                                            "}"
                            )
                    )
            )
    )
    @PostMapping
    public ResponseEntity<GeneralResponse<Variant>> addVariant(@org.springframework.web.bind.annotation.RequestBody SaveVariant variant) {
        log.info("incoming add variant request {}", JsonUtil.toJson(variant));
        Variant savedVariant = variantService.saveVariant(variant, null);
        return ResponseUtil.created("Variant created successfully", savedVariant);
    }

    @Operation(
            summary = "Update a variant",
            requestBody = @RequestBody(
                    description = "Variant object to update",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SaveVariant.class),
                            examples = @ExampleObject(
                                    name = "UpdateVariantExample",
                                    value = "{\n" +
                                            "  \"color\": \"blue\",\n" +
                                            "  \"size\": \"S\",\n" +
                                            "  \"price\": 10000.00,\n" +
                                            "  \"stock\": 10,\n" +
                                            "  \"itemId\": 1\n" +
                                            "}"
                            )
                    )
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<Variant>> updateVariant(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody SaveVariant request) {
        log.info("incoming update variant request {} -> {}", id, JsonUtil.toJson(request));
        Variant updated = variantService.saveVariant(request, id);
        return ResponseUtil.success("Variant updated successfully", updated);
    }

    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<GeneralResponse<Variant>> reduceStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        Variant variant = variantService.reduceStock(id, quantity);
        return ResponseUtil.success("Stock reduced", variant);
    }

    @PutMapping("/{id}/increase-stock")
    public ResponseEntity<GeneralResponse<Variant>> increaseStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        Variant variant = variantService.increaseStock(id, quantity);
        return ResponseUtil.success("Stock increased", variant);
    }

    @Operation(summary = "Delete variant by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteVariant(@PathVariable Long id) {
        log.info("incoming delete variant request {}", id);
        variantService.deleteVariant(id);
        return ResponseUtil.deleted("Variant deleted successfully");
    }
}
