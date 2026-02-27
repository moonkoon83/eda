package com.pulmuone.eda.domain.event;

import lombok.Getter;

@Getter
public class PointFailedEvent extends OrderEvent {
    private final String reason;

    public PointFailedEvent(String orderNumber, String reason) {
        super(orderNumber);
        this.reason = reason;
    }
}
