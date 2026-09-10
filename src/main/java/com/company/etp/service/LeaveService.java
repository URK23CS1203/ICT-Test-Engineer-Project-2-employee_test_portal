package com.company.etp.service;

import com.company.etp.model.LeaveRequest;

import java.util.List;

public interface LeaveService {
    LeaveRequest apply(LeaveRequest request);
    LeaveRequest applyForCurrentUser(LeaveRequest request, String username);
    List<LeaveRequest> findAll();
    List<LeaveRequest> findForCurrentUser(String username);
    LeaveRequest approve(Long id, String username);
    LeaveRequest reject(Long id, String username);
}
