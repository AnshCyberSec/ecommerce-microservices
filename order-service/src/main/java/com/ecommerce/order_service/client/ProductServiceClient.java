package com.ecommerce.order_service.client;



import com.ecommerce.order_service.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @PostMapping("/api/products/validate")
    Boolean validateProducts(@RequestBody List<Long> productIds);

    @PostMapping("/api/products/batch")
    List<ProductResponse> getProductsByIds(@RequestBody List<Long> productIds);
}