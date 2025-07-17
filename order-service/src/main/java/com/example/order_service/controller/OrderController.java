package com.example.order_service.controller;

import com.example.order_service.application.dto.request.OrderRequest;
import com.example.order_service.application.dto.response.OrderResponse;
import com.example.order_service.domain.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orderService")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody OrderRequest orderRequest, HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");

        String userEmail = (String) request.getAttribute("userEmail");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        OrderResponse response = orderService.createOrder(orderRequest, userId, userEmail);

        log.info("New order with Id: {}", response.orderId());
        return ResponseEntity
                .accepted()
                .location(URI.create("/api/orderService/placeOrder" + response.orderId()))
                .body(response);
    }

    @GetMapping("/orders/")
    public ResponseEntity<?> getOrdersByUserId(HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);

        if(orders.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId, HttpServletRequest request) {

        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}