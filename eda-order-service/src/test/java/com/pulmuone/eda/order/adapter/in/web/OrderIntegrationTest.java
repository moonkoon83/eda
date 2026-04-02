package com.pulmuone.eda.order.adapter.in.web;

import com.pulmuone.eda.common.domain.event.PointDeductedEvent;
import com.pulmuone.eda.common.domain.event.PointFailedEvent;
import com.pulmuone.eda.common.domain.event.StockDeductedEvent;
import com.pulmuone.eda.common.domain.event.StockFailedEvent;
import com.pulmuone.eda.order.application.port.in.CreateOrderUseCase;
import com.pulmuone.eda.order.application.port.out.LoadOrderPort;
import com.pulmuone.eda.order.domain.Order;
import com.pulmuone.eda.order.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("local")
class OrderIntegrationTest {

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private LoadOrderPort loadOrderPort;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @DisplayName("통합 테스트: Saga 패턴을 통한 주문 생성이 성공하면 'COMPLETED' 상태여야 한다")
    void createOrder_ShouldPersistWithCompletedStatusViaSaga() {
        // given
        String productId = "saga-success-product";
        int quantity = 3;

        // when
        Order createdOrder = createOrderUseCase.createOrder(productId, quantity);
        String orderNumber = createdOrder.getOrderNumber();

        // then: 생성 직후는 PENDING
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // 가상으로 외부 서비스(Stock, Point)의 성공 응답 발행
        kafkaTemplate.send("stock.deducted", new StockDeductedEvent(orderNumber, productId, quantity));
        kafkaTemplate.send("point.deducted", new PointDeductedEvent(orderNumber));

        // 비동기 처리 대기 및 최종 상태 확인
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Order finalOrder = loadOrderPort.findByOrderNumber(orderNumber).orElseThrow();
            assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        });
    }

    @Test
    @DisplayName("통합 테스트: 재고 부족 시 Saga를 통해 주문 상태가 'CANCELLED'로 변경되어야 한다")
    void createOrder_ShouldBecomeCancelledOnStockShortage() {
        // given
        String productId = "shortage-product";
        int quantity = 100;

        // when
        Order createdOrder = createOrderUseCase.createOrder(productId, quantity);
        String orderNumber = createdOrder.getOrderNumber();

        // then
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // 가상으로 외부 서비스(Stock)의 실패 응답 발행
        kafkaTemplate.send("stock.failed", new StockFailedEvent(orderNumber, "Stock shortage"));

        // 비동기 처리 대기 및 최종 상태 확인
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Order finalOrder = loadOrderPort.findByOrderNumber(orderNumber).orElseThrow();
            assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        });
    }

    @Test
    @DisplayName("통합 테스트: 적립금 부족 시 Saga를 통해 주문 상태가 'CANCELLED'로 변경되어야 한다")
    void createOrder_ShouldBecomeCancelledOnPointShortage() {
        // given
        String productId = "point-shortage-product";
        int quantity = 50;

        // when
        Order createdOrder = createOrderUseCase.createOrder(productId, quantity);
        String orderNumber = createdOrder.getOrderNumber();

        // then
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // 가상으로 외부 서비스(Point)의 실패 응답 발행
        kafkaTemplate.send("point.failed", new PointFailedEvent(orderNumber, "Point shortage"));

        // 비동기 처리 대기 및 최종 상태 확인
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Order finalOrder = loadOrderPort.findByOrderNumber(orderNumber).orElseThrow();
            assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        });
    }
}
