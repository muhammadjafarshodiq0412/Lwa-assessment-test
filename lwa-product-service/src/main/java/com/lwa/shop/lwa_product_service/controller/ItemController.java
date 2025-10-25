package com.lwa.shop.lwa_product_service.controller;

import com.lwa.shop.lwa_product_service.entity.Item;
import com.lwa.shop.lwa_product_service.model.GeneralResponse;
import com.lwa.shop.lwa_product_service.service.ItemService;
import com.lwa.shop.lwa_product_service.util.JsonUtil;
import com.lwa.shop.lwa_product_service.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Tag(name = "Item API", description = "Operations for managing items")
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;

    @Operation(summary = "Get all items", description = "Fetch all items from the database")
    @ApiResponse(responseCode = "200", description = "Successfully fetched items",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GeneralResponse.class)))
    @GetMapping
    public ResponseEntity<GeneralResponse<List<Item>>> getAllItems() {
        log.info("incoming getAllItems request");
        List<Item> items = itemService.getAllItems();
        return ResponseUtil.success("Fetched all items", items);
    }

    @Operation(summary = "Get item by ID", description = "Fetch a single item by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully fetched item",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GeneralResponse.class)))
    @ApiResponse(responseCode = "404", description = "Item not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GeneralResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<Item>> getItem(
            @Parameter(description = "ID of the item to fetch", required = true)
            @PathVariable Long id) {
        log.info("incoming getItem request");
        Item item = itemService.getItem(id);
        return ResponseUtil.success("Fetched item with id " + id, item);
    }

    @Operation(summary = "Create a new item", description = "Add a new item")
    @ApiResponse(responseCode = "201", description = "Item created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GeneralResponse.class),
                    examples = @ExampleObject(
                            name = "CreateItemExample",
                            value = "{\"code\":\"201\",\"message\":\"Item created successfully\",\"data\":{\"id\":2,\"createdAt\":\"2025-10-25T15:52:35.28909\",\"updatedAt\":\"2025-10-25T15:52:35.289154\",\"createdBy\":\"SYSTEM\",\"updatedBy\":null,\"name\":\"T-Shirt\",\"description\":\"Premium cotton T-shirt with multiple color and size options\",\"variants\":[{\"id\":1,\"createdAt\":\"2025-10-25T15:52:35.293303\",\"updatedAt\":\"2025-10-25T15:52:35.29332\",\"createdBy\":\"SYSTEM\",\"updatedBy\":null,\"color\":\"Black\",\"size\":\"M\",\"price\":150000,\"stock\":25},{\"id\":2,\"createdAt\":\"2025-10-25T15:52:35.296142\",\"updatedAt\":\"2025-10-25T15:52:35.296158\",\"createdBy\":\"SYSTEM\",\"updatedBy\":null,\"color\":\"White\",\"size\":\"L\",\"price\":150000,\"stock\":30},{\"id\":3,\"createdAt\":\"2025-10-25T15:52:35.296857\",\"updatedAt\":\"2025-10-25T15:52:35.296867\",\"createdBy\":\"SYSTEM\",\"updatedBy\":null,\"color\":\"Blue\",\"size\":\"XL\",\"price\":160000,\"stock\":20}]}}"
                    )))
    @PostMapping
    public ResponseEntity<GeneralResponse<Item>> addItem(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Item to create",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Item.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"name\": \"T-Shirt\",\n" +
                                    "  \"description\": \"Premium cotton T-shirt with multiple color and size options\",\n" +
                                    "  \"variants\": [\n" +
                                    "  { \"color\": \"Black\", \"size\": \"M\", \"price\": 150000.0, \"stock\": 25 },\n" +
                                    "  { \"color\": \"White\", \"size\": \"L\", \"price\": 150000.0, \"stock\": 30 },\n" +
                                    "  { \"color\": \"Blue\", \"size\": \"XL\", \"price\": 160000.0, \"stock\": 20 }\n" +
                                    "]\n" +
                                    "}\n")
                    ))
            @RequestBody Item item) {
        log.info("incoming add item request {}", JsonUtil.toJson(item));
        Item savedItem = itemService.saveItem(item);
        return ResponseUtil.created("Item created successfully", savedItem);
    }

    @Operation(summary = "Delete item by ID", description = "Delete an item by its ID")
    @ApiResponse(responseCode = "200", description = "Item deleted successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GeneralResponse.class)))
    @ApiResponse(responseCode = "404", description = "Item not found",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GeneralResponse.class)))
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteItem(
            @Parameter(description = "ID of the item to delete", required = true)
            @PathVariable Long id) {
        log.info("incoming delete item request {}", id);
        itemService.deleteItem(id);
        return ResponseUtil.deleted("Item deleted successfully");
    }
}

