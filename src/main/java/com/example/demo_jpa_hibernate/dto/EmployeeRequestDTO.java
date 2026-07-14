package com.example.demo_jpa_hibernate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class EmployeeRequestDTO {
    //    @NotBlank(message = "Name is required")
//    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;

    //    @NotNull(message = "Price is required")
//    @Positive(message = "Price must be positive")
    private Double salary;


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

    public EmployeeRequestDTO() {
    }

    public EmployeeRequestDTO(String name, Double salary) {
        this.name = name;
        this.salary = salary;
    }
}
