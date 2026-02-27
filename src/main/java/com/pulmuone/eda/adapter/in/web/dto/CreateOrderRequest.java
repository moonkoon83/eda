package com.pulmuone.eda.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateOrderRequest {
    @NotBlank(message = "Product ID is required.")
    private String productId;

    @NotNull(message = "Quantity is required.")
    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer quantity;
}
