package com.pulmuone.eda.service;

import com.pulmuone.eda.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class OrderNumberGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();
    private static final int MAX_RETRIES = 5; // Max retries for unique number generation

    private final OrderRepository orderRepository;

    public String generate() {
        int retries = 0;
        String orderNumber;
        do {
            orderNumber = generateUniqueCandidate();
            retries++;
            if (retries > MAX_RETRIES) {
                // Handle case where it's hard to find a unique number
                // This might indicate a very high transaction rate or a bug
                throw new IllegalStateException("Failed to generate a unique order number after " + MAX_RETRIES + " retries.");
            }
        } while (orderRepository.existsByOrderNumber(orderNumber));
        return orderNumber;
    }

    private String generateUniqueCandidate() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String randomPart = String.format("%04d", RANDOM.nextInt(10000)); // 4-digit random number
        return timestamp + randomPart;
    }
}
