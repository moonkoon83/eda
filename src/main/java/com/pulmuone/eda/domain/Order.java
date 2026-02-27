package com.pulmuone.eda.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "eda_order")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String orderNumber;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Order(String orderNumber, String productId, Integer quantity) {
        validate(orderNumber, productId, quantity);
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.quantity = quantity;
        this.status = OrderStatus.PENDING;
    }

    public static Order create(String orderNumber, String productId, Integer quantity) {
        return new Order(orderNumber, productId, quantity);
    }

    private void validate(String orderNumber, String productId, Integer quantity) {
        if (orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException("Order number must not be empty.");
        }
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("Product ID must not be empty.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
}
