package com.example.demo_jpa_hibernate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmployeeUpdateDTO {

    private Long id;

    @NotBlank(message = "Must provide the name to be updated")
    private String name;

    @NotNull(message = "Must provide the salary to be updated")
    private Double salary;

    public EmployeeUpdateDTO(Long id, String name, Double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }
}
