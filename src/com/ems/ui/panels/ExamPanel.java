package com.ems.ui.panels;

import com.ems.model.Exam;
import com.ems.service.ExamService;
import com.ems.util.SubjectCatalog;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.util.List;

public class ExamPanel extends JPanel {
    private final ExamService service = new ExamService();
    private final DefaultTableModel model;
    private final JComboBox<String> subjectCodeBoxRef;

    public ExamPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Exam Scheduling",
                "Create exam records and keep schedules synchronized"
        ), BorderLayout.NORTH);

        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField examIdField = new JTextField(6);
        JSpinner datePicker = new JSpinner(new SpinnerDateModel());
        JComboBox<String> subjectCodeBox = new JComboBox<>();
        this.subjectCodeBoxRef = subjectCodeBox;
        JComboBox<String> typeBox = new JComboBox<>(new String[]{
            "INTERNAL",
            "MIDTERM",
            "END_SEM",
            "SUPPLEMENTARY"
        });

        UiUtil.styleInput(examIdField, 110);
        UiUtil.allowDigitsOnly(examIdField);
        datePicker.setFont(AppTheme.FONT_BODY);
        datePicker.setPreferredSize(new java.awt.Dimension(170, 38));
        datePicker.setEditor(new JSpinner.DateEditor(datePicker, "yyyy-MM-dd"));
        subjectCodeBox.setFont(AppTheme.FONT_BODY);
        subjectCodeBox.setBackground(java.awt.Color.WHITE);
        subjectCodeBox.setPreferredSize(new java.awt.Dimension(170, 38));
        typeBox.setFont(AppTheme.FONT_BODY);
        typeBox.setBackground(java.awt.Color.WHITE);
        typeBox.setPreferredSize(new java.awt.Dimension(180, 38));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(UiUtil.buildLabel("Exam ID"), gbc);
        gbc.gridx = 1;
        form.add(examIdField, gbc);

        gbc.gridx = 2;
        form.add(UiUtil.buildLabel("Date (YYYY-MM-DD)"), gbc);
        gbc.gridx = 3;
        form.add(datePicker, gbc);

        gbc.gridx = 4;
        form.add(UiUtil.buildLabel("Subject Code"), gbc);
        gbc.gridx = 5;
        form.add(subjectCodeBox, gbc);

        gbc.gridx = 6;
        form.add(UiUtil.buildLabel("Type"), gbc);
        gbc.gridx = 7;
        form.add(typeBox, gbc);

        javax.swing.JButton addBtn = UiUtil.buildPrimaryButton("Create");
        javax.swing.JButton updateBtn = UiUtil.buildSecondaryButton("Update");
        javax.swing.JButton deleteBtn = UiUtil.buildSecondaryButton("Delete");
        javax.swing.JButton refreshBtn = UiUtil.buildSecondaryButton("Refresh");
        javax.swing.JButton reloadSubjectsBtn = UiUtil.buildSecondaryButton("Reload Subjects");

        JPanel actions = UiUtil.buildActionRow();
        actions.add(addBtn);
        actions.add(updateBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);
        actions.add(reloadSubjectsBtn);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(actions, gbc);

        formCard.add(form, BorderLayout.CENTER);

        model = new DefaultTableModel(new Object[]{"Exam ID", "Date", "Subject", "Type"}, 0);
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
                String idText = examIdField.getText().trim();
                if (idText.isEmpty()) { UiUtil.error(this, new IllegalArgumentException("Exam ID is required")); return; }
                int id = Integer.parseInt(idText);
                Exam exam = new Exam(
                        id,
                        toLocalDate((Date) datePicker.getValue()),
                        selectedSubject(subjectCodeBox),
                        ((String) typeBox.getSelectedItem()).trim()
                );
                service.add(exam);
                UiUtil.info(this, "Exam created");
                refresh();
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                String idText = examIdField.getText().trim();
                if (idText.isEmpty()) { UiUtil.error(this, new IllegalArgumentException("Exam ID is required for update")); return; }
                int id = Integer.parseInt(idText);
                Exam exam = new Exam(
                        id,
                        toLocalDate((Date) datePicker.getValue()),
                        selectedSubject(subjectCodeBox),
                        ((String) typeBox.getSelectedItem()).trim()
                );
                service.update(exam);
                UiUtil.info(this, "Exam updated");
                refresh();
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        deleteBtn.addActionListener(e -> {
            try {
                String idText = examIdField.getText().trim();
                if (idText.isEmpty()) { UiUtil.error(this, new IllegalArgumentException("Exam ID is required for delete")); return; }
                int id = Integer.parseInt(idText);
                int res = javax.swing.JOptionPane.showConfirmDialog(this, "Delete exam " + id + "?", "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
                if (res == javax.swing.JOptionPane.YES_OPTION) {
                    service.delete(id);
                    UiUtil.info(this, "Exam deleted");
                    refresh();
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        reloadSubjectsBtn.addActionListener(e -> loadSubjects(subjectCodeBox));

        SubjectCatalog.addListener(() -> loadSubjects(subjectCodeBoxRef));

        loadSubjects(subjectCodeBox);
        refresh();
    }

    private void loadSubjects(JComboBox<String> subjectCodeBox) {
        try {
            subjectCodeBox.removeAllItems();
            List<String> subjectCodes = service.subjectCodes();
            for (String code : subjectCodes) {
                subjectCodeBox.addItem(code);
            }
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }

    private String selectedSubject(JComboBox<String> subjectCodeBox) {
        String subject = (String) subjectCodeBox.getSelectedItem();
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code is required. Add subjects first.");
        }
        return subject.trim();
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Exam date is required");
        }
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private void refresh() {
        try {
            java.util.List<Exam> exams = service.all();
            model.setRowCount(0);
            for (Exam exam : exams) {
                model.addRow(new Object[]{
                        exam.getExamId(),
                        exam.getExamDate(),
                        exam.getSubjectCode(),
                        exam.getExamType()
                });
            }
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }
}
