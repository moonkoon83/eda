package com.pulmuone.eda.adapter.in.event;

import com.pulmuone.eda.application.port.out.LoadOrderPort;
import com.pulmuone.eda.application.port.out.SaveOrderPort;
import com.pulmuone.eda.domain.Order;
import com.pulmuone.eda.domain.event.PointDeductedEvent;
import com.pulmuone.eda.domain.event.PointFailedEvent;
import com.pulmuone.eda.domain.event.StockDeductedEvent;
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
    public void onStockDeducted(StockDeductedEvent event) {
        log.info("[Order Service] Subscribed StockDeductedEvent for Order: {}", event.getOrderNumber());
        updateStatus(event.getOrderNumber(), "STOCK_OK");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPointDeducted(PointDeductedEvent event) {
        log.info("[Order Service] Subscribed PointDeductedEvent for Order: {}", event.getOrderNumber());
        updateStatus(event.getOrderNumber(), "POINT_OK");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onStockFailed(StockFailedEvent event) {
        log.error("[Order Service] Subscribed StockFailedEvent for Order: {}. Reason: {}", event.getOrderNumber(), event.getReason());
        updateStatus(event.getOrderNumber(), "CANCELLED");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPointFailed(PointFailedEvent event) {
        log.error("[Order Service] Subscribed PointFailedEvent for Order: {}. Reason: {}", event.getOrderNumber(), event.getReason());
        updateStatus(event.getOrderNumber(), "CANCELLED");
    }

    private void updateStatus(String orderNumber, String action) {
        Order order = loadOrderPort.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalStateException("Order not found: " + orderNumber));

        switch (action) {
            case "STOCK_OK":
                order.completeStock();
                break;
            case "POINT_OK":
                order.completePoint();
                break;
            case "CANCELLED":
                order.cancel();
                break;
        }

        saveOrderPort.save(order);
        log.info("[Order Service] Order {} status updated by {}. Current Status: {}", 
                orderNumber, action, order.getStatus());
    }
}
