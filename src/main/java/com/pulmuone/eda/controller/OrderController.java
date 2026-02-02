package com.pulmuone.eda.controller;

import com.pulmuone.eda.controller.dto.CreateOrderRequest;
import com.pulmuone.eda.domain.Order;
import com.pulmuone.eda.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        Order createdOrder = orderService.createOrder(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(createdOrder);
    }
}
