package com.example.app.service;

import com.example.app.exception.GlobalExceptionHandler;
import com.example.app.model.Product;
import com.example.app.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Re-use the exception declared in GlobalExceptionHandler package
class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String msg) { super(msg); }
}

@Service
@Transactional
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    // ── Read ──────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        log.info("Fetching all products");
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found: id=" + id));
    }

    @Transactional(readOnly = true)
    public List<Product> search(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Product> inStock() {
        return repo.findByStockGreaterThan(0);
    }

    // ── Write ─────────────────────────────────────────────────
    public Product create(Product product) {
        log.info("Creating product: {}", product.getName());
        return repo.save(product);
    }

    public Product update(Long id, Product updated) {
        Product existing = findById(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStock(updated.getStock());
        log.info("Updated product id={}", id);
        return repo.save(existing);
    }

    public void delete(Long id) {
        Product existing = findById(id);
        repo.delete(existing);
        log.info("Deleted product id={}", id);
    }
}
