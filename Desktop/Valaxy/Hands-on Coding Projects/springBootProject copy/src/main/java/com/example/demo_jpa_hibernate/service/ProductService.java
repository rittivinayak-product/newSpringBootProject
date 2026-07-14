package com.example.demo_jpa_hibernate.service;

import com.example.demo_jpa_hibernate.dto.ProductRequestDTO;
import com.example.demo_jpa_hibernate.dto.ProductRequestUpdateDTO;
import com.example.demo_jpa_hibernate.dto.ProductResponsDTO;
import com.example.demo_jpa_hibernate.entity.Product;
import com.example.demo_jpa_hibernate.exception.ProductNotFoundException;
import com.example.demo_jpa_hibernate.repository.ProductRepositpory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepositpory productRepositpory;

    @PersistenceContext
    private EntityManager entityManager;

    public List<ProductResponsDTO> getAllProducts() {

        return productRepositpory.findAll().stream().map(this::mapToProductResponsDTO).toList();
    }

    public List<Product> searchByName(String name) {

        return productRepositpory.searchByName(name);
    }

    public ProductResponsDTO save(ProductRequestDTO productRequestDTO) {
        Product product = new Product(productRequestDTO.getName(), productRequestDTO.getPrice());
        return mapToProductResponsDTO(productRepositpory.save(product));
    }

    public ProductResponsDTO update(Long id, ProductRequestDTO productRequestDTO) {
        Product product = productRepositpory.findById(id).get();
        product.setName(productRequestDTO.getName());
        product.setPrice(productRequestDTO.getPrice());
        return mapToProductResponsDTO(productRepositpory.save(product));

    }

    public String deleteById(Long id) {
        Product product = productRepositpory.findById(id).get();
        productRepositpory.delete(product);
        return "Product deleted successfully";
    }

    public ProductResponsDTO findById(Long id) {
        Optional<Product> product = productRepositpory.findById(id);
        if (product.isPresent()) {
            return mapToProductResponsDTO(product.get());
        } else {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
    }

    public ProductResponsDTO updateProduct(Long id, ProductRequestUpdateDTO productRequestDTO) {
        Optional<Product> product = productRepositpory.findById(id);
        if (product.isPresent()) {
            Product updateProduct = product.get();
            if (productRequestDTO.getName() != null) {
                updateProduct.setName(productRequestDTO.getName());
            }
            if (productRequestDTO.getPrice() != null) {
                updateProduct.setPrice(productRequestDTO.getPrice());
            }
            return mapToProductResponsDTO(productRepositpory.save(updateProduct));
        } else {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
    }

    public Page<ProductResponsDTO> paginate(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepositpory.findAll(pageable);
        return productPage.map(this::mapToProductResponsDTO);
    }

    public List<ProductResponsDTO> searchFilterProducts(String name, Double minPrice, Double maxPrice) {

        List<Product> products = productRepositpory.searchProducts(name, minPrice, maxPrice);
        return products.stream().map(this::mapToProductResponsDTO).toList();
    }

    public List<ProductResponsDTO> searchFilterProductsbyCriteria(String name, Double minPrice, Double maxPrice) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> productRoot = criteriaQuery.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();
        if (name != null) {
            predicates.add(criteriaBuilder.like(productRoot.get("name"), "%" + name + "%"));
        }
        if (minPrice != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(productRoot.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(productRoot.get("price"), maxPrice));
        }
        criteriaQuery.select(productRoot).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getResultList().stream().map(this::mapToProductResponsDTO).toList();
    }


    private ProductResponsDTO mapToProductResponsDTO(Product product) {


        return new ProductResponsDTO(product.getId(), product.getName(), product.getPrice());
    }


}
