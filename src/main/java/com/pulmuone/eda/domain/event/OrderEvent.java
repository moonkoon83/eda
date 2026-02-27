package com.pulmuone.eda.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class OrderEvent {
    private final String orderNumber;
}
