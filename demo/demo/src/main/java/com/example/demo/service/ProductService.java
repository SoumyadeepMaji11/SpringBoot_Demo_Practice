package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    //create
    public List<Product> saveProduct(List<Product> product){
        return productRepository.saveAll(product);
    }

    //read
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    //update
    public Product updateProduct(Product updatedProduct, Long id){
        Optional<Product> existingProduct = productRepository.findById(id);
        if(existingProduct.isPresent()) {
            Product product = existingProduct.get();
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());
            product.setQuantity(updatedProduct.getQuantity());
            return productRepository.save(product);
        }else{
            throw new RuntimeException("Product not found");

        }
    }

    //delete
    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    //pagination
    public Page<Product> findAll(Pageable pageable){
        return productRepository.findAll(pageable);
    }
}
