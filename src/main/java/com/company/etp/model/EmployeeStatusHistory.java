package com.company.etp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(nullable = false, length = 20)
    private String oldStatus;

    @Column(nullable = false, length = 20)
    private String newStatus;

    @Column(nullable = false, length = 50)
    private String changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @Column(length = 250)
    private String reason;
}
