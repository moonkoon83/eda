package com.pulmuone.eda.application.service;

import com.pulmuone.eda.application.port.in.CreateOrderUseCase;
import com.pulmuone.eda.application.port.out.SaveOrderPort;
import com.pulmuone.eda.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class OrderService implements CreateOrderUseCase {

    private final OrderNumberGenerator orderNumberGenerator;
    private final SaveOrderPort saveOrderPort;

    @Transactional
    @Override
    public Order createOrder(String productId, Integer quantity) {
        String orderNumber = orderNumberGenerator.generate();
        Order order = new Order(orderNumber, productId, quantity);
        return saveOrderPort.save(order);
    }
}
