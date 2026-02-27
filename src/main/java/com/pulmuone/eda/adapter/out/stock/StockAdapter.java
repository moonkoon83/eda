package com.pulmuone.eda.adapter.out.stock;

import com.pulmuone.eda.application.port.out.DeductStockPort;
import com.pulmuone.eda.domain.StockShortageException;
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
