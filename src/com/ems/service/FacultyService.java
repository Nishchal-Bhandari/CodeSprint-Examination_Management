package com.ems.service;

import com.ems.dao.FacultyDAO;
import com.ems.model.Faculty;

import java.util.List;

public class FacultyService {
    private final FacultyDAO dao = new FacultyDAO();

    public void add(Faculty faculty) throws Exception {
        validate(faculty);
        dao.addFaculty(faculty);
    }

    public void delete(int facultyId) throws Exception {
        if (facultyId <= 0) {
            throw new IllegalArgumentException("Faculty ID must be positive");
        }
        dao.deleteFaculty(facultyId);
    }

    public void updateAvailability(int facultyId, String availability) throws Exception {
        if (facultyId <= 0) {
            throw new IllegalArgumentException("Faculty ID must be positive");
        }
        if (availability == null || availability.trim().isEmpty()) {
            throw new IllegalArgumentException("Availability status is required");
        }
        dao.updateAvailability(facultyId, availability.trim());
    }

    public List<Faculty> all() throws Exception {
        return dao.getAllFaculty();
    }

    private void validate(Faculty faculty) {
        if (faculty.getFacultyId() <= 0) {
            throw new IllegalArgumentException("Faculty ID must be positive");
        }
        if (faculty.getFacultyName() == null || faculty.getFacultyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Faculty name is required");
        }
        if (faculty.getDeptId() <= 0) {
            throw new IllegalArgumentException("Department ID must be positive");
        }
        if (faculty.getWorkload() < 0) {
            throw new IllegalArgumentException("Workload cannot be negative");
        }
        if (faculty.getAvailability() == null || faculty.getAvailability().trim().isEmpty()) {
            throw new IllegalArgumentException("Availability is required");
        }
    }
}
