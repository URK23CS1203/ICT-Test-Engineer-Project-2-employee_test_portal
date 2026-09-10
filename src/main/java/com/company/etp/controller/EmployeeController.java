package com.company.etp.controller;

import com.company.etp.model.Employee;
import com.company.etp.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.company.etp.exception.DuplicateEmployeeException;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public String employees(@RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "ALL") String status,
                            Model model) {
        model.addAttribute("employees", service.search(search, status));
        model.addAttribute("search", search == null ? "" : search);
        model.addAttribute("status", status);
        return "employee";
    }

//    @PostMapping
//    public String save(@Valid @ModelAttribute("employee") Employee employee,
//                       BindingResult result, Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("employees", service.findAll());
//            return "employee";
//        }
//        try {
//            service.save(employee);
//            return "redirect:/employees?success=Employee saved";
//        } catch (IllegalArgumentException ex) {
//            model.addAttribute("employees", service.findAll());
//            model.addAttribute("error", ex.getMessage());
//            return "employee";
//        }
//    }

    @PostMapping
    public String save(@Valid @ModelAttribute("employee") Employee employee,
                       BindingResult result, Model model) {

        // Bean Validation errors
        if (result.hasErrors()) {
            model.addAttribute("employees", service.findAll());
            return "employee";
        }

        try {
            service.save(employee);

            return "redirect:/employees?success=Employee saved";

        } catch (DuplicateEmployeeException ex) {

            // Display duplicate errors beside the appropriate fields
            ex.getFieldErrors().forEach(
                    (field, message) ->
                            result.rejectValue(field, "", message)
            );

            model.addAttribute("employees", service.findAll());

            return "employee";
        }
    }

    @GetMapping("/new")
    public String newEmployee(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("employees", service.findAll());
        return "employee";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Long id,
                             @RequestParam(defaultValue = "No reason provided") String reason,
                             org.springframework.security.core.Authentication authentication) {
        try {
            service.deactivate(id, authentication.getName(), reason);
            return "redirect:/employees?success=Employee deactivated and linked login disabled";
        } catch (IllegalArgumentException ex) {
            return "redirect:/employees?error=" + java.net.URLEncoder.encode(ex.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/{id}/activate")
    public String activate(@PathVariable Long id,
                            @RequestParam(defaultValue = "Employee reactivated") String reason,
                            org.springframework.security.core.Authentication authentication) {
        try {
            service.activate(id, authentication.getName(), reason);
            return "redirect:/employees?success=Employee activated and linked login enabled";
        } catch (IllegalArgumentException ex) {
            return "redirect:/employees?error=" + java.net.URLEncoder.encode(ex.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}
