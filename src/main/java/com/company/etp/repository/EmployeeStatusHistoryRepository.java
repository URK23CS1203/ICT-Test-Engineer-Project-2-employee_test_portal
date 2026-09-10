package com.company.etp.repository;

import com.company.etp.model.EmployeeStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeStatusHistoryRepository extends JpaRepository<EmployeeStatusHistory, Long> {
    List<EmployeeStatusHistory> findByEmployeeIdOrderByChangedAtDesc(Long employeeId);
}
