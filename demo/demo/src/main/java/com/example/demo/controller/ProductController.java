package com.example.demo.controller;

import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private WebClient webClient;

    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Welcome! You are successfully authenticated.";
    }

    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/saveproduct")
//    public ResponseEntity<String> saveProduct(@RequestBody List<Product> product){
//        productService.saveProduct(product);
//        return ResponseEntity.status(HttpStatus.CREATED).body("Products added successfully");
//    }
    @PostMapping("/saveproduct")
    public ResponseEntity<String> saveProduct(@RequestBody List<Product> products) {
        try {
            for (Product product : products) {
                if (product.getName() == null || product.getName().trim().isEmpty()) {
                    throw new RuntimeException("Product name cannot be null or empty");
                }
            }
            productService.saveProduct(products);
            return ResponseEntity.status(HttpStatus.CREATED).body("Products added successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/getproduct")
    public List<Product> getProducts(){
        return productService.getAllProducts();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/getproduct/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id){
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = productService.updateProduct(product,id);
        return ResponseEntity.ok(updatedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteproduct/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getByPage")
    public Page<Product> getByPage(@RequestParam("pageNo") Integer page, @RequestParam("pageSize") Integer size){
        Pageable pageable = PageRequest.of(page,size);
        return productService.findAll(pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/todos-external")
    public List<Map<String, Object>> getTodos() {
        return productService.getAllTodos();
    }

}
