package com.pulmuone.eda.adapter.out.stock;

import com.pulmuone.eda.application.port.out.DeductStockPort;
import com.pulmuone.eda.domain.StockShortageException;
import com.pulmuone.eda.domain.event.OrderCreatedEvent;
import com.pulmuone.eda.domain.event.StockDeductedEvent;
import com.pulmuone.eda.domain.event.StockFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final DeductStockPort deductStockPort;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[Stock Service] Subscribed OrderCreatedEvent for Order: {}", event.getOrderNumber());

        try {
            // 2단계 9: 재고 차감 시도
            deductStockPort.deduct(event.getProductId(), event.getQuantity());

            // 성공 시 다음 단계 이벤트 발행
            log.info("[Stock Service] Stock deducted successfully for Order: {}", event.getOrderNumber());
            eventPublisher.publishEvent(new StockDeductedEvent(
                    event.getOrderNumber(),
                    event.getProductId(),
                    event.getQuantity()
            ));

        } catch (StockShortageException e) {
            // 실패 시 실패 이벤트 발행 (보상 트랜잭션 트리거)
            log.error("[Stock Service] Stock deduction failed for Order: {}. Reason: {}", event.getOrderNumber(), e.getMessage());
            eventPublisher.publishEvent(new StockFailedEvent(event.getOrderNumber(), e.getMessage()));
        }
    }
}
