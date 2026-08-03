package com.ems.model;

public class Student {
    private String usn;
    private String name;
    private String email;
    private int deptId;
    private int semester;

    public Student(String usn, String name, String email, int deptId, int semester) {
        this.usn = usn;
        this.name = name;
        this.email = email;
        this.deptId = deptId;
        this.semester = semester;
    }

    public String getUsn() {
        return usn;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getDeptId() {
        return deptId;
    }

    public int getSemester() {
        return semester;
    }
}
