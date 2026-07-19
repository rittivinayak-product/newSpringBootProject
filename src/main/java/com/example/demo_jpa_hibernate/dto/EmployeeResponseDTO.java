package com.example.demo_jpa_hibernate.dto;

public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private Double salary;
    private Integer version;

    public EmployeeResponseDTO(Long id, String name, Double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public EmployeeResponseDTO(Long id, String name, Double salary, Integer version) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.version = version;
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

    public Integer getVersion() { return version; }
}
