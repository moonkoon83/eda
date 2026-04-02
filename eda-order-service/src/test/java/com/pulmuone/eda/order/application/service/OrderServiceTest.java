package com.pulmuone.eda.order.application.service;

import com.pulmuone.eda.common.domain.event.OrderCreatedEvent;
import com.pulmuone.eda.order.application.port.out.LoadOrderPort;
import com.pulmuone.eda.order.application.port.out.SaveOrderPort;
import com.pulmuone.eda.order.domain.Order;
import com.pulmuone.eda.order.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private SaveOrderPort saveOrderPort;

    @Mock
    private LoadOrderPort loadOrderPort;

    @Mock
    private OrderNumberGenerator orderNumberGenerator;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        // 명시적으로 생성자 주입
        orderService = new OrderService(orderNumberGenerator, saveOrderPort, loadOrderPort, kafkaTemplate);
    }

    @Test
    @DisplayName("신규 주문을 생성하면, 주문이 저장되고 'OrderCreatedEvent'가 Kafka로 발행되어야 한다")
    void createOrder_ShouldSaveOrderAndPublishEvent() {
        // given
        String productId = "test-product-id";
        int quantity = 10;
        String fakeOrderNumber = "202602031122330001";
        Order newOrder = Order.create(fakeOrderNumber, productId, quantity);

        when(orderNumberGenerator.generate()).thenReturn(fakeOrderNumber);
        when(saveOrderPort.save(any(Order.class))).thenReturn(newOrder);

        // when
        Order createdOrder = orderService.createOrder(productId, quantity);

        // then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getOrderNumber()).isEqualTo(fakeOrderNumber);
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // verify
        verify(orderNumberGenerator, times(1)).generate();
        verify(saveOrderPort, times(1)).save(any(Order.class));
        
        // Kafka 메시지 발행 검증
        verify(kafkaTemplate, times(1)).send(eq("order.created"), any(OrderCreatedEvent.class));
    }
}
