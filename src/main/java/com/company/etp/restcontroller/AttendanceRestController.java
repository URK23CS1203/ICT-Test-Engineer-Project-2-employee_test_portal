package com.company.etp.restcontroller;

import com.company.etp.dto.AttendanceRequest;
import com.company.etp.model.Attendance;
import com.company.etp.model.Employee;
import com.company.etp.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceRestController {

    private final AttendanceService service;

    public AttendanceRestController(AttendanceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Attendance> all(Authentication authentication) {
        return service.findForCurrentUser(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<Attendance> mark(
            @Valid @RequestBody AttendanceRequest request,
            Authentication authentication) {

        Attendance attendance = new Attendance();

        Employee employee = new Employee();
        employee.setId(request.getEmployeeId());

        attendance.setEmployee(employee);
        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setStatus(request.getStatus());

        return ResponseEntity
                .status(201)
                .body(service.markForCurrentUser(
                        attendance,
                        authentication.getName()
                ));
    }
}




















//package com.company.etp.restcontroller;
//
//import com.company.etp.model.Attendance;
//import com.company.etp.service.AttendanceService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/attendance")
//public class AttendanceRestController {
//    private final AttendanceService service;
//    public AttendanceRestController(AttendanceService service) { this.service = service; }
//
//    @GetMapping
//    public List<Attendance> all(Authentication authentication) {
//        return service.findForCurrentUser(authentication.getName());
//    }
//
//    @PostMapping
//    public ResponseEntity<Attendance> mark(@Valid @RequestBody Attendance attendance,
//                                           Authentication authentication) {
//        return ResponseEntity.status(201).body(service.markForCurrentUser(attendance, authentication.getName()));
//    }
//}
