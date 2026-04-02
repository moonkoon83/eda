package com.pulmuone.eda.order.application.port.out;

import com.pulmuone.eda.order.domain.Order;
import java.util.List;
import java.util.Optional;

public interface LoadOrderPort {
    boolean existsByOrderNumber(String orderNumber);
    Optional<Order> findById(Long id);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findAll();
}
