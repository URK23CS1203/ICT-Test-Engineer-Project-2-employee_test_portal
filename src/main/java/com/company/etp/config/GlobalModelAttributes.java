package com.company.etp.config;

import com.company.etp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserRepository userRepository;

    public GlobalModelAttributes(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute("loggedInUsername")
    public String loggedInUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return "";
        return authentication.getName();
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @ModelAttribute("currentEmployeeId")
    public Long currentEmployeeId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        return userRepository.findByUsername(authentication.getName())
                .map(user -> user.getEmployee() == null ? null : user.getEmployee().getId())
                .orElse(null);
    }
}
