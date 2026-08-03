package com.ems.service;

import com.ems.dao.AttendanceDAO;
import com.ems.model.ExamAttendance;

import java.util.List;

public class AttendanceService {
    private final AttendanceDAO dao = new AttendanceDAO();

    public void mark(String usn, int examId, boolean present) throws Exception {
        if (usn == null || usn.isBlank()) throw new IllegalArgumentException("USN is required");
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        dao.markAttendance(usn.trim(), examId, present);
    }

    public List<ExamAttendance> forExam(int examId) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        return dao.fetchAttendanceForExam(examId);
    }

    public List<String[]> report(int examId) throws Exception {
        if (examId <= 0) throw new IllegalArgumentException("Exam ID must be positive");
        return dao.fetchAttendanceReport(examId);
    }

    public void logWashroomExit(String usn, int examId) throws Exception {
        if (usn == null || usn.isBlank()) throw new IllegalArgumentException("USN is required");
        dao.logWashroomExit(usn.trim(), examId);
    }

    public void returnFromWashroom(long wlId) throws Exception {
        dao.markWashroomReturn(wlId);
    }

    public List<String[]> washroomLog(int examId) throws Exception {
        return dao.fetchWashroomLog(examId);
    }

    public void logMalpractice(String usn, int examId, String roomNo,
                                String incidentType, String description, Integer reportedBy) throws Exception {
        if (usn == null || usn.isBlank()) throw new IllegalArgumentException("USN is required");
        if (incidentType == null || incidentType.isBlank()) throw new IllegalArgumentException("Incident type is required");
        if (roomNo == null || roomNo.isBlank()) throw new IllegalArgumentException("Room No is required");
        dao.logMalpractice(usn.trim(), examId, roomNo.trim(), incidentType.trim(), description, reportedBy);
    }

    public List<String[]> malpracticeForExam(int examId) throws Exception {
        return dao.fetchMalpracticeForExam(examId);
    }

    public List<String[]> allMalpractice() throws Exception {
        return dao.fetchAllMalpractice();
    }
}
