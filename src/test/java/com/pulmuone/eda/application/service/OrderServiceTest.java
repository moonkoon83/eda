package com.pulmuone.eda.application.service;

import com.pulmuone.eda.application.port.out.SaveOrderPort;
import com.pulmuone.eda.domain.Order;
import com.pulmuone.eda.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private SaveOrderPort saveOrderPort;

    @Mock
    private OrderNumberGenerator orderNumberGenerator;

    @Test
    @DisplayName("신규 주문을 생성하면, 주문 번호가 부여되고 'PENDING' 상태여야 한다")
    void createOrder_ShouldCreateOrderWithNumberInPendingState() {
        // given
        String productId = "test-product-id";
        int quantity = 10;
        String fakeOrderNumber = "202602031122330001";
        Order newOrder = new Order(fakeOrderNumber, productId, quantity);

        when(orderNumberGenerator.generate()).thenReturn(fakeOrderNumber);
        when(saveOrderPort.save(any(Order.class))).thenReturn(newOrder);

        // when
        Order createdOrder = orderService.createOrder(productId, quantity);

        // then
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getOrderNumber()).isEqualTo(fakeOrderNumber);
        assertThat(createdOrder.getProductId()).isEqualTo(productId);
        assertThat(createdOrder.getQuantity()).isEqualTo(quantity);
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);

        // verify interactions
        verify(orderNumberGenerator, times(1)).generate();
        verify(saveOrderPort, times(1)).save(any(Order.class));
    }
}
