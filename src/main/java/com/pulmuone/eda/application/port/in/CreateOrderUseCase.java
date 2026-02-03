package com.pulmuone.eda.application.port.in;

import com.pulmuone.eda.domain.Order;

public interface CreateOrderUseCase {
    Order createOrder(String productId, Integer quantity);
}
