package com.pulmuone.eda.adapter.in.web;

import com.pulmuone.eda.application.port.in.CreateOrderUseCase;
import com.pulmuone.eda.application.port.out.LoadOrderPort;
import com.pulmuone.eda.application.port.out.SaveOrderPort;
import com.pulmuone.eda.application.service.OrderNumberGenerator;
import com.pulmuone.eda.domain.Order;
import com.pulmuone.eda.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class OrderIntegrationTest {

    @TestConfiguration
    static class OrderIntegrationTestConfig {
        @Bean
        @Primary
        public OrderNumberGenerator spiedOrderNumberGenerator(LoadOrderPort loadOrderPort) {
            return spy(new OrderNumberGenerator(loadOrderPort));
        }
    }

    @Autowired
    private CreateOrderUseCase createOrderUseCase;

    @Autowired
    private LoadOrderPort loadOrderPort;

    @Autowired
    private SaveOrderPort saveOrderPort;

    @Autowired
    private OrderNumberGenerator orderNumberGenerator; // This will be the spy from TestConfig

    @Test
    @DisplayName("통합 테스트: 신규 주문 생성 시 주문 번호가 부여되고 DB에 저장되어야 한다")
    void createOrder_ShouldPersistOrderWithOrderNumber() {
        // given
        String productId = "integration-product-id";
        int quantity = 5;

        // when
        Order createdOrder = createOrderUseCase.createOrder(productId, quantity);

        // then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getOrderNumber()).isNotNull().hasSize(18);
        assertThat(createdOrder.getProductId()).isEqualTo(productId);
        assertThat(createdOrder.getQuantity()).isEqualTo(quantity);
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        Order foundOrder = loadOrderPort.findById(createdOrder.getId()).orElse(null);
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getOrderNumber()).isEqualTo(createdOrder.getOrderNumber());
    }

    @Test
    @DisplayName("통합 테스트: 주문 번호 중복 시 재시도하여 유니크한 번호를 생성해야 한다")
    void createOrder_ShouldRetryGeneratingNumber_WhenDuplicateExists() {
        // given
        String duplicateOrderNumber = "DUPLICATE-1234";
        String uniqueOrderNumber = "UNIQUE-5678";

        saveOrderPort.save(new Order(duplicateOrderNumber, "pre-existing-product", 1));

        doReturn(duplicateOrderNumber)
                .doReturn(uniqueOrderNumber)
                .when(orderNumberGenerator).generate();

        // when
        Order createdOrder = createOrderUseCase.createOrder("new-product", 10);

        // then
        assertThat(createdOrder.getOrderNumber()).isEqualTo(uniqueOrderNumber);
        verify(orderNumberGenerator, times(2)).generate();
    }
}
