package com.pulmuone.eda.point.application.port.out;

public interface DeductPointPort {
    void deduct(String productId, Integer quantity);
}
