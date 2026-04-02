package com.pulmuone.eda.common.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointFailedEvent extends OrderEvent {
    private String reason;

    public PointFailedEvent(String orderNumber, String reason) {
        super(orderNumber);
        this.reason = reason;
    }
}
