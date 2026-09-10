package com.company.etp.repository;

import com.company.etp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeCode(String employeeCode);
    Optional<Employee> findByEmailIgnoreCase(String email);
    List<Employee> findByNameContainingIgnoreCase(String name);
}
