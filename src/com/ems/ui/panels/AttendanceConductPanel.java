package com.ems.ui.panels;

import com.ems.service.AttendanceService;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Attendance & Conduct panel – per-seat attendance and malpractice log.
 */
public class AttendanceConductPanel extends JPanel {
    private final AttendanceService service = new AttendanceService();
    private final com.ems.service.AllocationService allocationService = new com.ems.service.AllocationService();
    private final DefaultTableModel attModel;
    private final DefaultTableModel mpModel;

    public AttendanceConductPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Attendance & Conduct",
                "Mark per-seat attendance and log malpractice incidents"
        ), BorderLayout.NORTH);

        // ---- Top form card ----
        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField examIdField = new JTextField("5001", 8); UiUtil.styleInput(examIdField, 100); UiUtil.allowDigitsOnly(examIdField);
        JComboBox<String> usnBox = new JComboBox<>();
        usnBox.setFont(com.ems.util.AppTheme.FONT_BODY);
        usnBox.setPreferredSize(new Dimension(140, 38));

        // Malpractice sub-fields
        JTextField roomField     = new JTextField(8);  UiUtil.styleInput(roomField, 100);
        JComboBox<String> mpTypeBox = new JComboBox<>(new String[]{
                "Copying", "Mobile Phone", "Chit Found", "Impersonation", "Disruptive Behaviour", "Other"
        });
        mpTypeBox.setFont(com.ems.util.AppTheme.FONT_BODY);
        mpTypeBox.setPreferredSize(new Dimension(150, 38));
        JTextField mpDescField   = new JTextField(20); UiUtil.styleInput(mpDescField, 380);
        JTextField reporterField = new JTextField(6);  UiUtil.styleInput(reporterField, 100); UiUtil.allowDigitsOnly(reporterField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,8,6,8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Exam ID + Bulk Attendance Actions
        gbc.gridy = 0;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Exam ID"), gbc);
        gbc.gridx = 1; form.add(examIdField, gbc);

        JButton loadBtn = UiUtil.buildSecondaryButton("📋 Load Students");
        JButton markAllPresentBtn = UiUtil.buildSecondaryButton("✅ Mark All Present");
        JButton markAllAbsentBtn = UiUtil.buildSecondaryButton("❌ Mark All Absent");
        JButton saveAllBtn = UiUtil.buildPrimaryButton("💾 Save Attendance for All");

        JPanel attRow = UiUtil.buildActionRow();
        attRow.add(loadBtn); attRow.add(markAllPresentBtn); attRow.add(markAllAbsentBtn); attRow.add(saveAllBtn);
        gbc.gridx = 2; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(attRow, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;

        // Row 1: Single Malpractice Target USN
        gbc.gridy = 1;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Target USN"), gbc);
        gbc.gridx = 1; form.add(usnBox, gbc);

        // Row 2: Malpractice 1
        gbc.gridy = 2;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Room No"), gbc);
        gbc.gridx = 1; form.add(roomField, gbc);
        gbc.gridx = 2; form.add(UiUtil.buildLabel("Incident"), gbc);
        gbc.gridx = 3; form.add(mpTypeBox, gbc);
        gbc.gridx = 4; form.add(UiUtil.buildLabel("Reporter ID"), gbc);
        gbc.gridx = 5; form.add(reporterField, gbc);
        
        // Row 3: Malpractice 2 (Description)
        gbc.gridy = 3;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Description"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(mpDescField, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        
        // Row 4: Malpractice buttons
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.EAST;
        JButton mpBtn = UiUtil.buildSecondaryButton("Log Malpractice");
        JButton viewMpBtn = UiUtil.buildSecondaryButton("View Incidents");
        JPanel mpRow = UiUtil.buildActionRow();
        mpRow.add(mpBtn); mpRow.add(viewMpBtn);
        form.add(mpRow, gbc);

        formCard.add(form, BorderLayout.CENTER);

        // ---- Tables ----
        attModel = new DefaultTableModel(new Object[]{"USN", "Student Name", "Current Status", "Mark Present"}, 0) {
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class;
                return String.class;
            }
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        JTable attTable = new JTable(attModel);
        UiUtil.styleTable(attTable);
        attTable.setRowHeight(38);
        attTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        attTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        attTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        attTable.getColumnModel().getColumn(3).setPreferredWidth(120);

        // High contrast colour-code renderer for dark theme
        attTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (sel) {
                    comp.setBackground(t.getSelectionBackground());
                    comp.setForeground(t.getSelectionForeground());
                } else {
                    String status = (String) t.getModel().getValueAt(r, 2);
                    if ("Absent".equalsIgnoreCase(status)) {
                        comp.setBackground(new Color(69, 10, 10));
                        comp.setForeground(new Color(254, 202, 202));
                    } else if ("Present".equalsIgnoreCase(status)) {
                        comp.setBackground(new Color(6, 78, 59));
                        comp.setForeground(new Color(167, 243, 208));
                    } else {
                        comp.setBackground(com.ems.util.AppTheme.SURFACE_BG);
                        comp.setForeground(com.ems.util.AppTheme.TEXT);
                    }
                }
                return comp;
            }
        });

        // Checkbox renderer matching row background
        attTable.getColumnModel().getColumn(3).setCellRenderer((t, val, sel, foc, r, c) -> {
            JCheckBox box = new JCheckBox();
            box.setHorizontalAlignment(SwingConstants.CENTER);
            box.setSelected(Boolean.TRUE.equals(val));
            if (sel) {
                box.setBackground(t.getSelectionBackground());
            } else {
                String status = (String) t.getModel().getValueAt(r, 2);
                if ("Absent".equalsIgnoreCase(status)) {
                    box.setBackground(new Color(69, 10, 10));
                } else if ("Present".equalsIgnoreCase(status)) {
                    box.setBackground(new Color(6, 78, 59));
                } else {
                    box.setBackground(com.ems.util.AppTheme.SURFACE_BG);
                }
            }
            return box;
        });

        mpModel = new DefaultTableModel(new Object[]{"ID","USN","Name","Room","Incident","Description","Reported At"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable mpTable = new JTable(mpModel);
        UiUtil.styleTable(mpTable);

        JTabbedPane tabs = new JTabbedPane();
        JPanel attCard = UiUtil.buildSurfaceCard(); attCard.setLayout(new BorderLayout()); attCard.add(new JScrollPane(attTable), BorderLayout.CENTER);
        JPanel mpCard  = UiUtil.buildSurfaceCard(); mpCard.setLayout(new BorderLayout());  mpCard.add(new JScrollPane(mpTable), BorderLayout.CENTER);
        tabs.addTab("Attendance", attCard);
        tabs.addTab("Malpractice", mpCard);

        JPanel stack = new JPanel(new BorderLayout(0, 14));
        stack.setOpaque(false);
        stack.add(formCard, BorderLayout.NORTH);
        stack.add(tabs, BorderLayout.CENTER);
        add(stack, BorderLayout.CENTER);

        // ---- Listeners ----
        java.awt.event.ActionListener loadUsnAction = e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                List<String[]> allocations = allocationService.forExam(examId);
                usnBox.removeAllItems();
                for (String[] row : allocations) {
                    usnBox.addItem(row[1]);
                }
            } catch (Exception ignored) {}
        };
        examIdField.addActionListener(loadUsnAction);
        examIdField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                loadUsnAction.actionPerformed(null);
            }
        });

        loadBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                loadUsnAction.actionPerformed(null);
                loadAtt(examId);
                tabs.setSelectedIndex(0);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        markAllPresentBtn.addActionListener(e -> {
            int rowCount = attModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                attModel.setValueAt(Boolean.TRUE, i, 3);
            }
        });

        markAllAbsentBtn.addActionListener(e -> {
            int rowCount = attModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                attModel.setValueAt(Boolean.FALSE, i, 3);
            }
        });

        saveAllBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                int rowCount = attModel.getRowCount();
                if (rowCount == 0) {
                    UiUtil.info(this, "No students loaded for Exam #" + examId);
                    return;
                }
                int saved = 0;
                for (int i = 0; i < rowCount; i++) {
                    String usn = (String) attModel.getValueAt(i, 0);
                    Boolean isPresent = (Boolean) attModel.getValueAt(i, 3);
                    if (usn != null && isPresent != null) {
                        service.mark(usn, examId, isPresent);
                        saved++;
                    }
                }
                UiUtil.info(this, "Successfully saved attendance for " + saved + " student(s)!");
                loadAtt(examId);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        mpBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                String reporterText = reporterField.getText().trim();
                Integer reporterId = reporterText.isEmpty() ? null : Integer.parseInt(reporterText);
                String selectedUsn = (String) usnBox.getSelectedItem();
                if (selectedUsn == null) throw new IllegalArgumentException("No USN selected");
                service.logMalpractice(selectedUsn, examId,
                        roomField.getText().trim(),
                        (String) mpTypeBox.getSelectedItem(),
                        mpDescField.getText().trim(),
                        reporterId);
                UiUtil.info(this, "Malpractice incident logged");
                loadMp(examId);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        viewMpBtn.addActionListener(e -> {
            try {
                loadMp(Integer.parseInt(examIdField.getText().trim()));
                tabs.setSelectedIndex(1);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });
    }

    private void loadAtt(int examId) throws Exception {
        List<String[]> rows = service.report(examId);
        attModel.setRowCount(0);
        for (String[] r : rows) {
            String usn = r[0];
            String name = r[1];
            String status = r[2];
            boolean isPresent = !"Absent".equalsIgnoreCase(status);
            attModel.addRow(new Object[]{usn, name, status, Boolean.valueOf(isPresent)});
        }
    }

    private void loadMp(int examId) throws Exception {
        List<String[]> rows = service.malpracticeForExam(examId);
        mpModel.setRowCount(0);
        for (String[] r : rows) mpModel.addRow(r);
    }
}
