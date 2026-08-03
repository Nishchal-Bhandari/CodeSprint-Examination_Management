package com.ems.service;

import com.ems.dao.FacultyDutyDAO;
import java.util.List;

public class FacultyDutyService {
    private final FacultyDutyDAO dao = new FacultyDutyDAO();

    public String autoAssign(int examId, String roomNo, String role, int requiredCount) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        if (roomNo == null || roomNo.trim().isEmpty()) throw new IllegalArgumentException("Room number is required");
        if (role == null || role.trim().isEmpty()) throw new IllegalArgumentException("Role is required");
        if (requiredCount <= 0) throw new IllegalArgumentException("Required count must be positive");
        return dao.autoAssignDuty(examId, roomNo.trim(), role.trim(), requiredCount);
    }

    public boolean manualAssign(int examId, String roomNo, int facultyId, String role) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Valid Exam ID required");
        if (roomNo == null || roomNo.trim().isEmpty()) throw new IllegalArgumentException("Room number required");
        if (facultyId <= 0) throw new IllegalArgumentException("Valid Faculty ID required");
        return dao.manualAssignDuty(examId, roomNo.trim(), facultyId, role != null ? role.trim() : "INVIGILATOR");
    }

    public boolean swapDuties(long dutyId1, long dutyId2) throws Exception {
        if (dutyId1 <= 0 || dutyId2 <= 0) throw new IllegalArgumentException("Valid Duty IDs required");
        return dao.swapFacultyDuty(dutyId1, dutyId2);
    }

    public List<String[]> all() throws Exception {
        return dao.fetchAllDuties();
    }

    public int clearExam(int examId) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        return dao.deleteDutiesForExam(examId);
    }
}
