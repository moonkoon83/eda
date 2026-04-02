package com.pulmuone.eda.order.application.port.in;

import com.pulmuone.eda.order.domain.Order;
import java.util.List;
import java.util.Optional;

public interface CreateOrderUseCase {
    Order createOrder(String productId, Integer quantity);
    Optional<Order> getOrderById(Long id);
    Optional<Order> getOrderByOrderNumber(String orderNumber);
    List<Order> getAllOrders();
}
