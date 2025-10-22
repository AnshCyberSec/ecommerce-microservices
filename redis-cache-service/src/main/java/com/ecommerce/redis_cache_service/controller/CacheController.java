package com.ecommerce.redis_cache_service.controller;

import com.ecommerce.redis_cache_service.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;



    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        log.info("GET /api/cache/products/{}", id);
        return ResponseEntity.ok(cacheService.getProductById(id));
    }

    @GetMapping("/products")
    public ResponseEntity<Object> getAllProducts() {
        log.info("GET /api/cache/products");
        return ResponseEntity.ok(cacheService.getAllProducts());
    }

    @GetMapping("/products/slug/{slug}")
    public ResponseEntity<Object> getProductBySlug(@PathVariable String slug) {
        log.info("GET /api/cache/products/slug/{}", slug);
        return ResponseEntity.ok(cacheService.getProductBySlug(slug));
    }

    @PostMapping("/products")
    public ResponseEntity<Object> createProduct(@RequestBody Map<String, Object> productRequest) {
        log.info("POST /api/cache/products");
        return ResponseEntity.ok(cacheService.createProduct(productRequest));
    }


    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> productRequest) {
        log.info("PUT /api/cache/products/{}", id);
        return ResponseEntity.ok(cacheService.updateProduct(id, productRequest));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        log.info("DELETE /api/cache/products/{}", id);
        return ResponseEntity.ok(cacheService.deleteProduct(id));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Object> getCustomerById(@PathVariable Long id) {
        log.info("GET /api/cache/customers/{}", id);
        return ResponseEntity.ok(cacheService.getCustomerById(id));
    }

    @GetMapping("/customers")
    public ResponseEntity<Object> getAllCustomers() {
        log.info("GET /api/cache/customers");
        return ResponseEntity.ok(cacheService.getAllCustomers());
    }

    @PostMapping("/customers")
    public ResponseEntity<Object> createCustomer(@RequestBody Map<String, Object> customerRequest) {
        log.info("POST /api/cache/customers");
        return ResponseEntity.ok(cacheService.createCustomer(customerRequest));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<Object> updateCustomer(@PathVariable Long id, @RequestBody Map<String, Object> customerRequest) {
        log.info("PUT /api/cache/customers/{}", id);
        return ResponseEntity.ok(cacheService.updateCustomer(id, customerRequest));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Object> deleteCustomer(@PathVariable Long id) {
        log.info("DELETE /api/cache/customers/{}", id);
        return ResponseEntity.ok(cacheService.deleteCustomer(id));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Object> getOrderById(@PathVariable Long id) {
        log.info("GET /api/cache/orders/{}", id);
        return ResponseEntity.ok(cacheService.getOrderById(id));
    }

    @GetMapping("/orders/customer/{customerId}")
    public ResponseEntity<Object> getOrdersByCustomerId(@PathVariable Long customerId) {
        log.info("GET /api/cache/orders/customer/{}", customerId);
        return ResponseEntity.ok(cacheService.getOrdersByCustomerId(customerId));
    }

    @PostMapping("/orders")
    public ResponseEntity<Object> createOrder(@RequestBody Map<String, Object> orderRequest) {
        log.info("POST /api/cache/orders");
        return ResponseEntity.ok(cacheService.createOrder(orderRequest));
    }


    @DeleteMapping("/clear/products")
    public ResponseEntity<Object> clearProductsCache() {
        log.info("DELETE /api/cache/clear/products");
        return ResponseEntity.ok(cacheService.clearProductsCache());
    }

    @DeleteMapping("/clear/customers")
    public ResponseEntity<Object> clearCustomersCache() {
        log.info("DELETE /api/cache/clear/customers");
        return ResponseEntity.ok(cacheService.clearCustomersCache());
    }

    @DeleteMapping("/clear/orders")
    public ResponseEntity<Object> clearOrdersCache() {
        log.info("DELETE /api/cache/clear/orders");
        return ResponseEntity.ok(cacheService.clearOrdersCache());
    }

    @DeleteMapping("/clear/all")
    public ResponseEntity<Object> clearAllCache() {
        log.info("DELETE /api/cache/clear/all");
        return ResponseEntity.ok(cacheService.clearAllCache());
    }


    @DeleteMapping("/evict/products/slug/{slug}")
    public ResponseEntity<Object> evictProductBySlug(@PathVariable String slug) {
        log.info("DELETE /api/cache/evict/products/slug/{}", slug);
        cacheService.evictProductBySlug(slug);
        return ResponseEntity.ok(Map.of(
                "message", "PDP cache evicted",
                "slug", slug
        ));
    }


    @GetMapping("/health")
    public ResponseEntity<String> health() {
        String healthStatus = cacheService.health();
        log.info("Health check: {}", healthStatus);
        return ResponseEntity.ok(healthStatus);
    }
}