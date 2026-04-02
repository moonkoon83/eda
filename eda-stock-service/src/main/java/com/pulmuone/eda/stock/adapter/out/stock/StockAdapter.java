package com.pulmuone.eda.stock.adapter.out.stock;

import com.pulmuone.eda.common.domain.exception.StockShortageException;
import com.pulmuone.eda.stock.application.port.out.DeductStockPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockAdapter implements DeductStockPort {

    @Override
    public void deduct(String productId, Integer quantity) {
        // 시뮬레이션: 수량이 100 이상이면 재고 부족 발생
        if (quantity >= 100) {
            log.error("[Stock Service] Stock shortage for Product {}: requested {}", productId, quantity);
            throw new StockShortageException("Insufficient stock for product: " + productId);
        }
        
        log.info("[Stock Service] Product {} deducted by {} units.", productId, quantity);
    }
}
