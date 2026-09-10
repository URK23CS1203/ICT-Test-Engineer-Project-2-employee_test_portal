package com.company.etp.controller;

import com.company.etp.model.Employee;
import com.company.etp.model.Attendance;
import com.company.etp.repository.EmployeeRepository;
import com.company.etp.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final EmployeeRepository employeeRepository;

    public AttendanceController(AttendanceService attendanceService, EmployeeRepository employeeRepository) {
        this.attendanceService = attendanceService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public String attendance(Model model, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("attendanceList", attendanceService.findForCurrentUser(authentication.getName()));
        model.addAttribute("employees", admin ? employeeRepository.findAll().stream().filter(Employee::isActive).toList() : java.util.List.of());
        model.addAttribute("attendance", new Attendance());
        return "attendance";
    }

    @PostMapping
    public String mark(@Valid @ModelAttribute("attendance") Attendance attendance,
                       BindingResult result, Model model, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (result.hasErrors()) {
            model.addAttribute("attendanceList", attendanceService.findForCurrentUser(authentication.getName()));
            model.addAttribute("employees", admin ? employeeRepository.findAll().stream().filter(Employee::isActive).toList() : java.util.List.of());
            return "attendance";
        }
//        if (result.hasErrors()) {
//
//            System.out.println("========== ATTENDANCE VALIDATION ERRORS ==========");
//            result.getAllErrors().forEach(error ->
//                    System.out.println(error.toString())
//            );
//            System.out.println("===================================================");
//
//            model.addAttribute(
//                    "attendanceList",
//                    attendanceService.findForCurrentUser(authentication.getName())
//            );
//
//            model.addAttribute(
//                    "employees",
//                    admin
//                            ? employeeRepository.findAll()
//                            .stream()
//                            .filter(Employee::isActive)
//                            .toList()
//                            : java.util.List.of()
//            );
//
//            return "attendance";
//        }
        try {
            attendanceService.markForCurrentUser(attendance, authentication.getName());
            return "redirect:/attendance?success=Attendance saved";
        } catch (IllegalArgumentException | org.springframework.security.access.AccessDeniedException ex) {
            model.addAttribute("attendanceList", attendanceService.findForCurrentUser(authentication.getName()));
            model.addAttribute("employees", admin ? employeeRepository.findAll().stream().filter(Employee::isActive).toList() : java.util.List.of());
            model.addAttribute("error", ex.getMessage());
            return "attendance";
        }
    }
}
