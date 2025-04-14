package me.sarismart.backend.Controller;

import me.sarismart.backend.Entity.Product;
import me.sarismart.backend.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "ProductController", description = "Product Management")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Get All Products", description = "Retrieve a list of all products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Product by ID", description = "Retrieve a product by its ID")
    public Optional<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @Operation(summary = "Create Product", description = "Create a new product")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Product", description = "Delete a product by its ID")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}