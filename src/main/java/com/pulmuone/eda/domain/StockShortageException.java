package com.pulmuone.eda.domain;

public class StockShortageException extends RuntimeException {
    public StockShortageException(String message) {
        super(message);
    }
}
