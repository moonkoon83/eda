package com.pulmuone.eda.adapter.out.point;

import com.pulmuone.eda.application.port.out.DeductPointPort;
import com.pulmuone.eda.domain.PointShortageException;
import com.pulmuone.eda.domain.event.PointDeductedEvent;
import com.pulmuone.eda.domain.event.PointFailedEvent;
import com.pulmuone.eda.domain.event.StockDeductedEvent;
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
public class PointEventListener {

    private final DeductPointPort deductPointPort;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onStockDeducted(StockDeductedEvent event) {
        log.info("[Point Service] Subscribed StockDeductedEvent for Order: {}", event.getOrderNumber());

        try {
            // 2단계 9: 적립금 차감 시도
            deductPointPort.deduct(event.getProductId(), event.getQuantity());

            // 성공 시 최종 완료 이벤트 발행
            log.info("[Point Service] Point deducted successfully for Order: {}", event.getOrderNumber());
            eventPublisher.publishEvent(new PointDeductedEvent(event.getOrderNumber()));

        } catch (PointShortageException e) {
            // 실패 시 실패 이벤트 발행 (보상 트랜잭션 트리거)
            log.error("[Point Service] Point deduction failed for Order: {}. Reason: {}", event.getOrderNumber(), e.getMessage());
            eventPublisher.publishEvent(new PointFailedEvent(event.getOrderNumber(), e.getMessage()));
        }
    }
}
