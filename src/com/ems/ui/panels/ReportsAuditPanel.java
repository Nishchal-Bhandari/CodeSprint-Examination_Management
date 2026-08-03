package com.ems.ui.panels;

import com.ems.service.ReportService;
import com.ems.service.AuditLogService;
import com.ems.model.AuditLog;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Reports & Audit panel – absentee, malpractice, room-wise, faculty duty, and audit trail.
 */
public class ReportsAuditPanel extends JPanel {
    private final ReportService reportService = new ReportService();
    private final AuditLogService auditService = new AuditLogService();

    private final DefaultTableModel absenteeModel;
    private final DefaultTableModel mpModel;
    private final DefaultTableModel dutyModel;
    private final DefaultTableModel auditModel;

    public ReportsAuditPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Reports & Audit",
                "Absentee report, malpractice summary, faculty duty log, and full audit trail"
        ), BorderLayout.NORTH);

        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField examIdField   = new JTextField(8); UiUtil.styleInput(examIdField, 130); UiUtil.allowDigitsOnly(examIdField);
        JTextField usernameField = new JTextField(14); UiUtil.styleInput(usernameField, 180);
        JTextField purgeDaysField= new JTextField(6); UiUtil.styleInput(purgeDaysField, 100); UiUtil.allowDigitsOnly(purgeDaysField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,8,6,8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Exam ID"), gbc);
        gbc.gridx = 1; form.add(examIdField, gbc);

        JButton absenteeBtn  = UiUtil.buildPrimaryButton("Absentee Report");
        JButton mpBtn        = UiUtil.buildSecondaryButton("Malpractice Summary");
        JButton dutyBtn      = UiUtil.buildSecondaryButton("Faculty Duty Log");
        JButton auditAllBtn  = UiUtil.buildSecondaryButton("Full Audit Trail");

        JPanel row0 = UiUtil.buildActionRow();
        row0.add(absenteeBtn); row0.add(mpBtn); row0.add(dutyBtn); row0.add(auditAllBtn);
        gbc.gridx = 2; gbc.gridwidth = 6; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(row0, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Filter by User"), gbc);
        gbc.gridx = 1; form.add(usernameField, gbc);
        gbc.gridx = 2; form.add(UiUtil.buildLabel("Purge Older Than (days)"), gbc);
        gbc.gridx = 3; form.add(purgeDaysField, gbc);
        JButton filterAuditBtn = UiUtil.buildSecondaryButton("Filter Audit");
        JButton purgeBtn       = UiUtil.buildSecondaryButton("Purge Audit");
        JPanel row1 = UiUtil.buildActionRow();
        row1.add(filterAuditBtn); row1.add(purgeBtn);
        gbc.gridx = 4; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(row1, gbc);

        formCard.add(form, BorderLayout.CENTER);

        // ---- Tables ----
        absenteeModel = new DefaultTableModel(new Object[]{"USN","Name","Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable absenteeTable = new JTable(absenteeModel);
        UiUtil.styleTable(absenteeTable);
        absenteeTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    String status = (String) t.getModel().getValueAt(r, 2);
                    comp.setBackground("Absent".equals(status) ? new Color(255, 225, 225) : Color.WHITE);
                }
                return comp;
            }
        });

        mpModel = new DefaultTableModel(new Object[]{"ID","USN","Name","Exam","Room","Incident","Description","Reported At"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable mpTable = new JTable(mpModel);
        UiUtil.styleTable(mpTable);

        dutyModel = new DefaultTableModel(new Object[]{"Duty ID","Exam","Room","Role","Faculty"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable dutyTable = new JTable(dutyModel);
        UiUtil.styleTable(dutyTable);

        auditModel = new DefaultTableModel(new Object[]{"Audit ID","User","Action","Table","Key","Old Value","New Value","Time"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable auditTable = new JTable(auditModel);
        UiUtil.styleTable(auditTable);

        JTabbedPane tabs = new JTabbedPane();
        JPanel abCard  = UiUtil.buildSurfaceCard(); abCard.setLayout(new BorderLayout());   abCard.add(new JScrollPane(absenteeTable), BorderLayout.CENTER);
        JPanel mpCard  = UiUtil.buildSurfaceCard(); mpCard.setLayout(new BorderLayout());   mpCard.add(new JScrollPane(mpTable), BorderLayout.CENTER);
        JPanel duCard  = UiUtil.buildSurfaceCard(); duCard.setLayout(new BorderLayout());   duCard.add(new JScrollPane(dutyTable), BorderLayout.CENTER);
        JPanel auCard  = UiUtil.buildSurfaceCard(); auCard.setLayout(new BorderLayout());   auCard.add(new JScrollPane(auditTable), BorderLayout.CENTER);

        tabs.addTab("Absentee Report", abCard);
        tabs.addTab("Malpractice Summary", mpCard);
        tabs.addTab("Faculty Duty Log", duCard);
        tabs.addTab("Audit Trail", auCard);

        JPanel stack = new JPanel(new BorderLayout(0, 14));
        stack.setOpaque(false);
        stack.add(formCard, BorderLayout.NORTH);
        stack.add(tabs, BorderLayout.CENTER);
        add(stack, BorderLayout.CENTER);

        // ---- Listeners ----
        absenteeBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                List<String[]> rows = reportService.absenteeReport(examId);
                absenteeModel.setRowCount(0);
                for (String[] r : rows) absenteeModel.addRow(r);
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
                List<String[]> rows = reportService.malpracticeSummary(examId);
                mpModel.setRowCount(0);
                for (String[] r : rows) mpModel.addRow(r);
                tabs.setSelectedIndex(1);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        dutyBtn.addActionListener(e -> {
            try {
                List<String[]> rows = reportService.facultyDutyLog();
                dutyModel.setRowCount(0);
                for (String[] r : rows) dutyModel.addRow(r);
                tabs.setSelectedIndex(2);
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        auditAllBtn.addActionListener(e -> {
            try {
                List<AuditLog> logs = auditService.all();
                auditModel.setRowCount(0);
                for (AuditLog l : logs) {
                    auditModel.addRow(new Object[]{
                            l.getAuditId(), l.getUsername(), l.getAction(),
                            l.getTableName(), l.getRecordKey(),
                            l.getOldValue(), l.getNewValue(), l.getLoggedAt()
                    });
                }
                tabs.setSelectedIndex(3);
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        filterAuditBtn.addActionListener(e -> {
            try {
                String user = usernameField.getText().trim();
                List<AuditLog> logs = user.isEmpty() ? auditService.all() : auditService.byUser(user);
                auditModel.setRowCount(0);
                for (AuditLog l : logs) {
                    auditModel.addRow(new Object[]{
                            l.getAuditId(), l.getUsername(), l.getAction(),
                            l.getTableName(), l.getRecordKey(),
                            l.getOldValue(), l.getNewValue(), l.getLoggedAt()
                    });
                }
                tabs.setSelectedIndex(3);
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        purgeBtn.addActionListener(e -> {
            try {
                String daysText = purgeDaysField.getText().trim();
                if (daysText.isEmpty()) { UiUtil.error(this, new IllegalArgumentException("Enter number of days")); return; }
                int days = Integer.parseInt(daysText);
                int res = JOptionPane.showConfirmDialog(this,
                        "Permanently delete audit logs older than " + days + " days?",
                        "Confirm Purge", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    auditService.purgeOlderThan(days);
                    UiUtil.info(this, "Audit logs purged");
                }
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Days must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });
    }
}
