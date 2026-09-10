package com.company.etp.service;

import com.company.etp.enums.LeaveStatus;
import com.company.etp.model.Employee;
import com.company.etp.model.LeaveRequest;
import com.company.etp.model.User;
import com.company.etp.repository.EmployeeRepository;
import com.company.etp.repository.LeaveRequestRepository;
import com.company.etp.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public LeaveServiceImpl(LeaveRequestRepository leaveRepository,
                            EmployeeRepository employeeRepository,
                            UserRepository userRepository) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public LeaveRequest apply(LeaveRequest request) {
        if (request.getEmployee() == null || request.getEmployee().getId() == null) {
            throw new IllegalArgumentException("Employee is required");
        }

        Employee employee = employeeRepository.findById(request.getEmployee().getId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        validateDatesAndBalance(request, employee);

        if (leaveRepository.hasOverlap(employee.getId(), request.getStartDate(),
                request.getEndDate(), List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED))) {
            throw new IllegalArgumentException("Overlapping pending or approved leave already exists");
        }

        request.setEmployee(employee);
        request.setStatus(LeaveStatus.PENDING);
        return leaveRepository.save(request);
    }

    @Override
    @Transactional
    public LeaveRequest applyForCurrentUser(LeaveRequest request, String username) {
        User user = currentUser(username);
        if (!"EMPLOYEE".equalsIgnoreCase(user.getRole())) {
            return apply(request);
        }

        Employee employee = user.getEmployee();
        if (employee == null) {
            throw new AccessDeniedException("No employee profile is linked to username: " + username);
        }
        if (!employee.isActive() || !user.isEnabled()) {
            throw new AccessDeniedException("Inactive employees cannot apply for leave");
        }

//        Long requestedEmployeeId = request.getEmployee() == null ? null : request.getEmployee().getId();
//        if (requestedEmployeeId == null || !requestedEmployeeId.equals(employee.getId())) {
//            throw new AccessDeniedException("Employees can apply for leave only for themselves.");
//        }

        Long requestedEmployeeId = request.getEmployee() == null
                ? null
                : request.getEmployee().getId();

        if (requestedEmployeeId != null &&
                !requestedEmployeeId.equals(employee.getId())) {

            throw new AccessDeniedException(
                    "Employees can apply for leave only for themselves."
            );
        }

        request.setEmployee(employee);
        return apply(request);
    }

    @Override
    public List<LeaveRequest> findAll() {
        return leaveRepository.findAll();
    }

    @Override
    public List<LeaveRequest> findForCurrentUser(String username) {
        User user = currentUser(username);
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return findAll();
        }
        if (user.getEmployee() == null) {
            throw new AccessDeniedException("No employee profile is linked to username: " + username);
        }
        return leaveRepository.findByEmployeeIdOrderByStartDateDesc(user.getEmployee().getId());
    }

    @Override
    @Transactional
    public LeaveRequest approve(Long id, String username) {
        LeaveRequest request = findRequest(id);
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Only pending leave can be approved");
        }

        Employee employee = request.getEmployee();
        long days = numberOfDays(request);
        if (days > employee.getLeaveBalance()) {
            throw new IllegalArgumentException("Insufficient leave balance");
        }

        employee.setLeaveBalance(employee.getLeaveBalance() - (int) days);
        employeeRepository.save(employee);

        request.setStatus(LeaveStatus.APPROVED);
        request.setApprovedBy(username);
        request.setApprovedAt(LocalDateTime.now());
        return leaveRepository.save(request);
    }

    @Override
    @Transactional
    public LeaveRequest reject(Long id, String username) {
        LeaveRequest request = findRequest(id);
        if (request.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Only pending leave can be rejected");
        }

        request.setStatus(LeaveStatus.REJECTED);
        request.setRejectedBy(username);
        request.setRejectedAt(LocalDateTime.now());
        return leaveRepository.save(request);
    }

    private User currentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found: " + username));
    }

    private LeaveRequest findRequest(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
    }

    private void validateDatesAndBalance(LeaveRequest request, Employee employee) {
        if (!employee.isActive()) {
            throw new IllegalArgumentException("Inactive employees cannot apply for leave");
        }
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        long days = numberOfDays(request);
        if (days > employee.getLeaveBalance()) {
            throw new IllegalArgumentException("Insufficient leave balance");
        }
    }

    private long numberOfDays(LeaveRequest request) {
        return ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
    }
}
