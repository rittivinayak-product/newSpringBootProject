package com.example.demo_jpa_hibernate.service;

import com.example.demo_jpa_hibernate.dto.EmployeeRequestDTO;
import com.example.demo_jpa_hibernate.dto.EmployeeResponseDTO;
import com.example.demo_jpa_hibernate.dto.ProductResponsDTO;
import com.example.demo_jpa_hibernate.entity.Employee;
import com.example.demo_jpa_hibernate.entity.Product;
import com.example.demo_jpa_hibernate.exception.EmployeeNotFoundException;
import com.example.demo_jpa_hibernate.repository.EmployeeRepository;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
        return new EmployeeResponseDTO(employee.getId(), employee.getName(), employee.getSalary(),employee.getVersion());
    }

    @Transactional
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

    public List<EmployeeResponseDTO> filterEmployeesBySalary(String name, Double minSalary, Double maxSalary) {
        List<Employee> employees = employeeRepository.filterEmployee(name, minSalary, maxSalary);
        return employees.stream().map(this::mapToEmployeeResponseDTO).toList();
    }

    public List<EmployeeResponseDTO> filteremployeesbyCriteria(String name, Double minSalary, Double maxSalary) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
        Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);
        List<Predicate> predicates = new ArrayList<>();
        if (name != null) {
            predicates.add(criteriaBuilder.like(employeeRoot.get("name"), "%" + name + "%"));
        }
        if (minSalary != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(employeeRoot.get("salary"), minSalary));
        }
        if (maxSalary != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(employeeRoot.get("salary"), maxSalary));
        }
        criteriaQuery.select(employeeRoot).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getResultList().stream().map(this::mapToEmployeeResponseDTO).toList();
    }

    public Page<EmployeeResponseDTO> paginate(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> productPage = employeeRepository.findAll(pageable);
        return productPage.map(this::mapToEmployeeResponseDTO);
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

    @Transactional
    public EmployeeResponseDTO updateSalary(Long id, EmployeeRequestDTO employeeRequestDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (!employee.getVersion().equals(employeeRequestDTO.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Employee.class, id);
        }
        employee.setSalary(employeeRequestDTO.getSalary());
        return mapToEmployeeResponseDTO(employeeRepository.save(employee));
    }

    @Transactional
    public void simulateCollision(Long id) {
        Employee userAview = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));
        Employee userBview = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee Not Found"));

        EmployeeRequestDTO requestA = new EmployeeRequestDTO(
                userAview.getName(),
                85000.00,
                userAview.getVersion()
        );

        updateSalary(id, requestA);

        EmployeeRequestDTO requestB = new EmployeeRequestDTO(
                "Updated via User B",
                userBview.getSalary(),
                userBview.getVersion()
        );

        try {
            updateSalary(id, requestB);
        } catch (ObjectOptimisticLockingFailureException e) {
            System.out.println(">>> DTO GUARD TRIGGERED: User B blocked. Reason: Stale version token.");
            throw e;
        }

    }
}
