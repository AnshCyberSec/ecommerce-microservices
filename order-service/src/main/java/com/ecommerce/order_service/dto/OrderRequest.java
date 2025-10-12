package com.ecommerce.order_service.dto;



import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class OrderRequest {
    private Long customerId;
    private String shippingAddress;
    private List<OrderItemRequest> orderItems;
}