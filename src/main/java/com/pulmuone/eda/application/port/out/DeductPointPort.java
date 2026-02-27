package com.pulmuone.eda.application.port.out;

public interface DeductPointPort {
    void deduct(String productId, Integer quantity);
}
