package com.example.demo_jpa_hibernate.repository;

import com.example.demo_jpa_hibernate.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAll();

    @Query("select e from Employee e where e.name like %:name%")
    List<Employee> getEmployeeByName(@Param("name") String name);

    @Query("SELECT e FROM Employee e WHERE " +
            "(CAST(:name AS text) IS NULL OR LOWER(e.name) LIKE CONCAT('%', LOWER(CAST(:name AS text)), '%')) AND " +
            "(:minSalary IS NULL OR e.salary >= :minSalary) AND " +
            "(:maxSalary IS NULL OR e.salary <= :maxSalary)")
    List<Employee> filterEmployee(@Param("name") String name, @Param("minSalary") Double minSalary, @Param("maxSalary") Double maxSalary);



}
