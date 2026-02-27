package com.pulmuone.eda.domain.event;

import lombok.Getter;

@Getter
public class StockFailedEvent extends OrderEvent {
    private final String reason;

    public StockFailedEvent(String orderNumber, String reason) {
        super(orderNumber);
        this.reason = reason;
    }
}
