package com.pulmuone.eda.application.service;

import com.pulmuone.eda.application.port.out.LoadOrderPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderNumberGeneratorTest {

    @InjectMocks
    private OrderNumberGenerator orderNumberGenerator;

    @Mock
    private LoadOrderPort loadOrderPort;

    @Test
    @DisplayName("주문 번호는 'YYYYMMDDHHMMSS' + 4자리 숫자로 생성되어야 한다")
    void generateOrderNumber_ShouldFollowFormat() {
        // given
        when(loadOrderPort.existsByOrderNumber(anyString())).thenReturn(false);

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
        } catch (Exception e) {
            assertThat(false).as("DateTime part (%s) does not match YYYYMMDDHHMMSS format", dateTimePart).isTrue();
        }

        // 4자리 숫자 형식 검증
        assertThat(randomPart).matches("^\\d{4}$");
        verify(loadOrderPort, times(1)).existsByOrderNumber(anyString());
    }

    @Test
    @DisplayName("생성된 주문 번호가 중복될 경우, 유니크한 번호가 생성될 때까지 재시도해야 한다")
    void generateOrderNumber_ShouldRetryOnDuplicate() {
        // given
        when(loadOrderPort.existsByOrderNumber(anyString()))
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        // when
        String orderNumber = orderNumberGenerator.generate();

        // then
        assertThat(orderNumber).isNotNull();
        verify(loadOrderPort, times(3)).existsByOrderNumber(anyString());
    }
}
