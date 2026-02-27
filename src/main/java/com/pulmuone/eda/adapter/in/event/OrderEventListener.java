package com.pulmuone.eda.adapter.in.event;

import com.pulmuone.eda.application.port.out.LoadOrderPort;
import com.pulmuone.eda.application.port.out.SaveOrderPort;
import com.pulmuone.eda.domain.Order;
import com.pulmuone.eda.domain.event.PointDeductedEvent;
import com.pulmuone.eda.domain.event.PointFailedEvent;
import com.pulmuone.eda.domain.event.StockFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final LoadOrderPort loadOrderPort;
    private final SaveOrderPort saveOrderPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPointDeducted(PointDeductedEvent event) {
        log.info("[Order Service] Subscribed PointDeductedEvent for Order: {}", event.getOrderNumber());
        updateOrderStatus(event.getOrderNumber(), "COMPLETED");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onStockFailed(StockFailedEvent event) {
        log.error("[Order Service] Subscribed StockFailedEvent for Order: {}. Reason: {}", event.getOrderNumber(), event.getReason());
        updateOrderStatus(event.getOrderNumber(), "CANCELLED");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPointFailed(PointFailedEvent event) {
        log.error("[Order Service] Subscribed PointFailedEvent for Order: {}. Reason: {}", event.getOrderNumber(), event.getReason());
        // 2단계 11: 적립금 실패 시 보상 트랜잭션 트리거 (여기서는 우선 주문 취소)
        updateOrderStatus(event.getOrderNumber(), "CANCELLED");
        
        // TODO: 실제 분산 환경이라면 여기서 '재고 복구(Stock Restore)' 이벤트를 발행해야 함
    }

    private void updateOrderStatus(String orderNumber, String status) {
        Order order = loadOrderPort.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderNumber));

        if ("COMPLETED".equals(status)) {
            order.complete();
            log.info("[Order Service] Order {} status updated to COMPLETED.", orderNumber);
        } else if ("CANCELLED".equals(status)) {
            order.cancel();
            log.info("[Order Service] Order {} status updated to CANCELLED.", orderNumber);
        }

        saveOrderPort.save(order);
    }
}
