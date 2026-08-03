package com.ems.model;

public class Faculty {
    private int facultyId;
    private String facultyName;
    private int deptId;
    private int workload;
    private String availability;
    private String email;

    public Faculty(int facultyId, String facultyName, int deptId, int workload, String availability, String email) {
        this.facultyId = facultyId;
        this.facultyName = facultyName;
        this.deptId = deptId;
        this.workload = workload;
        this.availability = availability;
        this.email = email;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public int getDeptId() {
        return deptId;
    }

    public int getWorkload() {
        return workload;
    }

    public String getAvailability() {
        return availability;
    }

    public String getEmail() {
        return email;
    }
}
