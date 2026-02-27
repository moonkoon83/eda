package com.pulmuone.eda.adapter.out.persistence;

import com.pulmuone.eda.application.port.out.LoadOrderPort;
import com.pulmuone.eda.application.port.out.SaveOrderPort;
import com.pulmuone.eda.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class OrderPersistenceAdapter implements SaveOrderPort, LoadOrderPort {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        return orderJpaRepository.existsByOrderNumber(orderNumber);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        return orderJpaRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }
}

interface OrderJpaRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderNumber(String orderNumber);
    Optional<Order> findByOrderNumber(String orderNumber);
}
