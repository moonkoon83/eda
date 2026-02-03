package com.pulmuone.eda.application.port.out;

import com.pulmuone.eda.domain.Order;
import java.util.Optional;

public interface LoadOrderPort {
    boolean existsByOrderNumber(String orderNumber);
    Optional<Order> findById(Long id);
}
