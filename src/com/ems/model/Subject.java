package com.ems.model;

public class Subject {
    private String subjectCode;
    private int deptId;
    private String subjectName;
    private int semester;

    public Subject(String subjectCode, int deptId, String subjectName, int semester) {
        this.subjectCode = subjectCode;
        this.deptId = deptId;
        this.subjectName = subjectName;
        this.semester = semester;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public int getDeptId() {
        return deptId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getSemester() {
        return semester;
    }
}
