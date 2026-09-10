package com.company.etp.service;

import com.company.etp.model.Employee;

import java.util.List;

public interface EmployeeService {
    Employee save(Employee employee);
    List<Employee> findAll();
    List<Employee> search(String name, String status);
    Employee findById(Long id);
    Employee deactivate(Long id, String changedBy, String reason);
    Employee activate(Long id, String changedBy, String reason);
}
