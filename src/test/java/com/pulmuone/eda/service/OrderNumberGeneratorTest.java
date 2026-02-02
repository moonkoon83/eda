package com.pulmuone.eda.service;

import com.pulmuone.eda.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime; // Import for LocalDateTime

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderNumberGeneratorTest {

    // NOTE: OrderNumberGenerator 클래스는 아직 존재하지 않으므로, 이 테스트는 컴파일 에러가 발생합니다.
    @InjectMocks
    private OrderNumberGenerator orderNumberGenerator;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("주문 번호는 'YYYYMMDDHHMMSS' + 4자리 숫자로 생성되어야 한다")
    void generateOrderNumber_ShouldFollowFormat() {
        // given
        when(orderRepository.existsByOrderNumber(anyString())).thenReturn(false);

        // when
        String orderNumber = orderNumberGenerator.generate();

        // then
        assertThat(orderNumber).isNotNull();
        assertThat(orderNumber).hasSize(18); // YYYYMMDDHHMMSS (14) + 4 digits (4) = 18

        String dateTimePart = orderNumber.substring(0, 14);
        String randomPart = orderNumber.substring(14);

        // 'YYYYMMDDHHMMSS' 형식 검증
        try {
            LocalDateTime.parse(dateTimePart, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            // If parsing succeeds, the format is correct
        } catch (Exception e) {
            assertThat(false).as("DateTime part (%s) does not match YYYYMMDDHHMMSS format", dateTimePart).isTrue();
        }

        // 4자리 숫자 형식 검증
        assertThat(randomPart).matches("^\\d{4}$");
        verify(orderRepository, times(1)).existsByOrderNumber(anyString());
    }

    @Test
    @DisplayName("생성된 주문 번호가 중복될 경우, 유니크한 번호가 생성될 때까지 재시도해야 한다")
    void generateOrderNumber_ShouldRetryOnDuplicate() {
        // given
        // 첫 번째 생성 시도: 중복됨
        // 두 번째 생성 시도: 중복됨
        // 세 번째 생성 시도: 유니크함
        when(orderRepository.existsByOrderNumber(anyString()))
                .thenReturn(true) // First attempt
                .thenReturn(true) // Second attempt
                .thenReturn(false); // Third attempt

        // when
        String orderNumber = orderNumberGenerator.generate();

        // then
        assertThat(orderNumber).isNotNull();
        // verify that existsByOrderNumber was called 3 times
        verify(orderRepository, times(3)).existsByOrderNumber(anyString());
    }
}
