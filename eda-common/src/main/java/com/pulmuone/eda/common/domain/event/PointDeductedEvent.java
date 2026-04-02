package com.pulmuone.eda.common.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointDeductedEvent extends OrderEvent {
    public PointDeductedEvent(String orderNumber) {
        super(orderNumber);
    }
}
