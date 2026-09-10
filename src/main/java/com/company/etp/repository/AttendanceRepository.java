package com.company.etp.repository;

import com.company.etp.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    void deleteByEmployeeId(Long employeeId);

    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);

    List<Attendance> findByEmployeeIdOrderByAttendanceDateDesc(Long employeeId);
}
