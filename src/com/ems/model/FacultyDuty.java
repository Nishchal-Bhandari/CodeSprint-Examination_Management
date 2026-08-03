package com.ems.model;

public class FacultyDuty {
    private long dutyId;
    private int facultyId;
    private int examId;
    private String roomNo;
    private String role;

    public FacultyDuty(long dutyId, int facultyId, int examId, String roomNo, String role) {
        this.dutyId = dutyId;
        this.facultyId = facultyId;
        this.examId = examId;
        this.roomNo = roomNo;
        this.role = role;
    }

    public long getDutyId() {
        return dutyId;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public int getExamId() {
        return examId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getRole() {
        return role;
    }
}
