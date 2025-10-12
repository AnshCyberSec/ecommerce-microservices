package com.ecommerce.order_service.service;



import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    CompletableFuture<OrderResponse> createOrder(OrderRequest request);
    OrderResponse getOrderById(Long id);
    List<OrderResponse> getOrdersByCustomerId(Long customerId);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long id, String status);
    void cancelOrder(Long id);
}