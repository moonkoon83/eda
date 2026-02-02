package com.pulmuone.eda.service;

import com.pulmuone.eda.domain.Order;
import com.pulmuone.eda.domain.OrderStatus;
import com.pulmuone.eda.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderNumberGenerator orderNumberGenerator;

    @Transactional
    public Order createOrder(String productId, Integer quantity) {
        String orderNumber = orderNumberGenerator.generate();
        Order order = new Order(orderNumber, productId, quantity);
        return orderRepository.save(order);
    }
}

