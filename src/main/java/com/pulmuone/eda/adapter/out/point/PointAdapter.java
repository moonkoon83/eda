package com.pulmuone.eda.adapter.out.point;

import com.pulmuone.eda.application.port.out.DeductPointPort;
import com.pulmuone.eda.domain.PointShortageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PointAdapter implements DeductPointPort {

    @Override
    public void deduct(String productId, Integer quantity) {
        // 시뮬레이션: 수량이 50 이상이면 적립금 부족 발생
        if (quantity >= 50) {
            log.error("[Point Service] Point shortage for Product {}: quantity {}", productId, quantity);
            throw new PointShortageException("Insufficient user points.");
        }
        
        log.info("[Point Service] User points deducted for Product {} (Quantity: {}).", productId, quantity);
    }
}
