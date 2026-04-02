package com.pulmuone.eda.common.domain.exception;

public class StockShortageException extends RuntimeException {
    public StockShortageException(String message) {
        super(message);
    }
}
