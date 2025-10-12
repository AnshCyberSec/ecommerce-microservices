package com.ecommerce.order_service.client;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {

    @PostMapping("/api/customers/{id}/validate")
    Boolean validateCustomer(@PathVariable Long id);

    @PostMapping("/api/customers/{id}/details")
    CustomerDetails getCustomerDetails(@PathVariable Long id);

    // Inner class for customer details
    class CustomerDetails {
        private String name;
        private String email;

        // Getters and Setters
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    }
}