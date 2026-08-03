package com.ems.model;

import java.time.LocalDate;

public class HallTicketEntry {
    private int examId;
    private LocalDate examDate;
    private String subjectCode;
    private String subjectName;
    private String examType;
    private String roomNo;
    private String benchNo;
    private Integer seatPosition;

    public HallTicketEntry(int examId, LocalDate examDate, String subjectCode, String subjectName, String examType, String roomNo, String benchNo, Integer seatPosition) {
        this.examId = examId;
        this.examDate = examDate;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.examType = examType;
        this.roomNo = roomNo;
        this.benchNo = benchNo;
        this.seatPosition = seatPosition;
    }

    public int getExamId() { return examId; }
    public LocalDate getExamDate() { return examDate; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public String getExamType() { return examType; }
    public String getRoomNo() { return roomNo; }
    public String getBenchNo() { return benchNo; }
    public Integer getSeatPosition() { return seatPosition; }
}
