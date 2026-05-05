package com.example.app;

import com.example.app.model.Product;
import com.example.app.repository.ProductRepository;
import com.example.app.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")          // uses H2 in-memory
class ApplicationTests {

    @Autowired MockMvc          mvc;
    @Autowired ProductRepository repo;
    @Autowired ProductService    service;

    @BeforeEach
    void setup() {
        repo.deleteAll();
    }

    // ── Service unit tests ────────────────────────────────────
    @Test
    void createAndFindProduct() {
        Product p = service.create(new Product("Widget", "A test widget", 9.99, 100));
        assertThat(p.getId()).isNotNull();
        assertThat(service.findById(p.getId()).getName()).isEqualTo("Widget");
    }

    @Test
    void updateProduct() {
        Product p = service.create(new Product("Old", "desc", 1.0, 1));
        p.setName("New");
        Product updated = service.update(p.getId(), p);
        assertThat(updated.getName()).isEqualTo("New");
    }

    @Test
    void deleteProduct() {
        Product p = service.create(new Product("ToDelete", "desc", 1.0, 1));
        service.delete(p.getId());
        assertThat(repo.count()).isZero();
    }

    // ── REST API integration tests ────────────────────────────
    @Test
    void getAll_returnsOk() throws Exception {
        mvc.perform(get("/api/products"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createProduct_returnsCreated() throws Exception {
        String json = """
            {"name":"Gadget","description":"Cool gadget","price":49.99,"stock":10}
            """;
        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.name").value("Gadget"))
           .andExpect(jsonPath("$.price").value(49.99));
    }

    @Test
    void createProduct_missingName_returnsBadRequest() throws Exception {
        String json = """
            {"description":"No name","price":5.00,"stock":1}
            """;
        mvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
           .andExpect(status().isBadRequest());
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        mvc.perform(get("/api/products/9999"))
           .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_returnsNoContent() throws Exception {
        Product p = service.create(new Product("Del", "d", 1.0, 1));
        mvc.perform(delete("/api/products/" + p.getId()))
           .andExpect(status().isNoContent());
    }

    @Test
    void healthEndpoint_returnsUp() throws Exception {
        mvc.perform(get("/actuator/health"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("UP"));
    }
}
