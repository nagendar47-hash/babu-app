package com.example.app.config;

import com.example.app.model.Product;
import com.example.app.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    // Runs only when the 'dev' profile is active — safe for prod
    @Bean
    @Profile("dev")
    public CommandLineRunner loadSeedData(ProductRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Product("Laptop",  "High-performance laptop",  999.99, 10));
                repo.save(new Product("Phone",   "Latest smartphone",        699.49,  5));
                repo.save(new Product("Tablet",  "10-inch tablet",           399.00, 20));
                repo.save(new Product("Monitor", "27-inch 4K display",       449.99,  8));
                log.info("Seed data loaded — 4 products inserted.");
            }
        };
    }
}
