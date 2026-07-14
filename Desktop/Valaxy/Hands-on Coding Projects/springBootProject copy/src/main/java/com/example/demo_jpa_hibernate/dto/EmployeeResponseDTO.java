package com.example.demo_jpa_hibernate.dto;

public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private Double salary;

    public EmployeeResponseDTO(Long id, String name, Double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getSalary() {
        return salary;
    }
}
