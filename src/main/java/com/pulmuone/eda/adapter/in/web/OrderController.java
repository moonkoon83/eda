package com.pulmuone.eda.adapter.in.web;

import com.pulmuone.eda.adapter.in.web.dto.CreateOrderRequest;
import com.pulmuone.eda.application.port.in.CreateOrderUseCase;
import com.pulmuone.eda.domain.Order;
import jakarta.validation.Valid;
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

    private final CreateOrderUseCase createOrderUseCase;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order createdOrder = createOrderUseCase.createOrder(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(createdOrder);
    }
}
