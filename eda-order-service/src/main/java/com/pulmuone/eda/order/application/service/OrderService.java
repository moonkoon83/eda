package com.pulmuone.eda.order.application.service;

import com.pulmuone.eda.common.domain.event.OrderCreatedEvent;
import com.pulmuone.eda.order.application.port.in.CreateOrderUseCase;
import com.pulmuone.eda.order.application.port.out.LoadOrderPort;
import com.pulmuone.eda.order.application.port.out.SaveOrderPort;
import com.pulmuone.eda.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class OrderService implements CreateOrderUseCase {

    private final OrderNumberGenerator orderNumberGenerator;
    private final SaveOrderPort saveOrderPort;
    private final LoadOrderPort loadOrderPort;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "order.created";

    @Transactional
    @Override
    public Order createOrder(String productId, Integer quantity) {
        String orderNumber = orderNumberGenerator.generate();
        log.info("[Order Service] Creating new order: {}, product: {}", orderNumber, productId);
        
        Order order = Order.create(orderNumber, productId, quantity);

        // 2단계 8: 주문을 PENDING 상태로 영속화
        Order savedOrder = saveOrderPort.save(order);
        log.info("[Order Service] Order {} saved to DB (id: {})", orderNumber, savedOrder.getId());

        // 2단계 8: 주문 생성됨 메시지 발행 (Saga 시작)
        kafkaTemplate.send(TOPIC, new OrderCreatedEvent(orderNumber, productId, quantity));
        log.info("[Order Service] OrderCreatedEvent issued to Kafka topic {} for Order: {}", TOPIC, orderNumber);

        return savedOrder;
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return loadOrderPort.findById(id);
    }

    @Override
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return loadOrderPort.findByOrderNumber(orderNumber);
    }

    @Override
    public List<Order> getAllOrders() {
        return loadOrderPort.findAll();
    }
}
