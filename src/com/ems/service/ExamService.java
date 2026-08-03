package com.ems.service;

import com.ems.dao.ExamDAO;
import com.ems.model.Exam;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExamService {
    private final ExamDAO dao = new ExamDAO();
    private static final Set<String> VALID_EXAM_TYPES = new HashSet<>(Arrays.asList(
            "INTERNAL", "MIDTERM", "END_SEM", "SUPPLEMENTARY"
    ));

    public void add(Exam exam) throws Exception {
        validate(exam);
        dao.addExam(exam);
    }

    public void update(Exam exam) throws Exception {
        validate(exam);
        dao.updateExam(exam);
    }

    public void delete(int examId) throws Exception {
        if (examId <= 0) {
            throw new IllegalArgumentException("Exam ID must be positive");
        }
        dao.deleteExam(examId);
    }

    public List<Exam> all() throws Exception {
        return dao.getAllExams();
    }

    public List<String> subjectCodes() throws Exception {
        return new com.ems.dao.SubjectDAO().getSubjectCodes();
    }

    public java.util.List<com.ems.model.HallTicketEntry> hallTicketForStudent(String usn) throws Exception {
        if (usn == null || usn.trim().isEmpty()) {
            throw new IllegalArgumentException("USN is required");
        }
        return new com.ems.dao.ExamDAO().getExamsForStudent(usn.trim());
    }

    private void validate(Exam exam) {
        if (exam.getExamId() <= 0) {
            throw new IllegalArgumentException("Exam ID must be positive");
        }
        if (exam.getExamDate() == null) {
            throw new IllegalArgumentException("Exam date is required");
        }
        if (exam.getSubjectCode() == null || exam.getSubjectCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required");
        }
        if (exam.getExamType() == null || exam.getExamType().trim().isEmpty()) {
            throw new IllegalArgumentException("Exam type is required");
        }
        String normalizedType = exam.getExamType().trim().toUpperCase();
        if (!VALID_EXAM_TYPES.contains(normalizedType)) {
            throw new IllegalArgumentException("Invalid exam type. Use one of: INTERNAL, MIDTERM, END_SEM, SUPPLEMENTARY");
        }
    }
}
