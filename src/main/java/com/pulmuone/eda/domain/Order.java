package com.pulmuone.eda.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String orderNumber;

    private String productId;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order(String orderNumber, String productId, Integer quantity) {
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.quantity = quantity;
        this.status = OrderStatus.PENDING;
    }
}
