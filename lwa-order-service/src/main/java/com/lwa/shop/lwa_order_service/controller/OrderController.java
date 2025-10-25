package com.lwa.shop.lwa_order_service.controller;

import com.lwa.shop.lwa_order_service.model.GeneralResponse;
import com.lwa.shop.lwa_order_service.model.request.OrderRequestDTO;
import com.lwa.shop.lwa_order_service.model.response.OrderResponseDTO;
import com.lwa.shop.lwa_order_service.service.OrderService;
import com.lwa.shop.lwa_order_service.service.ServiceHandler;
import com.lwa.shop.lwa_order_service.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order API", description = "Operations for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all orders", description = "Fetch all orders from the database")
    @GetMapping
    public ResponseEntity<GeneralResponse<List<OrderResponseDTO>>> getAllOrders() {
        log.info("==== START getAllOrders request ====");
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        log.info("==== FINISHED getAllOrders request, total orders: {} ====", orders.size());
        return ResponseUtil.success("Fetched all orders", orders);
    }

    @Operation(summary = "Get order by ID", description = "Fetch a single order by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<OrderResponseDTO>> getOrder(
            @Parameter(description = "ID of the order", required = true)
            @PathVariable Long id) {
        log.info("==== START getOrder request for id: {} ====", id);
        OrderResponseDTO order = orderService.getOrder(id);
        log.info("==== FINISHED getOrder request for id: {} ====", id);
        return ResponseUtil.success("Fetched order", order);
    }

    @Operation(summary = "Create a new order", description = "Create a new order with customer and items")
    @PostMapping
    public ResponseEntity<GeneralResponse<OrderResponseDTO>> addOrder(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order to create", required = true,
                    content = @Content(schema = @Schema(implementation = OrderRequestDTO.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"customerName\": \"Raymond\",\n" +
                                    "  \"orderItems\": [{\"variantId\":1, \"quantity\":2}]\n" +
                                    "}")
                    )
            )
            @RequestBody OrderRequestDTO dto) {
        log.info("==== START addOrder request for customer: {} ====", dto.getCustomerName());
        return ServiceHandler.handle(() -> {
            OrderResponseDTO savedOrder = orderService.saveOrder(dto);
            log.info("==== SUCCESS addOrder for customer: {}, orderId: {} ====", dto.getCustomerName(), savedOrder.getId());
            return ResponseUtil.created("Order created successfully", savedOrder);
        });
    }

    @Operation(summary = "Mark order as completed", description = "Change order status to COMPLETED")
    @PutMapping("/{id}/complete")
    public ResponseEntity<GeneralResponse<OrderResponseDTO>> completeOrder(
            @Parameter(description = "ID of the order to complete", required = true)
            @PathVariable Long id) {
        log.info("==== START completeOrder request for id: {} ====", id);
        OrderResponseDTO completedOrder = orderService.completeOrder(id);
        log.info("==== FINISHED completeOrder request for id: {}, status: {} ====", id, completedOrder.getStatus());
        return ResponseUtil.success("Order completed", completedOrder);
    }

    @Operation(summary = "Delete order by ID", description = "Delete an order by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteOrder(
            @Parameter(description = "ID of the order to delete", required = true)
            @PathVariable Long id) {
        log.info("==== START deleteOrder request for id: {} ====", id);
        orderService.deleteOrder(id);
        log.info("==== FINISHED deleteOrder request for id: {} ====", id);
        return ResponseUtil.deleted("Order deleted");
    }
}
