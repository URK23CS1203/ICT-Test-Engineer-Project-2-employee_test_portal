package com.company.etp.controller;

import com.company.etp.model.Employee;
import com.company.etp.model.LeaveRequest;
import com.company.etp.repository.EmployeeRepository;
import com.company.etp.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/leaves")
public class LeaveController {

    private final LeaveService leaveService;
    private final EmployeeRepository employeeRepository;

    public LeaveController(LeaveService leaveService, EmployeeRepository employeeRepository) {
        this.leaveService = leaveService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public String leaves(Model model, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("leaves", leaveService.findForCurrentUser(authentication.getName()));
        model.addAttribute("employees", admin ? employeeRepository.findAll().stream().filter(Employee::isActive).toList() :
                java.util.List.of());
        model.addAttribute("leave", new LeaveRequest());
        return "leave";
    }

    @PostMapping
    public String apply(@Valid @ModelAttribute("leave") LeaveRequest request,
                        BindingResult result, Model model, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (result.hasErrors()) {
            model.addAttribute("leaves", leaveService.findForCurrentUser(authentication.getName()));
            model.addAttribute("employees", admin ? employeeRepository.findAll().stream().filter(Employee::isActive).toList() : java.util.List.of());
            return "leave";
        }
        try {
            leaveService.applyForCurrentUser(request, authentication.getName());
            return "redirect:/leaves?success=Leave submitted";
        } catch (IllegalArgumentException | org.springframework.security.access.AccessDeniedException ex) {
            model.addAttribute("leaves", leaveService.findForCurrentUser(authentication.getName()));
            model.addAttribute("employees", admin ? employeeRepository.findAll().stream().filter(Employee::isActive).toList() : java.util.List.of());
            model.addAttribute("error", ex.getMessage());
            return "leave";
        }
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, Authentication authentication) {
        leaveService.approve(id, authentication.getName());
        return "redirect:/leaves?success=Leave approved";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, Authentication authentication) {
        leaveService.reject(id, authentication.getName());
        return "redirect:/leaves?success=Leave rejected";
    }
}
