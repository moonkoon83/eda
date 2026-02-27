package com.pulmuone.eda.application.port.out;

public interface DeductStockPort {
    void deduct(String productId, Integer quantity);
}
