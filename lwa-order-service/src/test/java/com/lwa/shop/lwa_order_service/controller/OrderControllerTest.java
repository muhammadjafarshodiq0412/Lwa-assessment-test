package com.lwa.shop.lwa_order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lwa.shop.lwa_order_service.model.request.OrderItemRequestDTO;
import com.lwa.shop.lwa_order_service.model.request.OrderRequestDTO;
import com.lwa.shop.lwa_order_service.model.response.OrderResponseDTO;
import com.lwa.shop.lwa_order_service.service.OrderService;
import com.lwa.shop.lwa_order_service.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.concurrent.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

    private MockMvc mockMvc;
    private OrderService orderService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        orderService = Mockito.mock(OrderService.class);
        objectMapper = new ObjectMapper();

        OrderController controller = new OrderController(orderService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void addOrder_success() throws Exception {
        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setVariantId(1L);
        itemDto.setQuantity(2);

        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setCustomerName("John Doe");
        requestDTO.setOrderItems(List.of(itemDto));

        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCustomerName("John Doe");
        responseDTO.setTotalAmount(200.0);

        when(orderService.saveOrder(any(OrderRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.customerName").value("John Doe"))
                .andExpect(jsonPath("$.data.totalAmount").value(200.0));

        verify(orderService, times(1)).saveOrder(any(OrderRequestDTO.class));
    }

    @Test
    void concurrentOrderPlacement() throws InterruptedException, ExecutionException {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        OrderItemRequestDTO itemDto = new OrderItemRequestDTO();
        itemDto.setVariantId(1L);
        itemDto.setQuantity(1);

        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setCustomerName("Concurrent User");
        requestDTO.setOrderItems(List.of(itemDto));

        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCustomerName("Concurrent User");
        responseDTO.setTotalAmount(100.0);

        when(orderService.saveOrder(any(OrderRequestDTO.class))).thenReturn(responseDTO);

        Callable<OrderResponseDTO> task = () -> orderService.saveOrder(requestDTO);
        List<Future<OrderResponseDTO>> futures = executor.invokeAll(List.of(task, task, task, task, task));

        for (Future<OrderResponseDTO> future : futures) {
            assertNotNull(future.get());
        }

        verify(orderService, times(threadCount)).saveOrder(any(OrderRequestDTO.class));

        executor.shutdown();
    }

    @Test
    void getAllOrders_success() throws Exception {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCustomerName("John Doe");

        when(orderService.getAllOrders()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].customerName").value("John Doe"));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getOrder_success() throws Exception {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCustomerName("John Doe");

        when(orderService.getOrder(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerName").value("John Doe"));

        verify(orderService, times(1)).getOrder(1L);
    }

    @Test
    void completeOrder_success() throws Exception {
        OrderResponseDTO responseDTO = new OrderResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setCustomerName("John Doe");
        responseDTO.setStatus("COMPLETED");

        when(orderService.completeOrder(1L)).thenReturn(responseDTO);

        mockMvc.perform(put("/orders/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        verify(orderService, times(1)).completeOrder(1L);
    }

    @Test
    void deleteOrder_success() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isOk());

        verify(orderService, times(1)).deleteOrder(1L);
    }
}
