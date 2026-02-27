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
@Transactional
@ActiveProfiles("local")
class OrderIntegrationTest {

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private LoadOrderPort loadOrderPort;

    @Test
    @DisplayName("통합 테스트: MySQL에 실제 주문을 생성하고 데이터를 확인한다")
    void createOrder_ShouldPersistInMySQL() {
        // given
        String productId = "mysql-test-product";
        int quantity = 3;

        // when
        Order createdOrder = createOrderUseCase.createOrder(productId, quantity);

        // then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getOrderNumber()).isNotNull().hasSize(18); // yyyyMMddHHmmss + 4digits
        assertThat(createdOrder.getProductId()).isEqualTo(productId);
        assertThat(createdOrder.getQuantity()).isEqualTo(quantity);
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

        // DB에서 직접 조회하여 검증
        Order foundOrder = loadOrderPort.findById(createdOrder.getId()).orElse(null);
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getOrderNumber()).isEqualTo(createdOrder.getOrderNumber());
        assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("통합 테스트: 재고 부족 시 주문 생성이 실패하고 트랜잭션이 롤백되어야 한다")
    void createOrder_ShouldRollbackOnStockShortage() {
        // given
        String productId = "shortage-product";
        int quantity = 100; // 수량이 100 이상이면 StockShortageException 발생

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.createOrder(productId, quantity))
                .isExactlyInstanceOf(StockShortageException.class);
    }

    @Test
    @DisplayName("통합 테스트: 적립금 부족 시 주문 생성이 실패하고 트랜잭션이 롤백되어야 한다")
    void createOrder_ShouldRollbackOnPointShortage() {
        // given
        String productId = "shortage-product";
        int quantity = 50; // 수량이 50 이상이면 PointShortageException 발생

        // when & then
        assertThatThrownBy(() -> createOrderUseCase.createOrder(productId, quantity))
                .isExactlyInstanceOf(PointShortageException.class);
    }
}
