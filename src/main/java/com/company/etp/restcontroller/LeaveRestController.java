package com.company.etp.restcontroller;

import com.company.etp.model.LeaveRequest;
import com.company.etp.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRestController {
    private final LeaveService service;
    public LeaveRestController(LeaveService service) { this.service = service; }

    @GetMapping
    public List<LeaveRequest> all(Authentication authentication) {
        return service.findForCurrentUser(authentication.getName());
    }

    @PostMapping
    public ResponseEntity<LeaveRequest> apply(@Valid @RequestBody LeaveRequest request, Authentication authentication) {
        return ResponseEntity.status(201).body(service.applyForCurrentUser(request, authentication.getName()));
    }

    @PutMapping("/{id}/approve")
    public LeaveRequest approve(@PathVariable Long id, Authentication authentication) {
        return service.approve(id, authentication.getName());
    }

    @PutMapping("/{id}/reject")
    public LeaveRequest reject(@PathVariable Long id, Authentication authentication) {
        return service.reject(id, authentication.getName());
    }
}
