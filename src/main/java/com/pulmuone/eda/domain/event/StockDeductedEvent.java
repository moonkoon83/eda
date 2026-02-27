package com.pulmuone.eda.domain.event;

import lombok.Getter;

@Getter
public class StockDeductedEvent extends OrderEvent {
    private final String productId;
    private final Integer quantity;

    public StockDeductedEvent(String orderNumber, String productId, Integer quantity) {
        super(orderNumber);
        this.productId = productId;
        this.quantity = quantity;
    }
}
