package com.ems.ui.panels;

import com.ems.model.Student;
import com.ems.service.StudentService;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class StudentPanel extends JPanel {
    private final StudentService service = new StudentService();
    private final DefaultTableModel model;

    public StudentPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel header = UiUtil.buildSectionBanner(
                "Student Management",
                "Add, update, remove, and generate hall tickets from one place"
        );

        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField usnField = new JTextField(10);
        JTextField nameField = new JTextField(12);
        JTextField emailField = new JTextField(16);
        JTextField deptField = new JTextField(5);
        JTextField semField = new JTextField(5);

        UiUtil.styleInput(usnField, 170);
        UiUtil.styleInput(nameField, 200);
        UiUtil.styleInput(emailField, 250);
        UiUtil.styleInput(deptField, 90);
        UiUtil.styleInput(semField, 80);
        UiUtil.allowDigitsOnly(deptField);
        UiUtil.allowDigitsOnly(semField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(UiUtil.buildLabel("USN"), gbc);
        gbc.gridx = 1;
        form.add(usnField, gbc);

        gbc.gridx = 2;
        form.add(UiUtil.buildLabel("Name"), gbc);
        gbc.gridx = 3;
        form.add(nameField, gbc);

        gbc.gridx = 4;
        form.add(UiUtil.buildLabel("Email"), gbc);
        gbc.gridx = 5;
        form.add(emailField, gbc);

        gbc.gridx = 6;
        form.add(UiUtil.buildLabel("Dept ID"), gbc);
        gbc.gridx = 7;
        form.add(deptField, gbc);

        gbc.gridx = 8;
        form.add(UiUtil.buildLabel("Sem"), gbc);
        gbc.gridx = 9;
        form.add(semField, gbc);

        javax.swing.JButton addBtn = UiUtil.buildPrimaryButton("Add");
        javax.swing.JButton updateBtn = UiUtil.buildSecondaryButton("Update");
        javax.swing.JButton deleteBtn = UiUtil.buildSecondaryButton("Delete by USN");
        javax.swing.JButton refreshBtn = UiUtil.buildSecondaryButton("Refresh");
        javax.swing.JButton ticketBtn = UiUtil.buildPrimaryButton("Generate Hall Ticket");

        JPanel actions = UiUtil.buildActionRow();
        actions.add(addBtn);
        actions.add(updateBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);
        actions.add(ticketBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(actions, gbc);

        formCard.add(form, BorderLayout.CENTER);

        model = new DefaultTableModel(new Object[]{"USN", "Name", "Email", "Dept", "Semester"}, 0);
        JTable table = new JTable(model);
        UiUtil.styleTable(table);

        JPanel tableCard = UiUtil.buildSurfaceCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel stack = new JPanel(new BorderLayout(0, 14));
        stack.setOpaque(false);
        stack.add(formCard, BorderLayout.NORTH);
        stack.add(tableCard, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(stack, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> refresh());

        ticketBtn.addActionListener(e -> {
            try {
                int sel = table.getSelectedRow();
                String usn;
                if (sel >= 0) {
                    usn = (String) model.getValueAt(sel, 0);
                } else {
                    usn = usnField.getText().trim();
                }

                if (usn == null || usn.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("Enter USN or select a student row to generate hall ticket"));
                    return;
                }

                com.ems.model.Student studentInfo = new com.ems.service.StudentService().findByUsn(usn);
                if (studentInfo == null) {
                    UiUtil.error(this, new IllegalArgumentException("Student not found for USN " + usn));
                    return;
                }

                java.util.List<com.ems.model.HallTicketEntry> entries = new com.ems.service.ExamService().hallTicketForStudent(usn);
                if (entries.isEmpty()) {
                    UiUtil.info(this, "No exams found for student " + usn);
                    return;
                }

                com.ems.ui.HallTicketDialog.promptAndOpen(javax.swing.SwingUtilities.getWindowAncestor(this), studentInfo, entries);
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        addBtn.addActionListener(e -> {
            try {
                String usn = usnField.getText().trim();
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                if (usn.isEmpty() || name.isEmpty() || email.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("USN, Name and Email are required"));
                    return;
                }
                int deptId;
                int semId;
                try {
                    deptId = Integer.parseInt(deptField.getText().trim());
                    semId = Integer.parseInt(semField.getText().trim());
                } catch (NumberFormatException nfe) {
                    UiUtil.error(this, new IllegalArgumentException("Dept ID and Semester must be numbers"));
                    return;
                }

                Student student = new Student(usn, name, email, deptId, semId);
                service.add(student);
                UiUtil.info(this, "Student added");
                refresh();
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                String usn = usnField.getText().trim();
                if (usn.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("USN is required to update a student"));
                    return;
                }
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                int deptId;
                int semId;
                try {
                    deptId = Integer.parseInt(deptField.getText().trim());
                    semId = Integer.parseInt(semField.getText().trim());
                } catch (NumberFormatException nfe) {
                    UiUtil.error(this, new IllegalArgumentException("Dept ID and Semester must be numbers"));
                    return;
                }

                Student student = new Student(usn, name, email, deptId, semId);
                service.update(student);
                UiUtil.info(this, "Student updated");
                refresh();
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        deleteBtn.addActionListener(e -> {
            try {
                String usnToDelete = usnField.getText().trim();
                if (usnToDelete.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("Enter USN or select a row to delete"));
                    return;
                }
                int res = javax.swing.JOptionPane.showConfirmDialog(this, "Delete student " + usnToDelete + "?", "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
                if (res == javax.swing.JOptionPane.YES_OPTION) {
                    service.delete(usnToDelete);
                    UiUtil.info(this, "Student deleted");
                    refresh();
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        refresh();
    }

    private void refresh() {
        try {
            java.util.List<Student> list = service.all();
            // update model only after successful fetch
            model.setRowCount(0);
            for (Student student : list) {
                model.addRow(new Object[]{
                        student.getUsn(),
                        student.getName(),
                        student.getEmail(),
                        student.getDeptId(),
                        student.getSemester()
                });
            }
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }
}
