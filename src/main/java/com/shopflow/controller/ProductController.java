package com.shopflow.controller;

import com.shopflow.dto.PageResponse;
import com.shopflow.dto.ProductRequest;
import com.shopflow.dto.ProductResponse;
import com.shopflow.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findAll(@PageableDefault(size = 10) Pageable pageable,@RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(productService.findAll(pageable, categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {

        ProductResponse response = productService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> save(@Valid @RequestBody ProductRequest pr) {
        ProductResponse prodResponse = productService.create(pr);
        URI location = URI.create("/api/products/" + prodResponse.id());
        return ResponseEntity.created(location).body(prodResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@Valid @RequestBody ProductRequest pr, @PathVariable Long id) {

        ProductResponse response = productService.update(id, pr);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        productService.delete(id);
        return ResponseEntity.noContent().build();

    }
}
