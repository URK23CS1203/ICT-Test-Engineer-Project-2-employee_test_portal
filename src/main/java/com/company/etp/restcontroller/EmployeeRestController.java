package com.company.etp.restcontroller;

import com.company.etp.model.Employee;
import com.company.etp.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {
    private final EmployeeService service;
    public EmployeeRestController(EmployeeService service) { this.service = service; }

    @GetMapping
    public List<Employee> all() { return service.findAll(); }

    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
        return ResponseEntity.status(201).body(service.save(employee));
    }

    @PutMapping("/{id}/deactivate")
    public Employee deactivate(@PathVariable Long id,
                               @RequestParam(defaultValue = "API deactivation") String reason,
                               Authentication authentication) {
        return service.deactivate(id, authentication.getName(), reason);
    }

    @PutMapping("/{id}/activate")
    public Employee activate(@PathVariable Long id,
                             @RequestParam(defaultValue = "API activation") String reason,
                             Authentication authentication) {
        return service.activate(id, authentication.getName(), reason);
    }
}
