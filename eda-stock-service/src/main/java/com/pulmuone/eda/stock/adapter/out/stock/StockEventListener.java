package com.pulmuone.eda.stock.adapter.out.stock;

import com.pulmuone.eda.common.domain.event.OrderCreatedEvent;
import com.pulmuone.eda.common.domain.event.StockDeductedEvent;
import com.pulmuone.eda.common.domain.event.StockFailedEvent;
import com.pulmuone.eda.common.domain.exception.StockShortageException;
import com.pulmuone.eda.stock.application.port.out.DeductStockPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final DeductStockPort deductStockPort;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String SUCCESS_TOPIC = "stock.deducted";
    private static final String FAILURE_TOPIC = "stock.failed";

    @KafkaListener(topics = "order.created")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[Stock Service] Subscribed OrderCreatedEvent from Kafka for Order: {}", event.getOrderNumber());

        try {
            // 2단계 9: 재고 차감 시도
            deductStockPort.deduct(event.getProductId(), event.getQuantity());

            // 성공 시 결과 메시지 발행
            log.info("[Stock Service] Stock deducted successfully for Order: {}", event.getOrderNumber());
            kafkaTemplate.send(SUCCESS_TOPIC, new StockDeductedEvent(
                    event.getOrderNumber(),
                    event.getProductId(),
                    event.getQuantity()
            ));

        } catch (StockShortageException e) {
            // 실패 시 실패 메시지 발행 (보상 트랜잭션 트리거)
            log.error("[Stock Service] Stock deduction failed for Order: {}. Reason: {}", event.getOrderNumber(), e.getMessage());
            kafkaTemplate.send(FAILURE_TOPIC, new StockFailedEvent(event.getOrderNumber(), e.getMessage()));
        }
    }
}
