package com.shopflow.service;

import com.shopflow.dto.PageResponse;
import com.shopflow.dto.ProductRequest;
import com.shopflow.dto.ProductResponse;
import com.shopflow.entity.Category;
import com.shopflow.entity.Product;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.CategoryRepository;
import com.shopflow.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductResponse create(ProductRequest productRequest) {
        Product product = new Product();
        Category category = categoryRepository.findById(productRequest.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setCategory(category);
        product.setName(productRequest.name());
        product.setPrice(productRequest.price());
        product.setDescription(productRequest.description());
        product.setStock(productRequest.stock());
        product = productRepository.save(product);

        return toResponse(product);
    }

    @Cacheable(value = "products", key = "#id")
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toResponse(product);
    }

    public PageResponse<ProductResponse> findAll(Pageable pageable, Long categoryId) {
        Page<Product> page;
        if (categoryId != null) {
            page = productRepository.findByCategoryId(categoryId, pageable);
        } else {
            page = productRepository.findAll(pageable);
        }
        List<ProductResponse> content = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(content, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }


    @CachePut(value = "products", key = "#id")
    public ProductResponse update(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Category category = categoryRepository.findById(productRequest.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());
        product.setStock(productRequest.stock());
        product.setCategory(category);
        product = productRepository.save(product);
        return toResponse(product);
    }

    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStock(), product.getCategory().getId());
    }

}
