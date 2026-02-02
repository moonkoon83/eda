package com.pulmuone.eda.service;

import com.pulmuone.eda.domain.Order;
import com.pulmuone.eda.domain.OrderStatus;
import com.pulmuone.eda.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional // 테스트 후 롤백하여 DB 상태를 깨끗하게 유지
class OrderIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @SpyBean // 실제 OrderNumberGenerator 객체를 사용하되, 일부 동작을 제어
    private OrderNumberGenerator orderNumberGenerator;

    @Test
    @DisplayName("통합 테스트: 신규 주문 생성 시 주문 번호가 부여되고 DB에 저장되어야 한다")
    void createOrder_ShouldPersistOrderWithOrderNumber() {
        // given
        String productId = "integration-product-id";
        int quantity = 5;

        // when
        Order createdOrder = orderService.createOrder(productId, quantity);

        // then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getId()).isNotNull();
        assertThat(createdOrder.getOrderNumber()).isNotNull().hasSize(18);
        assertThat(createdOrder.getProductId()).isEqualTo(productId);
        assertThat(createdOrder.getQuantity()).isEqualTo(quantity);
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // DB에서 실제로 조회하여 확인
        Order foundOrder = orderRepository.findById(createdOrder.getId()).orElse(null);
        assertThat(foundOrder).isNotNull();
        assertThat(foundOrder.getOrderNumber()).isEqualTo(createdOrder.getOrderNumber());
    }

    @Test
    @DisplayName("통합 테스트: 주문 번호 중복 시 재시도하여 유니크한 번호를 생성해야 한다")
    void createOrder_ShouldRetryGeneratingNumber_WhenDuplicateExists() {
        // given
        String duplicateOrderNumber = "DUPLICATE-1234";
        String uniqueOrderNumber = "UNIQUE-5678";

        // 1. DB에 중복될 주문을 미리 저장
        orderRepository.save(new Order(duplicateOrderNumber, "pre-existing-product", 1));

        // 2. SpyBean을 조작하여, 첫 번째 generate() 호출 시 중복된 번호를,
        //    두 번째 호출 시 유니크한 번호를 반환하도록 설정
        doReturn(duplicateOrderNumber)
                .doReturn(uniqueOrderNumber)
                .when(orderNumberGenerator).generate();

        // when
        // OrderService는 내부적으로 orderNumberGenerator.generate()를 호출함
        Order createdOrder = orderService.createOrder("new-product", 10);

        // then
        // 1. 최종적으로 생성된 주문의 번호가 유니크한 번호인지 확인
        assertThat(createdOrder.getOrderNumber()).isEqualTo(uniqueOrderNumber);

        // 2. generate() 메소드가 총 2번 호출되었는지 검증 (중복으로 1번, 성공으로 1번)
        verify(orderNumberGenerator, times(2)).generate();
    }
}
