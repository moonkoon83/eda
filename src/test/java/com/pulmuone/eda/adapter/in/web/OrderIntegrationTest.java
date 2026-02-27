package com.pulmuone.eda.adapter.in.web;

import com.pulmuone.eda.application.port.in.CreateOrderUseCase;
import com.pulmuone.eda.application.port.out.LoadOrderPort;
import com.pulmuone.eda.domain.Order;
import com.pulmuone.eda.domain.OrderStatus;
import com.pulmuone.eda.domain.PointShortageException;
import com.pulmuone.eda.domain.StockShortageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("local")
class OrderIntegrationTest {

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private LoadOrderPort loadOrderPort;

    @Test
    @DisplayName("통합 테스트: Saga 패턴을 통한 주문 생성이 성공하면 'COMPLETED' 상태여야 한다")
    void createOrder_ShouldPersistWithCompletedStatusViaSaga() {
        // given
        String productId = "saga-success-product";
        int quantity = 3;

        // when
        Order createdOrder = createOrderUseCase.createOrder(productId, quantity);

        // then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING); // 생성 직후는 PENDING

        // 이벤트가 모두 처리된 후 상태 확인 (현재는 동기식 @EventListener이므로 즉시 반영됨)
        Order finalOrder = loadOrderPort.findById(createdOrder.getId()).orElseThrow();
        assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("통합 테스트: 재고 부족 시 Saga를 통해 주문 상태가 'CANCELLED'로 변경되어야 한다")
    void createOrder_ShouldBecomeCancelledOnStockShortage() {
        // given
        String productId = "shortage-product";
        int quantity = 100; // 수량이 100 이상이면 StockShortageException 발생

        // when
        Order createdOrder = createOrderUseCase.createOrder(productId, quantity);

        // then (예외가 던져지지 않고 주문은 생성됨)
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // 이벤트 처리 후 CANCELLED 상태 확인
        Order finalOrder = loadOrderPort.findById(createdOrder.getId()).orElseThrow();
        assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("통합 테스트: 적립금 부족 시 Saga를 통해 주문 상태가 'CANCELLED'로 변경되어야 한다")
    void createOrder_ShouldBecomeCancelledOnPointShortage() {
        // given
        String productId = "shortage-product";
        int quantity = 50; // 수량이 50 이상이면 PointShortageException 발생

        // when
        Order createdOrder = createOrderUseCase.createOrder(productId, quantity);

        // then
        assertThat(createdOrder).isNotNull();

        // 이벤트 처리 후 CANCELLED 상태 확인
        Order finalOrder = loadOrderPort.findById(createdOrder.getId()).orElseThrow();
        assertThat(finalOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}
