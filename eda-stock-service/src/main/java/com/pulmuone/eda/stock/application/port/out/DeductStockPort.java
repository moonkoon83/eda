package com.pulmuone.eda.stock.application.port.out;

public interface DeductStockPort {
    void deduct(String productId, Integer quantity);
}
