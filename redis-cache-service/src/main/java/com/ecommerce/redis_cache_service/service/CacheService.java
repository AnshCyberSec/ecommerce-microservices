package com.ecommerce.redis_cache_service.service;

import com.ecommerce.redis_cache_service.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUtil redisUtil;

    private static final String PRODUCT_KEY_PREFIX = "product-service:api:products:";
    private static final String CUSTOMER_KEY_PREFIX = "customer-service:api:customers:";
    private static final String ORDER_KEY_PREFIX = "order-service:api:orders:";



    public Object getProductById(Long id) {
        String cacheKey = PRODUCT_KEY_PREFIX + id;

        Object cachedProduct = redisTemplate.opsForValue().get(cacheKey);
        if (cachedProduct != null) {
            log.info("CACHE HIT - Product ID: {}", id);
            return cachedProduct;
        }

        log.info("CACHE MISS - Fetching product from service: {}", id);
        try {
            Object product = restTemplate.getForObject(
                    "http://PRODUCT-SERVICE/api/products/" + id, Object.class);

            if (product != null && !product.toString().contains("\"error\"")) {
                redisTemplate.opsForValue().set(cacheKey, product, Duration.ofMinutes(15));
                log.info("Cached product with key: {}", cacheKey);
            }
            return product;
        } catch (Exception e) {
            log.error("Error fetching product {}: {}", id, e.getMessage());
            return createErrorResponse("Product service unavailable", e.getMessage());
        }
    }

    public Object getAllProducts() {
        String cacheKey = PRODUCT_KEY_PREFIX + "all";

        Object cachedProducts = redisTemplate.opsForValue().get(cacheKey);
        if (cachedProducts != null) {
            log.info("CACHE HIT - All products");
            return cachedProducts;
        }

        log.info("CACHE MISS - Fetching all products from service");
        try {
            Object products = restTemplate.getForObject(
                    "http://PRODUCT-SERVICE/api/products", Object.class);

            if (products != null) {
                redisTemplate.opsForValue().set(cacheKey, products, Duration.ofMinutes(10));
                log.info("Cached all products with key: {}", cacheKey);
            }
            return products;
        } catch (Exception e) {
            log.error("Error fetching all products: {}", e.getMessage());
            return createErrorResponse("Product service unavailable", e.getMessage());
        }
    }

    public Object getProductBySlug(String slug) {
        String cacheKey = PRODUCT_KEY_PREFIX + "slug:" + slug;

        Object cachedProduct = redisTemplate.opsForValue().get(cacheKey);
        if (cachedProduct != null) {
            log.info("CACHE HIT - Product Slug: {}", slug);
            return cachedProduct;
        }

        log.info("CACHE MISS - Fetching product by slug: {}", slug);
        try {
            Object product = restTemplate.getForObject(
                    "http://PRODUCT-SERVICE/api/products/slug/" + slug, Object.class);

            if (product != null && !product.toString().contains("\"error\"")) {
                redisTemplate.opsForValue().set(cacheKey, product, Duration.ofMinutes(30));
                log.info("Cached product by slug with key: {}", cacheKey);
            }
            return product;
        } catch (Exception e) {
            log.error("Error fetching product by slug {}: {}", slug, e.getMessage());
            return createErrorResponse("Product service unavailable", e.getMessage());
        }
    }

    public Object createProduct(Map<String, Object> productRequest) {
        log.info("Creating new product");
        try {
            Object result = restTemplate.postForObject(
                    "http://PRODUCT-SERVICE/api/products", productRequest, Object.class);

            evictProductListCaches();
            log.info("Cleared product lists cache after creation");

            return result;
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            return createErrorResponse("Error creating product", e.getMessage());
        }
    }


    public Object updateProduct(Long id, Map<String, Object> productRequest) {
        log.info("Updating product ID: {}", id);
        try {
            restTemplate.put("http://PRODUCT-SERVICE/api/products/" + id, productRequest);

            redisTemplate.delete(PRODUCT_KEY_PREFIX + id);
            evictProductListCaches();
            log.info("Cleared product cache after update");

            return Map.of("message", "Product updated successfully", "id", id);
        } catch (Exception e) {
            log.error("Error updating product {}: {}", id, e.getMessage());
            return createErrorResponse("Error updating product", e.getMessage());
        }
    }

    public Object deleteProduct(Long id) {
        log.info("Deleting product ID: {}", id);
        try {
            restTemplate.delete("http://PRODUCT-SERVICE/api/products/" + id);

            redisTemplate.delete(PRODUCT_KEY_PREFIX + id);
            evictProductListCaches();
            log.info("Cleared product cache after deletion");

            return Map.of("message", "Product deleted successfully", "id", id);
        } catch (Exception e) {
            log.error("Error deleting product {}: {}", id, e.getMessage());
            return createErrorResponse("Error deleting product", e.getMessage());
        }
    }



    public Object getCustomerById(Long id) {
        String cacheKey = CUSTOMER_KEY_PREFIX + id;

        Object cachedCustomer = redisTemplate.opsForValue().get(cacheKey);
        if (cachedCustomer != null) {
            log.info("CACHE HIT - Customer ID: {}", id);
            return cachedCustomer;
        }

        log.info("CACHE MISS - Fetching customer from service: {}", id);
        try {
            Object customer = restTemplate.getForObject(
                    "http://CUSTOMER-SERVICE/api/customers/" + id, Object.class);

            if (customer != null && !customer.toString().contains("\"error\"")) {
                redisTemplate.opsForValue().set(cacheKey, customer, Duration.ofMinutes(15));
                log.info("Cached customer with key: {}", cacheKey);
            }
            return customer;
        } catch (Exception e) {
            log.error("Error fetching customer {}: {}", id, e.getMessage());
            return createErrorResponse("Customer service unavailable", e.getMessage());
        }
    }

    public Object getAllCustomers() {
        String cacheKey = CUSTOMER_KEY_PREFIX + "all";

        Object cachedCustomers = redisTemplate.opsForValue().get(cacheKey);
        if (cachedCustomers != null) {
            log.info("CACHE HIT - All customers");
            return cachedCustomers;
        }

        log.info("CACHE MISS - Fetching all customers from service");
        try {
            Object customers = restTemplate.getForObject(
                    "http://CUSTOMER-SERVICE/api/customers", Object.class);

            if (customers != null) {
                redisTemplate.opsForValue().set(cacheKey, customers, Duration.ofMinutes(10));
                log.info("Cached all customers with key: {}", cacheKey);
            }
            return customers;
        } catch (Exception e) {
            log.error("Error fetching all customers: {}", e.getMessage());
            return createErrorResponse("Customer service unavailable", e.getMessage());
        }
    }


    public Object createCustomer(Map<String, Object> customerRequest) {
        log.info("Creating new customer");
        try {
            Object result = restTemplate.postForObject(
                    "http://CUSTOMER-SERVICE/api/customers", customerRequest, Object.class);

            evictCustomerListCaches();
            log.info("Cleared customer lists cache after creation");

            return result;
        } catch (Exception e) {
            log.error("Error creating customer: {}", e.getMessage());
            return createErrorResponse("Error creating customer", e.getMessage());
        }
    }


    public Object updateCustomer(Long id, Map<String, Object> customerRequest) {
        log.info("Updating customer ID: {}", id);
        try {
            restTemplate.put("http://CUSTOMER-SERVICE/api/customers/" + id, customerRequest);

            redisTemplate.delete(CUSTOMER_KEY_PREFIX + id);
            evictCustomerListCaches();
            evictCustomerRelatedCache(id);
            log.info("Cleared customer cache after update");

            return Map.of("message", "Customer updated successfully", "id", id);
        } catch (Exception e) {
            log.error("Error updating customer {}: {}", id, e.getMessage());
            return createErrorResponse("Error updating customer", e.getMessage());
        }
    }

    public Object deleteCustomer(Long id) {
        log.info("Deleting customer ID: {}", id);
        try {
            restTemplate.delete("http://CUSTOMER-SERVICE/api/customers/" + id);

            redisTemplate.delete(CUSTOMER_KEY_PREFIX + id);
            evictCustomerListCaches();
            evictCustomerRelatedCache(id);
            log.info("Cleared customer cache after deletion");

            return Map.of("message", "Customer deleted successfully", "id", id);
        } catch (Exception e) {
            log.error("Error deleting customer {}: {}", id, e.getMessage());
            return createErrorResponse("Error deleting customer", e.getMessage());
        }
    }


    public Object getOrderById(Long id) {
        String cacheKey = ORDER_KEY_PREFIX + id;

        Object cachedOrder = redisTemplate.opsForValue().get(cacheKey);
        if (cachedOrder != null) {
            log.info("CACHE HIT - Order ID: {}", id);
            return cachedOrder;
        }

        log.info("CACHE MISS - Fetching order from service: {}", id);
        try {
            Object order = restTemplate.getForObject(
                    "http://ORDER-SERVICE/api/orders/" + id, Object.class);

            if (order != null && !order.toString().contains("\"error\"")) {
                redisTemplate.opsForValue().set(cacheKey, order, Duration.ofMinutes(20));
                log.info("Cached order with key: {}", cacheKey);
            }
            return order;
        } catch (Exception e) {
            log.error("Error fetching order {}: {}", id, e.getMessage());
            return createErrorResponse("Order service unavailable", e.getMessage());
        }
    }

    public Object getOrdersByCustomerId(Long customerId) {
        String cacheKey = ORDER_KEY_PREFIX + "customer:" + customerId;

        Object cachedOrders = redisTemplate.opsForValue().get(cacheKey);
        if (cachedOrders != null) {
            log.info("CACHE HIT - Orders for customer: {}", customerId);
            return cachedOrders;
        }

        log.info("CACHE MISS - Fetching orders for customer: {}", customerId);
        try {
            Object orders = restTemplate.getForObject(
                    "http://ORDER-SERVICE/api/orders/customer/" + customerId, Object.class);

            if (orders != null) {
                redisTemplate.opsForValue().set(cacheKey, orders, Duration.ofMinutes(15));
                log.info("Cached customer orders with key: {}", cacheKey);
            }
            return orders;
        } catch (Exception e) {
            log.error("Error fetching orders for customer {}: {}", customerId, e.getMessage());
            return createErrorResponse("Order service unavailable", e.getMessage());
        }
    }


    public Object createOrder(Map<String, Object> orderRequest) {
        log.info("Creating new order");
        try {
            Object result = restTemplate.postForObject(
                    "http://ORDER-SERVICE/api/orders", orderRequest, Object.class);

            if (orderRequest.containsKey("customerId")) {
                Long customerId = Long.valueOf(orderRequest.get("customerId").toString());
                evictCustomerRelatedCache(customerId);
            }
            evictOrderListCaches();
            log.info("Cleared order related cache after creation");

            return result;
        } catch (Exception e) {
            log.error("Error creating order: {}", e.getMessage());
            return createErrorResponse("Error creating order", e.getMessage());
        }
    }


    public Object clearProductsCache() {
        redisUtil.deleteKeysByPattern(PRODUCT_KEY_PREFIX + "*");
        return Map.of("message", "All products cache cleared");
    }

    public Object clearCustomersCache() {
        redisUtil.deleteKeysByPattern(CUSTOMER_KEY_PREFIX + "*");
        return Map.of("message", "All customers cache cleared");
    }

    public Object clearOrdersCache() {
        redisUtil.deleteKeysByPattern(ORDER_KEY_PREFIX + "*");
        return Map.of("message", "All orders cache cleared");
    }

    public Object clearAllCache() {
        clearProductsCache();
        clearCustomersCache();
        clearOrdersCache();
        log.info("Cleared all cache");
        return Map.of("message", "All cache cleared successfully");
    }

    public void evictProductBySlug(String slug) {
        String pattern = PRODUCT_KEY_PREFIX + "slug:" + slug;
        redisUtil.deleteKeysByPattern(pattern);
        log.info("Evicted PDP cache for slug: {}", slug);
    }

    private void evictProductListCaches() {
        try {
            String[] patterns = {
                    PRODUCT_KEY_PREFIX + "all",
                    PRODUCT_KEY_PREFIX + "slug:*"
            };

            for (String pattern : patterns) {
                redisUtil.deleteKeysByPattern(pattern);
            }
            log.info("Cleared product list caches");
        } catch (Exception e) {
            log.error("Error evicting product list caches: {}", e.getMessage());
        }
    }

    private void evictCustomerListCaches() {
        try {
            redisUtil.deleteKeysByPattern(CUSTOMER_KEY_PREFIX + "all");
            log.info("Cleared customer list caches");
        } catch (Exception e) {
            log.error("Error evicting customer list caches: {}", e.getMessage());
        }
    }

    private void evictOrderListCaches() {
        try {
            redisUtil.deleteKeysByPattern(ORDER_KEY_PREFIX + "all");
            redisUtil.deleteKeysByPattern(ORDER_KEY_PREFIX + "customer:*");
            log.info("Cleared order list caches");
        } catch (Exception e) {
            log.error("Error evicting order list caches: {}", e.getMessage());
        }
    }

    private void evictCustomerRelatedCache(Long customerId) {
        redisTemplate.delete(CUSTOMER_KEY_PREFIX + customerId);
        String ordersPattern = ORDER_KEY_PREFIX + "customer:" + customerId + "*";
        redisUtil.deleteKeysByPattern(ordersPattern);
        log.info("Evicted customer related cache: {}", customerId);
    }

    private Map<String, Object> createErrorResponse(String error, String details) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("details", details);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return errorResponse;
    }

    public String health() {
        try {
            String testKey = "health:test";
            redisTemplate.opsForValue().set(testKey, "ok", Duration.ofSeconds(10));
            String result = (String) redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);
            return "ok".equals(result) ? "Redis Cache Service is healthy - Redis connected"
                    : "Redis Cache Service - Redis connection issue";
        } catch (Exception e) {
            return "Redis Cache Service - Redis connection failed: " + e.getMessage();
        }
    }
}