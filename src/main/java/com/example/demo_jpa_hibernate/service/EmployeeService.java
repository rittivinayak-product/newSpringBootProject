package com.example.demo_jpa_hibernate.service;

import com.example.demo_jpa_hibernate.dto.EmployeeRequestDTO;
import com.example.demo_jpa_hibernate.dto.EmployeeResponseDTO;
import com.example.demo_jpa_hibernate.dto.ProductRequestDTO;
import com.example.demo_jpa_hibernate.dto.ProductResponsDTO;
import com.example.demo_jpa_hibernate.entity.Employee;
import com.example.demo_jpa_hibernate.entity.Product;
import com.example.demo_jpa_hibernate.exception.EmployeeNotFoundException;
import com.example.demo_jpa_hibernate.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream().map(this::mapToEmployeeResponseDTO).toList();
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            return mapToEmployeeResponseDTO(employee.get());
        } else {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
    }

    public List<EmployeeResponseDTO> getEmployeeByName(String name) {
        return employeeRepository.getEmployeeByName(name).stream().map(this::mapToEmployeeResponseDTO).toList();
    }

    public EmployeeResponseDTO mapToEmployeeResponseDTO(Employee employee) {
        return new EmployeeResponseDTO(employee.getId(), employee.getName(), employee.getSalary());
    }

    public EmployeeResponseDTO save(EmployeeRequestDTO employeeRequestDTO) {
        Employee employee = new Employee(employeeRequestDTO.getName(),employeeRequestDTO.getSalary());
        return mapToEmployeeResponseDTO(employeeRepository.save(employee));
    }

    public EmployeeResponseDTO update(Long id, EmployeeRequestDTO employeeRequestDTO) {
        Employee employee = employeeRepository.findById(id).get();
        employee.setName(employeeRequestDTO.getName());
        employee.setSalary(employeeRequestDTO.getSalary());
        return mapToEmployeeResponseDTO(employeeRepository.save(employee));
    }

    public String deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).get();
        employeeRepository.delete(employee);
        return "Employee with id: " + id + " has been deleted";
    }

    public List<EmployeeResponseDTO> filterproductsBySalary(String name, Double minSalary, Double maxSalary) {
        List<Employee> employees = employeeRepository.filterEmployee(name, minSalary, maxSalary);
        return employees.stream().map(this::mapToEmployeeResponseDTO).toList();
    }


    public EmployeeResponseDTO updatePartial(Long id, EmployeeRequestDTO employeeRequestDTO) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee updatedEmployee = employee.get();
            if (employeeRequestDTO.getName() != null) {
                updatedEmployee.setName(employeeRequestDTO.getName());
            }
            if (employeeRequestDTO.getSalary() != null) {
                updatedEmployee.setSalary(employeeRequestDTO.getSalary());
            }
            return mapToEmployeeResponseDTO(employeeRepository.save(updatedEmployee));
        } else {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
    }
}
