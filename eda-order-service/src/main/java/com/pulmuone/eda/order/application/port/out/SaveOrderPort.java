package com.pulmuone.eda.order.application.port.out;

import com.pulmuone.eda.order.domain.Order;

public interface SaveOrderPort {
    Order save(Order order);
}
