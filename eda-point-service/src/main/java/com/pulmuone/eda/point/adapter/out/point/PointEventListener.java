package com.pulmuone.eda.point.adapter.out.point;

import com.pulmuone.eda.common.domain.event.OrderCreatedEvent;
import com.pulmuone.eda.common.domain.event.PointDeductedEvent;
import com.pulmuone.eda.common.domain.event.PointFailedEvent;
import com.pulmuone.eda.common.domain.exception.PointShortageException;
import com.pulmuone.eda.point.application.port.out.DeductPointPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

    private final DeductPointPort deductPointPort;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String SUCCESS_TOPIC = "point.deducted";
    private static final String FAILURE_TOPIC = "point.failed";

    @KafkaListener(topics = "order.created")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[Point Service] Subscribed OrderCreatedEvent from Kafka for Order: {}", event.getOrderNumber());

        try {
            // 2단계 9: 적립금 차감 시도
            deductPointPort.deduct(event.getProductId(), event.getQuantity());

            // 성공 시 결과 메시지 발행
            log.info("[Point Service] Point deducted successfully for Order: {}", event.getOrderNumber());
            kafkaTemplate.send(SUCCESS_TOPIC, new PointDeductedEvent(event.getOrderNumber()));

        } catch (PointShortageException e) {
            // 실패 시 실패 메시지 발행 (보상 트랜잭션 트리거)
            log.error("[Point Service] Point deduction failed for Order: {}. Reason: {}", event.getOrderNumber(), e.getMessage());
            kafkaTemplate.send(FAILURE_TOPIC, new PointFailedEvent(event.getOrderNumber(), e.getMessage()));
        }
    }
}
