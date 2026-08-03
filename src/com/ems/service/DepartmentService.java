package com.ems.service;

import com.ems.dao.DepartmentDAO;
import com.ems.model.Department;

import java.util.List;

public class DepartmentService {
    private final DepartmentDAO dao = new DepartmentDAO();

    public void add(Department dept) throws Exception {
        if (dept.getDeptId() <= 0) throw new IllegalArgumentException("Department ID must be positive");
        if (dept.getDeptName() == null || dept.getDeptName().trim().isEmpty()) throw new IllegalArgumentException("Department name is required");
        dao.addDepartment(dept);
    }

    public void delete(int deptId) throws Exception {
        if (deptId <= 0) throw new IllegalArgumentException("Department ID must be positive");
        dao.deleteDepartment(deptId);
    }

    public List<Department> all() throws Exception {
        return dao.getAllDepartments();
    }
}
