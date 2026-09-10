package com.company.etp.service;

import com.company.etp.model.Attendance;

import java.util.List;

public interface AttendanceService {
    Attendance mark(Attendance attendance);
    List<Attendance> findAll();
    List<Attendance> findForCurrentUser(String username);
    Attendance markForCurrentUser(Attendance attendance, String username);
}
