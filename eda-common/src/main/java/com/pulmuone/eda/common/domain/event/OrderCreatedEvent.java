package com.pulmuone.eda.common.domain.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCreatedEvent extends OrderEvent {
    private String productId;
    private Integer quantity;

    public OrderCreatedEvent(String orderNumber, String productId, Integer quantity) {
        super(orderNumber);
        this.productId = productId;
        this.quantity = quantity;
    }
}
