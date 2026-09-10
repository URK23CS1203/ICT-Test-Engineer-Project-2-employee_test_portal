package com.company.etp.service;

import com.company.etp.exception.DuplicateEmployeeException;
import com.company.etp.model.Employee;
import com.company.etp.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;


    @Test
    void shouldRejectDuplicateEmployeeCode() {

        // 1. Create the employee that we are trying to save
        Employee employee = Employee.builder()
                .employeeCode("EMP001")
                .name("Test Employee")
                .email("test@gmail.com")
                .phone("9876543210")
                .leaveBalance(12)
                .active(true)
                .build();


        // 2. Pretend that EMP001 already exists in the database
        Employee existingEmployee = Employee.builder()
                .id(1L)
                .employeeCode("EMP001")
                .name("Existing Employee")
                .email("existing@gmail.com")
                .build();

        when(employeeRepository.findByEmployeeCode("EMP001"))
                .thenReturn(Optional.of(existingEmployee));


        // 3. Execute the service method and expect an exception
        assertThrows(
                DuplicateEmployeeException.class,
                () -> employeeService.save(employee)
        );
    }
}











//package com.company.etp.service;
//
//import com.company.etp.model.Employee;
//import com.company.etp.model.User;
//import com.company.etp.repository.AttendanceRepository;
//import com.company.etp.repository.EmployeeRepository;
//import com.company.etp.repository.EmployeeStatusHistoryRepository;
//import com.company.etp.repository.LeaveRequestRepository;
//import com.company.etp.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class EmployeeServiceImplTest {
//
//    @Mock EmployeeRepository employeeRepository;
//    @Mock LeaveRequestRepository leaveRequestRepository;
//    @Mock AttendanceRepository attendanceRepository;
//    @Mock UserRepository userRepository;
//    @Mock EmployeeStatusHistoryRepository historyRepository;
//
//    @InjectMocks EmployeeServiceImpl service;
//
//    @Test
//    void deactivate_shouldDeactivateEmployeeDisableLinkedUserAndCreateHistory() {
//        Employee employee = Employee.builder().id(1L).employeeCode("EMP001").name("Employee One").active(true).build();
//        User user = User.builder().id(1L).username("employee1").role("EMPLOYEE").enabled(true).employee(employee).build();
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//        when(userRepository.findByEmployeeId(1L)).thenReturn(Optional.of(user));
//
//        service.deactivate(1L, "admin", "Employee left the organization");
//
//        assertFalse(employee.isActive());
//        assertFalse(user.isEnabled());
//        verify(historyRepository).save(any());
//    }
//
//    @Test
//    void activate_shouldActivateEmployeeEnableLinkedUserAndCreateHistory() {
//        Employee employee = Employee.builder().id(1L).employeeCode("EMP001").name("Employee One").active(false).build();
//        User user = User.builder().id(1L).username("employee1").role("EMPLOYEE").enabled(false).employee(employee).build();
//        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
//        when(userRepository.findByEmployeeId(1L)).thenReturn(Optional.of(user));
//
//        service.activate(1L, "admin", "Employee returned");
//
//        assertTrue(employee.isActive());
//        assertTrue(user.isEnabled());
//        verify(historyRepository).save(any());
//    }
//}
