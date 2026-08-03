package com.ems.ui.panels;

import com.ems.model.Subject;
import com.ems.service.SubjectService;
import com.ems.util.AppTheme;
import com.ems.util.SubjectCatalog;
import com.ems.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class SubjectPanel extends JPanel {
    private final SubjectService service = new SubjectService();
    private final DefaultTableModel model;

    public SubjectPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
            "Subject Management",
            "Keep subjects organized by department and semester"
        ), BorderLayout.NORTH);

        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField codeField = new JTextField(8);
        JTextField deptField = new JTextField(5);
        JTextField nameField = new JTextField(18);
        JTextField semField = new JTextField(5);

        UiUtil.styleInput(codeField, 150);
        UiUtil.styleInput(deptField, 100);
        UiUtil.styleInput(nameField, 260);
        UiUtil.styleInput(semField, 90);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(UiUtil.buildLabel("Subject Code"), gbc);
        gbc.gridx = 1;
        form.add(codeField, gbc);
        gbc.gridx = 2;
        form.add(UiUtil.buildLabel("Dept ID"), gbc);
        gbc.gridx = 3;
        form.add(deptField, gbc);
        gbc.gridx = 4;
        form.add(UiUtil.buildLabel("Subject Name"), gbc);
        gbc.gridx = 5;
        form.add(nameField, gbc);
        gbc.gridx = 6;
        form.add(UiUtil.buildLabel("Semester"), gbc);
        gbc.gridx = 7;
        form.add(semField, gbc);

        javax.swing.JButton addBtn = UiUtil.buildPrimaryButton("Add Subject");
        javax.swing.JButton updateBtn = UiUtil.buildSecondaryButton("Update");
        javax.swing.JButton deleteBtn = UiUtil.buildSecondaryButton("Delete");
        javax.swing.JButton refreshBtn = UiUtil.buildSecondaryButton("Refresh");

        JPanel actions = UiUtil.buildActionRow();
        actions.add(addBtn);
        actions.add(updateBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(actions, gbc);

        formCard.add(form, BorderLayout.CENTER);

        model = new DefaultTableModel(new Object[]{"Code", "Dept", "Name", "Semester"}, 0);
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

        addBtn.addActionListener(e -> save(codeField, deptField, nameField, semField, true));
        updateBtn.addActionListener(e -> save(codeField, deptField, nameField, semField, false));
        deleteBtn.addActionListener(e -> {
            try {
                String code = codeField.getText().trim();
                if (code.isEmpty()) { UiUtil.error(this, new IllegalArgumentException("Subject code is required")); return; }
                int res = javax.swing.JOptionPane.showConfirmDialog(this, "Delete subject " + code + "?", "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
                if (res == javax.swing.JOptionPane.YES_OPTION) {
                    service.delete(code);
                    UiUtil.info(this, "Subject deleted");
                    refresh();
                    SubjectCatalog.notifyChanged();
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });
        refreshBtn.addActionListener(e -> refresh());

        refresh();
    }

    private void save(JTextField codeField, JTextField deptField, JTextField nameField, JTextField semField, boolean add) {
        try {
            Subject subject = new Subject(
                    codeField.getText().trim(),
                    Integer.parseInt(deptField.getText().trim()),
                    nameField.getText().trim(),
                    Integer.parseInt(semField.getText().trim())
            );
            if (add) {
                service.add(subject);
                UiUtil.info(this, "Subject added");
            } else {
                service.update(subject);
                UiUtil.info(this, "Subject updated");
            }
            refresh();
            SubjectCatalog.notifyChanged();
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            for (Subject subject : service.all()) {
                model.addRow(new Object[]{
                        subject.getSubjectCode(),
                        subject.getDeptId(),
                        subject.getSubjectName(),
                        subject.getSemester()
                });
            }
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }
}
