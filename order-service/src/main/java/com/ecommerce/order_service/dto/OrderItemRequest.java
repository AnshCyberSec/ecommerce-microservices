package com.ecommerce.order_service.dto;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemRequest {
    private Long productId;
    private Integer quantity;
}