package com.pulmuone.eda.order.adapter.in.web;

import com.pulmuone.eda.order.adapter.in.web.dto.CreateOrderRequest;
import com.pulmuone.eda.order.application.port.in.CreateOrderUseCase;
import com.pulmuone.eda.order.domain.Order;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(createOrderUseCase.getAllOrders());
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<Order> getOrderByOrderNumber(@PathVariable String orderNumber) {
        return createOrderUseCase.getOrderByOrderNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return createOrderUseCase.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
