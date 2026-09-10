package com.company.etp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            com.company.etp.security.CustomUserDetailsService userDetailsService) throws Exception {

        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth

                // Login and static resources are public.
                // IMPORTANT: "/" is NOT public because DashboardController maps "/" to the dashboard.
                .requestMatchers(
                    "/login",
                    "/access-denied",
                    "/css/**",
                    "/js/**"
                ).permitAll()

                // Portal pages: both authenticated roles may access them.
                .requestMatchers(
                    "/dashboard",
                    "/employees",
                    "/leaves",
                    "/attendance"
                ).hasAnyRole("ADMIN", "EMPLOYEE")

                // ADMIN-only browser operations.
                // These paths match the actual EmployeeController/LeaveController mappings.
                .requestMatchers(
                    "/employees/new"
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.POST, "/employees"
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.POST, "/employees/*/deactivate",
                    "/employees/*/activate"
                ).hasRole("ADMIN")
                .requestMatchers(
                    HttpMethod.POST, "/leaves/*/approve",
                    "/leaves/*/reject"
                ).hasRole("ADMIN")

                // Both roles may submit leave and attendance.
                .requestMatchers(
                    HttpMethod.POST, "/leaves", "/attendance"
                ).hasAnyRole("ADMIN", "EMPLOYEE")

                // REST API GET operations.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/employees",
                    "/api/employees/**",
                    "/api/leaves",
                    "/api/leaves/**",
                    "/api/attendance",
                    "/api/attendance/**"
                ).hasAnyRole("ADMIN", "EMPLOYEE")

                // REST API employee creation/deletion: ADMIN only.
                .requestMatchers(HttpMethod.POST, "/api/employees").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/employees/*/deactivate",
                    "/api/employees/*/activate").hasRole("ADMIN")

                // REST API leave/attendance creation.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/leaves",
                    "/api/attendance"
                ).hasAnyRole("ADMIN", "EMPLOYEE")

                // REST API state-changing operations: ADMIN only.
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/leaves/**",
                    "/api/attendance/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/leaves/**",
                    "/api/attendance/**"
                ).hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            .httpBasic(Customizer.withDefaults())
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
