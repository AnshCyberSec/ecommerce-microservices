package com.ecommerce.customer_service.controller;





import com.ecommerce.customer_service.dto.CustomerRequest;
import com.ecommerce.customer_service.dto.CustomerResponse;
import com.ecommerce.customer_service.entity.Customer;
import com.ecommerce.customer_service.exception.CustomerNotFoundException;
import com.ecommerce.customer_service.repository.CustomerRepository;
import com.ecommerce.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // For order service integration
    @PostMapping("/{id}/validate")
    public CompletableFuture<ResponseEntity<Boolean>> validateCustomer(@PathVariable Long id) {
        return customerService.validateCustomer(id)
                .thenApply(ResponseEntity::ok);
    }


    @PostMapping("/{id}/details")
    public ResponseEntity<CustomerDetails> getCustomerDetails(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        CustomerDetails details = new CustomerDetails();
        details.setName(customer.getName());
        details.setEmail(customer.getEmail());

        return ResponseEntity.ok(details);
    }

    // Inner class for response
    public static class CustomerDetails {
        private String name;
        private String email;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}




