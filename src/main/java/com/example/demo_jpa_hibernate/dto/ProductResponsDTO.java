package com.example.demo_jpa_hibernate.dto;

public class ProductResponsDTO {

    private Long id;
    private String product_name;
    private Double product_price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return product_name;
    }

    public void setName(String name) {
        this.product_name = name;
    }

    public Double getPrice() {
        return product_price;
    }

    public void setPrice(Double price) {
        this.product_price = price;
    }

    public ProductResponsDTO(Long id, String name, Double price) {
        this.id = id;
        this.product_name = name;
        this.product_price = price;
    }

    public ProductResponsDTO() {
    }
}
