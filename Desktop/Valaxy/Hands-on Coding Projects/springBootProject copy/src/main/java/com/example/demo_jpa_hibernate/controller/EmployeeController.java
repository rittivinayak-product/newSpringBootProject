package com.example.demo_jpa_hibernate.controller;

import com.example.demo_jpa_hibernate.dto.EmployeeRequestDTO;
import com.example.demo_jpa_hibernate.dto.EmployeeResponseDTO;
import com.example.demo_jpa_hibernate.dto.ProductRequestDTO;
import com.example.demo_jpa_hibernate.dto.ProductResponsDTO;
import com.example.demo_jpa_hibernate.entity.Employee;
import com.example.demo_jpa_hibernate.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list")
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/list/{id}")
    public EmployeeResponseDTO getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/search/{name}")
    public List<EmployeeResponseDTO> searchEmployeeByName(@PathVariable String name) {
        return employeeService.getEmployeeByName(name);
    }

    @PostMapping("/add")
    public EmployeeResponseDTO addEmployee(@Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        return employeeService.save(employeeRequestDTO);
    }

    @PutMapping("/update/{id}")
    public EmployeeResponseDTO updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        return employeeService.update(id,employeeRequestDTO);
    }

    @PatchMapping("partialupdate/{id}")
    public EmployeeResponseDTO partialUpdateEmployee( @PathVariable Long id, @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        return employeeService.updatePartial(id,employeeRequestDTO);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.deleteEmployee(id));
    }

    @GetMapping("/filter")
    public List<EmployeeResponseDTO> filterEmployeeBySalary(@RequestParam(required = false) String name,
                                                            @RequestParam(required = false) Double minSalary,
                                                            @RequestParam(required = false) Double maxSalary) {
        return employeeService.filterproductsBySalary(name, minSalary, maxSalary);
    }

}
