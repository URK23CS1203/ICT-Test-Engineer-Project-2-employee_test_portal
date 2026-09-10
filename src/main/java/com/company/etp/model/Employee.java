package com.company.etp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee code is required")
    @Column(nullable = false, unique = true, length = 20)
    private String employeeCode;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 80)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 15)
    private String phone;

    @PositiveOrZero(message = "Leave balance cannot be negative")
    @Column(nullable = false)
    @Builder.Default
    private int leaveBalance = 12;

//    @Column(nullable = false, columnDefinition = "boolean default true")
//    @Builder.Default
//    private boolean active = true;
    @Column(nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    @JsonSetter(nulls = Nulls.SKIP)
    private boolean active = true;
}
