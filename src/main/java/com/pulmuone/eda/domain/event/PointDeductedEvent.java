package com.pulmuone.eda.domain.event;

import lombok.Getter;

@Getter
public class PointDeductedEvent extends OrderEvent {
    public PointDeductedEvent(String orderNumber) {
        super(orderNumber);
    }
}
