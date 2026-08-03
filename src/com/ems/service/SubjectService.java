package com.ems.service;

import com.ems.dao.SubjectDAO;
import com.ems.model.Subject;

import java.util.List;

public class SubjectService {
    private final SubjectDAO dao = new SubjectDAO();

    public void add(Subject subject) throws Exception {
        validate(subject);
        dao.addSubject(subject);
    }

    public void update(Subject subject) throws Exception {
        validate(subject);
        dao.updateSubject(subject);
    }

    public void delete(String subjectCode) throws Exception {
        if (subjectCode == null || subjectCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required");
        }
        dao.deleteSubject(subjectCode.trim());
    }

    public List<Subject> all() throws Exception {
        return dao.getAllSubjects();
    }

    public List<String> codes() throws Exception {
        return dao.getSubjectCodes();
    }

    private void validate(Subject subject) {
        if (subject.getSubjectCode() == null || subject.getSubjectCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required");
        }
        if (subject.getDeptId() <= 0) {
            throw new IllegalArgumentException("Department ID must be positive");
        }
        if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            throw new IllegalArgumentException("Subject name is required");
        }
        if (subject.getSemester() < 1 || subject.getSemester() > 8) {
            throw new IllegalArgumentException("Semester must be between 1 and 8");
        }
    }
}
