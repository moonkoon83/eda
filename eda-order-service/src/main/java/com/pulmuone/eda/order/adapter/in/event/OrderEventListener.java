package com.pulmuone.eda.order.adapter.in.event;

import com.pulmuone.eda.common.domain.event.PointDeductedEvent;
import com.pulmuone.eda.common.domain.event.PointFailedEvent;
import com.pulmuone.eda.common.domain.event.StockDeductedEvent;
import com.pulmuone.eda.common.domain.event.StockFailedEvent;
import com.pulmuone.eda.order.application.port.out.LoadOrderPort;
import com.pulmuone.eda.order.application.port.out.SaveOrderPort;
import com.pulmuone.eda.order.domain.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final LoadOrderPort loadOrderPort;
    private final SaveOrderPort saveOrderPort;

    @KafkaListener(topics = "stock.deducted")
    @Transactional
    public void onStockDeducted(StockDeductedEvent event) {
        log.info("[Order Service] Subscribed StockDeductedEvent from Kafka for Order: {}", event.getOrderNumber());
        updateStatus(event.getOrderNumber(), "STOCK_OK");
    }

    @KafkaListener(topics = "point.deducted")
    @Transactional
    public void onPointDeducted(PointDeductedEvent event) {
        log.info("[Order Service] Subscribed PointDeductedEvent from Kafka for Order: {}", event.getOrderNumber());
        updateStatus(event.getOrderNumber(), "POINT_OK");
    }

    @KafkaListener(topics = "stock.failed")
    @Transactional
    public void onStockFailed(StockFailedEvent event) {
        log.error("[Order Service] Subscribed StockFailedEvent from Kafka for Order: {}. Reason: {}", event.getOrderNumber(), event.getReason());
        updateStatus(event.getOrderNumber(), "CANCELLED");
    }

    @KafkaListener(topics = "point.failed")
    @Transactional
    public void onPointFailed(PointFailedEvent event) {
        log.error("[Order Service] Subscribed PointFailedEvent from Kafka for Order: {}. Reason: {}", event.getOrderNumber(), event.getReason());
        updateStatus(event.getOrderNumber(), "CANCELLED");
    }

    private void updateStatus(String orderNumber, String action) {
        int retryCount = 0;
        while (retryCount < 3) {
            try {
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
                return; // 성공 시 종료

            } catch (org.springframework.dao.OptimisticLockingFailureException e) {
                retryCount++;
                log.warn("[Order Service] Concurrent update detected for Order {}. Retrying... ({}/3)", 
                        orderNumber, retryCount);
                try { Thread.sleep(100); } catch (InterruptedException ignored) {} // 짧은 대기
            }
        }
        log.error("[Order Service] Failed to update Order {} status after 3 retries.", orderNumber);
    }
}
