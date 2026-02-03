package com.pulmuone.eda.adapter.in.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateOrderRequest {
    private String productId;
    private Integer quantity;
}
