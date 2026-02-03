package com.pulmuone.eda.application.port.out;

import com.pulmuone.eda.domain.Order;

public interface SaveOrderPort {
    Order save(Order order);
}
