package com.ecommerce.order_service.service;



import com.ecommerce.order_service.client.CustomerServiceClient;
import com.ecommerce.order_service.client.ProductServiceClient;
import com.ecommerce.order_service.dto.*;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderItem;
import com.ecommerce.order_service.exception.OrderNotFoundException;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;
    private final CustomerServiceClient customerServiceClient;

    @Override
    public CompletableFuture<OrderResponse> createOrder(OrderRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Validate customer using FeignClient
                Boolean isCustomerValid = customerServiceClient.validateCustomer(request.getCustomerId());
                if (!isCustomerValid) {
                    throw new RuntimeException("Customer not found with id: " + request.getCustomerId());
                }

                // Get customer details
                CustomerServiceClient.CustomerDetails customerDetails =
                        customerServiceClient.getCustomerDetails(request.getCustomerId());

                // Validate products using FeignClient
                List<Long> productIds = request.getOrderItems().stream()
                        .map(OrderItemRequest::getProductId)
                        .collect(Collectors.toList());

                Boolean areProductsValid = productServiceClient.validateProducts(productIds);
                if (!areProductsValid) {
                    throw new RuntimeException("Some products are invalid or out of stock");
                }

                // Get product details
                List<ProductResponse> products = productServiceClient.getProductsByIds(productIds);

                // Create order
                Order order = orderMapper.toEntity(request);
                order.setCustomerName(customerDetails.getName());
                order.setCustomerEmail(customerDetails.getEmail());

                // Calculate order items and total
                double totalAmount = 0.0;

                for (int i = 0; i < request.getOrderItems().size(); i++) {
                    OrderItemRequest itemRequest = request.getOrderItems().get(i);

                    // Find product by ID from the list
                    ProductResponse product = products.stream()
                            .filter(p -> p.getId().equals(itemRequest.getProductId()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.getProductId()));

                    OrderItem orderItem = OrderItem.builder()
                            .order(order)
                            .productId(itemRequest.getProductId())
                            .productName(product.getName())
                            .productPrice(product.getPrice())
                            .quantity(itemRequest.getQuantity())
                            .subtotal(product.getPrice() * itemRequest.getQuantity())
                            .build();

                    order.getOrderItems().add(orderItem);
                    totalAmount += orderItem.getSubtotal();
                }

                order.setTotalAmount(totalAmount);

                Order savedOrder = orderRepository.save(order);
                log.info("Created order with id: {}", savedOrder.getId());

                return orderMapper.toResponse(savedOrder);

            } catch (Exception e) {
                log.error("Error creating order: {}", e.getMessage());
                throw new RuntimeException("Failed to create order: " + e.getMessage());
            }
        });
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId)
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        try {
            order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
            Order updatedOrder = orderRepository.save(order);
            log.info("Updated order status to {} for order id: {}", status, id);
            return orderMapper.toResponse(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Cancelled order with id: {}", id);
    }
}