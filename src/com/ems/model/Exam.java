package com.ems.model;

import java.time.LocalDate;

public class Exam {
    private int examId;
    private LocalDate examDate;
    private String subjectCode;
    private String sessionSlot; // "FN" (Forenoon) or "AN" (Afternoon)
    private String examType;

    public Exam(int examId, LocalDate examDate, String subjectCode, String sessionSlot, String examType) {
        this.examId = examId;
        this.examDate = examDate;
        this.subjectCode = subjectCode;
        this.sessionSlot = sessionSlot != null ? sessionSlot : "FN";
        this.examType = examType;
    }

    public Exam(int examId, LocalDate examDate, String subjectCode, String examType) {
        this(examId, examDate, subjectCode, "FN", examType);
    }

    public int getExamId() {
        return examId;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public String getSessionSlot() {
        return sessionSlot;
    }

    public String getExamType() {
        return examType;
    }
}
