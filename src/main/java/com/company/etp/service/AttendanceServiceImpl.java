package com.company.etp.service;

import com.company.etp.model.Attendance;
import com.company.etp.model.Employee;
import com.company.etp.model.User;
import com.company.etp.repository.AttendanceRepository;
import com.company.etp.repository.EmployeeRepository;
import com.company.etp.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceServiceImpl(
            AttendanceRepository attendanceRepository,
            EmployeeRepository employeeRepository,
            UserRepository userRepository) {

        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // COMMON ATTENDANCE MARKING
    // =========================================================

    @Override
    public Attendance mark(Attendance attendance) {

        if (attendance.getEmployee() == null
                || attendance.getEmployee().getId() == null) {

            throw new IllegalArgumentException(
                    "Employee must be selected."
            );
        }

        Employee employee = employeeRepository
                .findById(attendance.getEmployee().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Employee not found"
                        )
                );

        if (!employee.isActive()) {
            throw new IllegalArgumentException("Inactive employees cannot have new attendance marked");
        }

        // Prevent duplicate attendance for same employee/date
        attendanceRepository
                .findByEmployeeIdAndAttendanceDate(
                        employee.getId(),
                        attendance.getAttendanceDate()
                )
                .ifPresent(existing -> {

                    throw new IllegalArgumentException(
                            "Attendance already exists for this employee and date"
                    );
                });

        attendance.setEmployee(employee);

        return attendanceRepository.save(attendance);
    }


    // =========================================================
    // FIND ALL ATTENDANCE
    // =========================================================

    @Override
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> findForCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found: " + username));
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return findAll();
        }
        if (user.getEmployee() == null) {
            throw new AccessDeniedException("No employee profile is linked to username: " + username);
        }
        return attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(user.getEmployee().getId());
    }


    // =========================================================
    // MARK ATTENDANCE FOR CURRENT LOGGED-IN USER
    // =========================================================

    @Override
    public Attendance markForCurrentUser(
            Attendance attendance,
            String username) {

        // -----------------------------------------------------
        // Find currently logged-in user
        // -----------------------------------------------------

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found: "
                                        + username
                        )
                );


        // -----------------------------------------------------
        // ADMIN
        // -----------------------------------------------------
        //
        // ADMIN can mark attendance for ANY employee.
        //
        // -----------------------------------------------------

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {

            return mark(attendance);
        }


        // -----------------------------------------------------
        // EMPLOYEE
        // -----------------------------------------------------

        if ("EMPLOYEE".equalsIgnoreCase(user.getRole())) {

            // Find Employee linked to this login
            Employee currentEmployee = user.getEmployee();

            if (currentEmployee == null) {

                throw new AccessDeniedException(
                        "No employee profile is linked to username: "
                                + username
                );
            }


            // Employee submitted employee ID
            Long selectedEmployeeId =
                    attendance.getEmployee() == null
                            ? null
                            : attendance.getEmployee().getId();


            // -------------------------------------------------
            // SECURITY CHECK
            // -------------------------------------------------
            //
            // employee1 -> EMP001
            //
            // If employee1 submits EMP001 → ALLOW
            //
            // If employee1 submits EMP002 → DENY
            //
            // -------------------------------------------------

            if (selectedEmployeeId == null
                    || !selectedEmployeeId.equals(
                    currentEmployee.getId())) {

                throw new AccessDeniedException(
                        "Employees can mark attendance only for themselves."
                );
            }


            // Use the trusted Employee object from User relationship
            attendance.setEmployee(currentEmployee);

            return mark(attendance);
        }


        // -----------------------------------------------------
        // Unknown role
        // -----------------------------------------------------

        throw new AccessDeniedException(
                "User does not have permission to mark attendance."
        );
    }
}