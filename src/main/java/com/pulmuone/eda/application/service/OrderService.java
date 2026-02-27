package com.pulmuone.eda.application.service;

import com.pulmuone.eda.application.port.in.CreateOrderUseCase;
import com.pulmuone.eda.application.port.out.SaveOrderPort;
import com.pulmuone.eda.domain.Order;
import com.pulmuone.eda.domain.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class OrderService implements CreateOrderUseCase {

    private final OrderNumberGenerator orderNumberGenerator;
    private final SaveOrderPort saveOrderPort;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public Order createOrder(String productId, Integer quantity) {
        String orderNumber = orderNumberGenerator.generate();
        Order order = Order.create(orderNumber, productId, quantity);

        // 2단계 8: 주문을 PENDING 상태로 영속화
        Order savedOrder = saveOrderPort.save(order);

        // 2단계 8: 주문 생성됨 이벤트 발행 (Saga 시작)
        eventPublisher.publishEvent(new OrderCreatedEvent(orderNumber, productId, quantity));

        return savedOrder;
    }
}
