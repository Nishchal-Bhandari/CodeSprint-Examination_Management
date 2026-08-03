package com.ems.ui.panels;

import com.ems.service.AttendanceService;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Attendance & Conduct panel – per-seat attendance, malpractice log, washroom log.
 */
public class AttendanceConductPanel extends JPanel {
    private final AttendanceService service = new AttendanceService();
    private final com.ems.service.AllocationService allocationService = new com.ems.service.AllocationService();
    private final DefaultTableModel attModel;
    private final DefaultTableModel mpModel;
    private final DefaultTableModel wrModel;

    public AttendanceConductPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Attendance & Conduct",
                "Mark per-seat attendance, log malpractice incidents, and track washroom exits"
        ), BorderLayout.NORTH);

        // ---- Top form card ----
        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField examIdField = new JTextField(8); UiUtil.styleInput(examIdField, 100); UiUtil.allowDigitsOnly(examIdField);
        JComboBox<String> usnBox = new JComboBox<>();
        usnBox.setFont(com.ems.util.AppTheme.FONT_BODY);
        usnBox.setPreferredSize(new Dimension(140, 38));
        
        JComboBox<String> presentBox = new JComboBox<>(new String[]{"Present","Absent"});
        presentBox.setFont(com.ems.util.AppTheme.FONT_BODY);
        presentBox.setPreferredSize(new Dimension(120, 38));

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

        // Row 0: Exam ID + USN + status
        gbc.gridy = 0;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Exam ID"), gbc);
        gbc.gridx = 1; form.add(examIdField, gbc);
        gbc.gridx = 2; form.add(UiUtil.buildLabel("USN"), gbc);
        gbc.gridx = 3; form.add(usnBox, gbc);
        gbc.gridx = 4; form.add(UiUtil.buildLabel("Status"), gbc);
        gbc.gridx = 5; form.add(presentBox, gbc);

        // Attendance buttons
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.EAST;
        JButton markBtn = UiUtil.buildPrimaryButton("Mark Attendance");
        JButton loadBtn = UiUtil.buildSecondaryButton("Load Attendance");
        JPanel attRow = UiUtil.buildActionRow();
        attRow.add(markBtn); attRow.add(loadBtn);
        form.add(attRow, gbc);
        
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

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

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        // Row 5: Washroom
        gbc.gridy = 5;
        JComboBox<String> wrUsnBox = new JComboBox<>();
        wrUsnBox.setFont(com.ems.util.AppTheme.FONT_BODY);
        wrUsnBox.setPreferredSize(new Dimension(140, 38));
        
        JTextField wrIdField  = new JTextField(8);  UiUtil.styleInput(wrIdField, 100); UiUtil.allowDigitsOnly(wrIdField);
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Washroom USN"), gbc);
        gbc.gridx = 1; form.add(wrUsnBox, gbc);
        gbc.gridx = 2; form.add(UiUtil.buildLabel("Return WL ID"), gbc);
        gbc.gridx = 3; form.add(wrIdField, gbc);
        
        // Row 6: Washroom buttons
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.EAST;
        JButton wrExitBtn = UiUtil.buildSecondaryButton("Log Exit");
        JButton wrReturnBtn = UiUtil.buildSecondaryButton("Mark Return");
        JButton viewWrBtn = UiUtil.buildSecondaryButton("View Washroom Log");
        JPanel wrRow = UiUtil.buildActionRow();
        wrRow.add(wrExitBtn); wrRow.add(wrReturnBtn); wrRow.add(viewWrBtn);
        form.add(wrRow, gbc);

        formCard.add(form, BorderLayout.CENTER);

        // ---- Tables ----
        attModel = new DefaultTableModel(new Object[]{"USN","Name","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable attTable = new JTable(attModel);
        UiUtil.styleTable(attTable);
        // Colour-code rows
        attTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String status = (String) t.getModel().getValueAt(r, 2);
                    if ("Absent".equals(status)) comp.setBackground(new Color(255, 235, 235));
                    else if ("Present".equals(status)) comp.setBackground(new Color(230, 255, 240));
                    else comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        });

        mpModel = new DefaultTableModel(new Object[]{"ID","USN","Name","Room","Incident","Description","Reported At"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable mpTable = new JTable(mpModel);
        UiUtil.styleTable(mpTable);

        wrModel = new DefaultTableModel(new Object[]{"WL ID","USN","Name","Exit Time","Return Time"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable wrTable = new JTable(wrModel);
        UiUtil.styleTable(wrTable);

        JTabbedPane tabs = new JTabbedPane();
        JPanel attCard = UiUtil.buildSurfaceCard(); attCard.setLayout(new BorderLayout()); attCard.add(new JScrollPane(attTable), BorderLayout.CENTER);
        JPanel mpCard  = UiUtil.buildSurfaceCard(); mpCard.setLayout(new BorderLayout());  mpCard.add(new JScrollPane(mpTable), BorderLayout.CENTER);
        JPanel wrCard  = UiUtil.buildSurfaceCard(); wrCard.setLayout(new BorderLayout());  wrCard.add(new JScrollPane(wrTable), BorderLayout.CENTER);
        tabs.addTab("Attendance", attCard);
        tabs.addTab("Malpractice", mpCard);
        tabs.addTab("Washroom Log", wrCard);

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
                wrUsnBox.removeAllItems();
                for (String[] row : allocations) {
                    usnBox.addItem(row[1]);
                    wrUsnBox.addItem(row[1]);
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

        markBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                boolean present = "Present".equals(presentBox.getSelectedItem());
                String selectedUsn = (String) usnBox.getSelectedItem();
                if (selectedUsn == null) throw new IllegalArgumentException("No USN selected");
                service.mark(selectedUsn, examId, present);
                UiUtil.info(this, "Attendance marked");
                loadAtt(examId);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        loadBtn.addActionListener(e -> {
            try {
                loadAtt(Integer.parseInt(examIdField.getText().trim()));
                tabs.setSelectedIndex(0);
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

        wrExitBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                String selectedWrUsn = (String) wrUsnBox.getSelectedItem();
                if (selectedWrUsn == null) throw new IllegalArgumentException("No Washroom USN selected");
                service.logWashroomExit(selectedWrUsn, examId);
                UiUtil.info(this, "Washroom exit logged");
                loadWr(examId);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        wrReturnBtn.addActionListener(e -> {
            try {
                long wlId = Long.parseLong(wrIdField.getText().trim());
                service.returnFromWashroom(wlId);
                UiUtil.info(this, "Return marked");
                loadWr(Integer.parseInt(examIdField.getText().trim()));
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("WL ID and Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        viewWrBtn.addActionListener(e -> {
            try {
                loadWr(Integer.parseInt(examIdField.getText().trim()));
                tabs.setSelectedIndex(2);
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
        for (String[] r : rows) attModel.addRow(r);
    }

    private void loadMp(int examId) throws Exception {
        List<String[]> rows = service.malpracticeForExam(examId);
        mpModel.setRowCount(0);
        for (String[] r : rows) mpModel.addRow(r);
    }

    private void loadWr(int examId) throws Exception {
        List<String[]> rows = service.washroomLog(examId);
        wrModel.setRowCount(0);
        for (String[] r : rows) wrModel.addRow(r);
    }
}
