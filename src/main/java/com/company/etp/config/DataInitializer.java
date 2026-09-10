package com.company.etp.config;

import com.company.etp.model.Employee;
import com.company.etp.model.User;
import com.company.etp.repository.EmployeeRepository;
import com.company.etp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Create ADMIN login
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role("ADMIN")
                    .enabled(true)
                    .build());
        }

        // Create Employee1 login
        if (userRepository.findByUsername("employee1").isEmpty()) {
            userRepository.save(User.builder()
                    .username("employee1")
                    .password(passwordEncoder.encode("Employee@123"))
                    .role("EMPLOYEE")
                    .enabled(true)
                    .build());
        }

        /*
         * Link employee1 to one Employee record.
         * Employee1 is allowed to mark attendance only for this linked employee.
         */
        Employee employee = employeeRepository.findByEmployeeCode("EMP001")
                .orElseGet(() -> employeeRepository.save(
                        Employee.builder()
                                .employeeCode("1203")
                                .name("Arsath Ahamed N")
                                .email("arsathahamed@karunya.edu.in")
                                .build()
                ));

        User employeeUser = userRepository.findByUsername("employee1")
                .orElseThrow(() -> new IllegalStateException("employee1 user was not created."));

        if (employeeUser.getEmployee() == null
                || !employee.getId().equals(employeeUser.getEmployee().getId())) {
            employeeUser.setEmployee(employee);
            userRepository.save(employeeUser);
        }
    }
}
