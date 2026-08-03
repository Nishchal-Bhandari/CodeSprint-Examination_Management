package com.ems.ui.panels;

import com.ems.model.Faculty;
import com.ems.service.FacultyService;
import com.ems.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class FacultyPanel extends JPanel {
    private final FacultyService service = new FacultyService();
    private final DefaultTableModel model;

    public FacultyPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Faculty Management",
                "View all faculties, add new faculty records, and delete faculty when needed"
        ), BorderLayout.NORTH);

        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField idField = new JTextField(6);
        JTextField nameField = new JTextField(16);
        JTextField deptField = new JTextField(5);
        JTextField workloadField = new JTextField(4);
        JTextField emailField = new JTextField(18);
        JComboBox<String> availabilityBox = new JComboBox<>(new String[]{"AVAILABLE", "UNAVAILABLE"});

        UiUtil.styleInput(idField, 110);
        UiUtil.styleInput(nameField, 220);
        UiUtil.styleInput(deptField, 90);
        UiUtil.styleInput(workloadField, 90);
        UiUtil.styleInput(emailField, 260);
        UiUtil.allowDigitsOnly(idField);
        UiUtil.allowDigitsOnly(deptField);
        UiUtil.allowDigitsOnly(workloadField);
        availabilityBox.setFont(com.ems.util.AppTheme.FONT_BODY);
        availabilityBox.setPreferredSize(new Dimension(150, 38));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(UiUtil.buildLabel("Faculty ID"), gbc);
        gbc.gridx = 1;
        form.add(idField, gbc);
        gbc.gridx = 2;
        form.add(UiUtil.buildLabel("Faculty Name"), gbc);
        gbc.gridx = 3;
        form.add(nameField, gbc);
        gbc.gridx = 4;
        form.add(UiUtil.buildLabel("Dept ID"), gbc);
        gbc.gridx = 5;
        form.add(deptField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(UiUtil.buildLabel("Workload"), gbc);
        gbc.gridx = 1;
        form.add(workloadField, gbc);
        gbc.gridx = 2;
        form.add(UiUtil.buildLabel("Availability"), gbc);
        gbc.gridx = 3;
        form.add(availabilityBox, gbc);
        gbc.gridx = 4;
        form.add(UiUtil.buildLabel("Email"), gbc);
        gbc.gridx = 5;
        form.add(emailField, gbc);

        javax.swing.JButton addBtn = UiUtil.buildPrimaryButton("Add Faculty");
        javax.swing.JButton deleteBtn = UiUtil.buildSecondaryButton("Delete by ID");
        javax.swing.JButton refreshBtn = UiUtil.buildSecondaryButton("Refresh");

        JPanel actions = UiUtil.buildActionRow();
        actions.add(addBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(actions, gbc);

        formCard.add(form, BorderLayout.CENTER);

        model = new DefaultTableModel(new Object[]{"ID", "Name", "Dept", "Workload", "Availability", "Email"}, 0);
        JTable table = new JTable(model);
        UiUtil.styleTable(table);

        JPanel tableCard = UiUtil.buildSurfaceCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel stack = new JPanel(new BorderLayout(0, 14));
        stack.setOpaque(false);
        stack.add(formCard, BorderLayout.NORTH);
        stack.add(tableCard, BorderLayout.CENTER);

        add(stack, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> refresh());

        addBtn.addActionListener(e -> {
            try {
                String idText = idField.getText().trim();
                String name = nameField.getText().trim();
                String deptText = deptField.getText().trim();
                String workloadText = workloadField.getText().trim();
                String email = emailField.getText().trim();
                String availability = (String) availabilityBox.getSelectedItem();

                if (idText.isEmpty() || name.isEmpty() || deptText.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("Faculty ID, Name and Dept ID are required"));
                    return;
                }

                int facultyId = Integer.parseInt(idText);
                int deptId = Integer.parseInt(deptText);
                int workload = workloadText.isEmpty() ? 0 : Integer.parseInt(workloadText);

                Faculty faculty = new Faculty(facultyId, name, deptId, workload, availability, email);
                service.add(faculty);
                UiUtil.info(this, "Faculty added");
                refresh();
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Faculty ID, Dept ID and Workload must be numbers"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        deleteBtn.addActionListener(e -> {
            try {
                String idText = idField.getText().trim();
                if (idText.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("Faculty ID is required"));
                    return;
                }
                int facultyId = Integer.parseInt(idText);
                int res = javax.swing.JOptionPane.showConfirmDialog(this, "Delete faculty " + facultyId + "?", "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
                if (res == javax.swing.JOptionPane.YES_OPTION) {
                    service.delete(facultyId);
                    UiUtil.info(this, "Faculty deleted");
                    refresh();
                }
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Faculty ID must be a number"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        refresh();
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            for (Faculty faculty : service.all()) {
                model.addRow(new Object[]{
                        faculty.getFacultyId(),
                        faculty.getFacultyName(),
                        faculty.getDeptId(),
                        faculty.getWorkload(),
                        faculty.getAvailability(),
                        faculty.getEmail()
                });
            }
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }
}
