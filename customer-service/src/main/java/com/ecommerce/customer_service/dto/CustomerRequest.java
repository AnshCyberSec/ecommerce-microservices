package com.ecommerce.customer_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CustomerRequest {
    private String name;
    private String email;
    private String phone;
    private String address;
}
