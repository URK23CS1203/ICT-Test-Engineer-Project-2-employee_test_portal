package com.company.etp.service;

import com.company.etp.model.Employee;
import com.company.etp.model.EmployeeStatusHistory;
import com.company.etp.model.User;
import com.company.etp.repository.AttendanceRepository;
import com.company.etp.repository.EmployeeRepository;
import com.company.etp.repository.EmployeeStatusHistoryRepository;
import com.company.etp.repository.LeaveRequestRepository;
import com.company.etp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import com.company.etp.exception.DuplicateEmployeeException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final UserRepository userRepository;
    private final EmployeeRepository repository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeStatusHistoryRepository historyRepository;

    public EmployeeServiceImpl(EmployeeRepository repository,
                               LeaveRequestRepository leaveRequestRepository,
                               AttendanceRepository attendanceRepository,
                               UserRepository userRepository,
                               EmployeeStatusHistoryRepository historyRepository) {
        this.repository = repository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @Override
    public Employee save(Employee employee) {
        employee.setActive(true);

        Map<String, String> errors = new HashMap<>();

        repository.findByEmployeeCode(employee.getEmployeeCode())
                .filter(existing -> !existing.getId().equals(employee.getId()))
                .ifPresent(existing ->
                        errors.put(
                                "employeeCode",
                                "Employee code already exists"
                        )
                );

        repository.findByEmailIgnoreCase(employee.getEmail())
                .filter(existing ->
                        !existing.getId().equals(employee.getId()))
                .ifPresent(existing ->
                        errors.put(
                                "email",
                                "Employee email already exists"
                        )
                );

        if (!errors.isEmpty()) {
            throw new DuplicateEmployeeException(errors);
        }
        return repository.save(employee);
    }

    @Override
    public List<Employee> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Employee> search(String name, String status) {
        List<Employee> employees;
        if (name == null || name.isBlank()) {
            employees = repository.findAll();
        } else {
            employees = repository.findByNameContainingIgnoreCase(name);
        }

        if (status == null || status.isBlank() || "ALL".equalsIgnoreCase(status)) {
            return employees;
        }

        boolean active = "ACTIVE".equalsIgnoreCase(status);
        return employees.stream()
                .filter(employee -> employee.isActive() == active)
                .toList();
    }

    @Override
    public Employee findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    @Override
    @Transactional
    public Employee deactivate(Long id, String changedBy, String reason) {
        Employee employee = findById(id);

        if (!employee.isActive()) {
            throw new IllegalArgumentException("Employee is already inactive");
        }

        employee.setActive(false);
        repository.save(employee);

        userRepository.findByEmployeeId(id).ifPresent(user -> {
            user.setEnabled(false);
            userRepository.save(user);
        });

        saveHistory(employee, "ACTIVE", "INACTIVE", changedBy, reason);
        return employee;
    }

    @Override
    @Transactional
    public Employee activate(Long id, String changedBy, String reason) {
        Employee employee = findById(id);

        if (employee.isActive()) {
            throw new IllegalArgumentException("Employee is already active");
        }

        employee.setActive(true);
        repository.save(employee);

        userRepository.findByEmployeeId(id).ifPresent(user -> {
            user.setEnabled(true);
            userRepository.save(user);
        });

        saveHistory(employee, "INACTIVE", "ACTIVE", changedBy, reason);
        return employee;
    }

    private void saveHistory(Employee employee, String oldStatus, String newStatus,
                             String changedBy, String reason) {
        historyRepository.save(EmployeeStatusHistory.builder()
                .employee(employee)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .changedAt(LocalDateTime.now())
                .reason(reason)
                .build());
    }
}
