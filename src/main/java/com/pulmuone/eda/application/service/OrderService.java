package com.pulmuone.eda.application.service;

import com.pulmuone.eda.application.port.in.CreateOrderUseCase;
import com.pulmuone.eda.application.port.out.DeductPointPort;
import com.pulmuone.eda.application.port.out.DeductStockPort;
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
    private final DeductStockPort deductStockPort;
    private final DeductPointPort deductPointPort;

    @Transactional
    @Override
    public Order createOrder(String productId, Integer quantity) {
        String orderNumber = orderNumberGenerator.generate();
        Order order = Order.create(orderNumber, productId, quantity);

        // 1단계 5-1: 동기식 재고 차감 호출
        deductStockPort.deduct(productId, quantity);

        // 1단계 5-2: 동기식 적립금 차감 호출
        deductPointPort.deduct(productId, quantity);

        // 1단계 6: 주문 완료 상태 변경
        order.complete();

        return saveOrderPort.save(order);
    }
}
