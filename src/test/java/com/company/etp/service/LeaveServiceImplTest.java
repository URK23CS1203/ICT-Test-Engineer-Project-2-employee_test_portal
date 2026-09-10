package com.company.etp.service;

import com.company.etp.model.Employee;
import com.company.etp.model.LeaveRequest;
import com.company.etp.model.User;
import com.company.etp.repository.EmployeeRepository;
import com.company.etp.repository.LeaveRequestRepository;
import com.company.etp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {

    @Mock LeaveRequestRepository leaveRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock UserRepository userRepository;

    @InjectMocks LeaveServiceImpl service;

    @Test
    void employeeCannotApplyLeaveForAnotherEmployee() {
        Employee own = Employee.builder().id(1L).active(true).leaveBalance(12).build();
        Employee other = Employee.builder().id(2L).active(true).leaveBalance(12).build();
        User user = User.builder().username("employee1").role("EMPLOYEE").enabled(true).employee(own).build();
        LeaveRequest request = LeaveRequest.builder().employee(other).leaveType("CASUAL")
                .startDate(LocalDate.now()).endDate(LocalDate.now()).reason("Personal").build();
        when(userRepository.findByUsername("employee1")).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class, () -> service.applyForCurrentUser(request, "employee1"));
        verifyNoInteractions(employeeRepository, leaveRepository);
    }
}
