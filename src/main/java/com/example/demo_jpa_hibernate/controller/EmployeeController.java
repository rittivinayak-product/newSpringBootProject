package com.example.demo_jpa_hibernate.controller;

import com.example.demo_jpa_hibernate.dto.EmployeeRequestDTO;
import com.example.demo_jpa_hibernate.dto.EmployeeResponseDTO;
import com.example.demo_jpa_hibernate.dto.ProductRequestDTO;
import com.example.demo_jpa_hibernate.dto.ProductResponsDTO;
import com.example.demo_jpa_hibernate.entity.Employee;
import com.example.demo_jpa_hibernate.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDTO addEmployee(@Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        return employeeService.save(employeeRequestDTO);
    }

    @PutMapping("/{id}/salary")
    public EmployeeResponseDTO updateSalary(@PathVariable Long id, @RequestBody EmployeeRequestDTO requestDTO) {
        return employeeService.updateSalary(id, requestDTO);
    }

    @PostMapping("/{id}/simulate-race-condition")
    public String simulate(@PathVariable Long id) {
        try {
            // 1. Thread A: The Fast Winner
            CompletableFuture<Void> threadA = CompletableFuture.runAsync(() -> {
                // Fetch the current state from the database (e.g., version = 0)
                EmployeeResponseDTO current = employeeService.getEmployeeById(id);

                EmployeeRequestDTO requestA = new EmployeeRequestDTO();
                requestA.setSalary(80000.0);
                requestA.setVersion(current.getVersion()); // version = 0

                employeeService.updateSalary(id, requestA);
            });

            // 2. Thread B: The Stale Loser
            CompletableFuture<Void> threadB = CompletableFuture.runAsync(() -> {
                // Fetch the exact same initial state (version = 0) before Thread A finishes
                EmployeeResponseDTO current = employeeService.getEmployeeById(id);

                try {
                    // Sleep for 1 second to guarantee Thread A commits its change first
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                EmployeeRequestDTO requestB = new EmployeeRequestDTO();
                requestB.setSalary(95000.0);
                requestB.setVersion(current.getVersion()); // Sending stale version = 0!

                employeeService.updateSalary(id, requestB);
            });

            // Wait for both background threads to finish their execution
            CompletableFuture.allOf(threadA, threadB).join();

            return "success";

        } catch (Exception e) {
            // Dig down to find the Optimistic Locking exception if wrapped by CompletableFuture
            Throwable cause = e.getCause();
            if (cause instanceof org.springframework.orm.ObjectOptimisticLockingFailureException
                    || e instanceof org.springframework.orm.ObjectOptimisticLockingFailureException) {
                return "Collision detected and prevented safely! Check your IDE logs.";
            }
            return "Failed due to another error: " + e.getMessage();
        }
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
        return employeeService.filterEmployeesBySalary(name, minSalary, maxSalary);
    }

    @GetMapping("/filterByCriteria")
    public List<EmployeeResponseDTO> filterEmployeeByCriteria(@RequestParam(required = false) String name,
                                                            @RequestParam(required = false) Double minSalary,
                                                            @RequestParam(required = false) Double maxSalary) {
        return employeeService.filteremployeesbyCriteria(name, minSalary, maxSalary);
    }

    @GetMapping("/page")
    public Page<EmployeeResponseDTO> getAllEmployeesPageable(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return employeeService.paginate(page, size);
    }



}
