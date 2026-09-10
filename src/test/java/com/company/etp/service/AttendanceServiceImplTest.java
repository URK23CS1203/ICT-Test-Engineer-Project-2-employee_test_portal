package com.company.etp.service;

import com.company.etp.model.Attendance;
import com.company.etp.model.Employee;
import com.company.etp.model.User;
import com.company.etp.repository.AttendanceRepository;
import com.company.etp.repository.EmployeeRepository;
import com.company.etp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock AttendanceRepository attendanceRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock UserRepository userRepository;

    @InjectMocks AttendanceServiceImpl service;

    @Test
    void employeeCannotMarkAttendanceForAnotherEmployee() {
        Employee own = Employee.builder().id(1L).active(true).build();
        Employee other = Employee.builder().id(2L).active(true).build();
        User user = User.builder().username("employee1").role("EMPLOYEE").enabled(true).employee(own).build();
        Attendance attendance = Attendance.builder().employee(other).attendanceDate(LocalDate.now()).build();
        when(userRepository.findByUsername("employee1")).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, () -> service.markForCurrentUser(attendance, "employee1"));
        verifyNoInteractions(employeeRepository, attendanceRepository);
    }
}
