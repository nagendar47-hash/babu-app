package com.example.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock = 0;

    // ── Constructors ──────────────────────────────────────────
    public Product() {}

    public Product(String name, String description, Double price, Integer stock) {
        this.name        = name;
        this.description = description;
        this.price       = price;
        this.stock       = stock;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public Long    getId()          { return id; }
    public String  getName()        { return name; }
    public String  getDescription() { return description; }
    public Double  getPrice()       { return price; }
    public Integer getStock()       { return stock; }

    public void setId(Long id)              { this.id = id; }
    public void setName(String name)        { this.name = name; }
    public void setDescription(String d)    { this.description = d; }
    public void setPrice(Double price)      { this.price = price; }
    public void setStock(Integer stock)     { this.stock = stock; }
}
