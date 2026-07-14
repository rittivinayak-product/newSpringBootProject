package com.example.demo_jpa_hibernate.controller;


import com.example.demo_jpa_hibernate.dto.ProductRequestDTO;
import com.example.demo_jpa_hibernate.dto.ProductRequestUpdateDTO;
import com.example.demo_jpa_hibernate.dto.ProductResponsDTO;
import com.example.demo_jpa_hibernate.entity.Product;
import com.example.demo_jpa_hibernate.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public List<ProductResponsDTO> getAllProducts() {
        return productService.getAllProducts();
    }


    @GetMapping("/search/{name}")
    public List<Product> searchByName(@PathVariable String name) {
        return productService.searchByName(name);
    }

    @PostMapping("/add")
    public ProductResponsDTO addProduct(@Valid @RequestBody ProductRequestDTO productRequestDTO) {
        return productService.save(productRequestDTO);
    }

    @PutMapping("/update/{id}")
    public ProductResponsDTO updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO productRequestDTO) {

        return productService.update(id, productRequestDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteById(id));

    }

    @GetMapping("/{id}")
    public ProductResponsDTO getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @PatchMapping("/updated/{id}")
    public ProductResponsDTO updateProducts(@PathVariable Long id, @RequestBody ProductRequestUpdateDTO productRequestDTO) {
        return productService.updateProduct(id, productRequestDTO);
    }

    @GetMapping("/page")
    public Page<ProductResponsDTO> getAllProductsPageable(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return productService.paginate(page, size);
    }

    @GetMapping("/filter")
    public List<ProductResponsDTO> filterProducts(@RequestParam(required = false) String name, @RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice) {
        return productService.searchFilterProducts(name, minPrice, maxPrice);
    }

    @GetMapping("/filterByCriteria")
    public List<ProductResponsDTO> filterProductsCriteria(@RequestParam(required = false) String name, @RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice) {
        return productService.searchFilterProductsbyCriteria(name, minPrice, maxPrice);
    }

}
