package com.pulmuone.eda.common.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockFailedEvent extends OrderEvent {
    private String reason;

    public StockFailedEvent(String orderNumber, String reason) {
        super(orderNumber);
        this.reason = reason;
    }
}
