package com.company.etp.repository;

import com.company.etp.enums.LeaveStatus;
import com.company.etp.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    void deleteByEmployeeId(Long employeeId);

    List<LeaveRequest> findByEmployeeIdOrderByStartDateDesc(Long employeeId);

    @Query("""
            SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
            FROM LeaveRequest l
            WHERE l.employee.id = :employeeId
              AND l.startDate <= :endDate
              AND l.endDate >= :startDate
              AND l.status IN :statuses
            """)
    boolean hasOverlap(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") Collection<LeaveStatus> statuses
    );
}
