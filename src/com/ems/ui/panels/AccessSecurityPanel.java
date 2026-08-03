package com.ems.ui.panels;

import com.ems.service.AuditLogService;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Access & Security panel – role-based info, session audit, exam period freeze controls,
 * backup/recovery helpers, and data edit lockdown toggle.
 */
public class AccessSecurityPanel extends JPanel {
    private final AuditLogService auditService = new AuditLogService();

    private final DefaultTableModel sessionModel;
    private boolean examPeriodLocked = false;

    public AccessSecurityPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Access & Security",
                "Role-based permissions, session audit, exam period freeze, and backup & recovery"
        ), BorderLayout.NORTH);

        // ---- Role summary card ----
        JPanel roleCard = UiUtil.buildSurfaceCard();
        roleCard.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel roleLbl = new JLabel("Role Permissions Summary");
        roleLbl.setFont(AppTheme.FONT_SUBTITLE);
        roleLbl.setForeground(AppTheme.PRIMARY_DARK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        roleCard.add(roleLbl, gbc);

        // Permission table inside roleCard
        String[][] permData = {
            {"ADMIN",     "Full access – all modules", "Yes", "Yes"},
            {"EXAM_CELL", "Scheduling, Allocation, Reports", "Yes", "No"},
            {"VIEWER",    "Read-only – Reports only", "No",  "No"},
        };
        DefaultTableModel permModel = new DefaultTableModel(
                new Object[]{"Role","Permissions","Can Edit","Can Delete"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] row : permData) permModel.addRow(row);
        JTable permTable = new JTable(permModel);
        UiUtil.styleTable(permTable);
        permTable.setPreferredScrollableViewportSize(new Dimension(700, 90));

        gbc.gridy = 1; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        roleCard.add(new JScrollPane(permTable), gbc);

        // ---- Controls card ----
        JPanel ctrlCard = UiUtil.buildSurfaceCard();
        ctrlCard.setLayout(new GridBagLayout());

        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.insets = new Insets(8, 12, 8, 12);
        cgbc.anchor = GridBagConstraints.WEST;

        JLabel ctrlLbl = new JLabel("Security Controls");
        ctrlLbl.setFont(AppTheme.FONT_SUBTITLE);
        ctrlLbl.setForeground(AppTheme.PRIMARY_DARK);
        cgbc.gridx = 0; cgbc.gridy = 0; cgbc.gridwidth = 5;
        ctrlCard.add(ctrlLbl, cgbc);

        cgbc.gridy = 1; cgbc.gridwidth = 1;

        // Exam Period Freeze
        JButton freezeBtn = UiUtil.buildPrimaryButton("Toggle Exam Period Freeze");
        JLabel freezeStatusLbl = new JLabel("Status: Inactive");
        freezeStatusLbl.setFont(AppTheme.FONT_BODY);
        freezeStatusLbl.setForeground(AppTheme.ACCENT);
        cgbc.gridx = 0; ctrlCard.add(freezeBtn, cgbc);
        cgbc.gridx = 1; ctrlCard.add(freezeStatusLbl, cgbc);

        // Backup
        JButton backupBtn = UiUtil.buildSecondaryButton("Run Backup (mysqldump)");
        cgbc.gridx = 2; ctrlCard.add(backupBtn, cgbc);

        // Data Edit Lockdown
        JCheckBox lockdownCb = new JCheckBox("Data Edit Lockdown (read-only mode)");
        lockdownCb.setOpaque(false);
        lockdownCb.setFont(AppTheme.FONT_BODY);
        cgbc.gridx = 3; ctrlCard.add(lockdownCb, cgbc);

        // ---- Session audit card ----
        JPanel sessionCard = UiUtil.buildSurfaceCard();
        sessionCard.setLayout(new BorderLayout());

        JLabel sessionLbl = new JLabel("Session & Login Audit", SwingConstants.LEFT);
        sessionLbl.setFont(AppTheme.FONT_SUBTITLE);
        sessionLbl.setForeground(AppTheme.PRIMARY_DARK);
        sessionLbl.setBorder(BorderFactory.createEmptyBorder(8, 12, 4, 0));
        sessionCard.add(sessionLbl, BorderLayout.NORTH);

        sessionModel = new DefaultTableModel(new Object[]{
                "Audit ID","User","Action","Table","Record","Old Value","New Value","Time"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable sessionTable = new JTable(sessionModel);
        UiUtil.styleTable(sessionTable);

        JPanel sessionBottom = new JPanel(new BorderLayout());
        sessionBottom.setOpaque(false);
        sessionBottom.add(new JScrollPane(sessionTable), BorderLayout.CENTER);

        JPanel sessionBtns = UiUtil.buildActionRow();
        JButton loadAuditBtn = UiUtil.buildSecondaryButton("Load Audit Trail");
        JTextField filterUserField = new JTextField(14); UiUtil.styleInput(filterUserField, 180);
        JButton filterBtn = UiUtil.buildSecondaryButton("Filter by User");
        sessionBtns.add(UiUtil.buildLabel("Username:"));
        sessionBtns.add(filterUserField);
        sessionBtns.add(filterBtn);
        sessionBtns.add(loadAuditBtn);
        sessionBottom.add(sessionBtns, BorderLayout.SOUTH);
        sessionCard.add(sessionBottom, BorderLayout.CENTER);

        // ---- Stack ----
        JPanel topPanel = new JPanel(new BorderLayout(0, 14));
        topPanel.setOpaque(false);
        topPanel.add(roleCard, BorderLayout.NORTH);
        topPanel.add(ctrlCard, BorderLayout.CENTER);

        JPanel stack = new JPanel(new BorderLayout(0, 14));
        stack.setOpaque(false);
        stack.add(topPanel, BorderLayout.NORTH);
        stack.add(sessionCard, BorderLayout.CENTER);
        add(stack, BorderLayout.CENTER);

        // ---- Listeners ----
        freezeBtn.addActionListener(e -> {
            examPeriodLocked = !examPeriodLocked;
            freezeStatusLbl.setText("Status: " + (examPeriodLocked ? "FROZEN (read-only)" : "Inactive"));
            freezeStatusLbl.setForeground(examPeriodLocked ? AppTheme.DANGER : AppTheme.ACCENT);
            auditService.log("SYSTEM", examPeriodLocked ? "EXAM_PERIOD_FREEZE_ON" : "EXAM_PERIOD_FREEZE_OFF",
                    null, null, null, null);
            UiUtil.info(this, "Exam period freeze " + (examPeriodLocked ? "ACTIVATED" : "DEACTIVATED"));
        });

        lockdownCb.addActionListener(e -> {
            boolean locked = lockdownCb.isSelected();
            auditService.log("SYSTEM", locked ? "DATA_LOCKDOWN_ON" : "DATA_LOCKDOWN_OFF", null, null, null, null);
            UiUtil.info(this, "Data edit lockdown " + (locked ? "enabled" : "disabled") + ".\n(Enforcement requires per-panel integration.)");
        });

        backupBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select Backup Destination Folder");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String dir = fc.getSelectedFile().getAbsolutePath();
                String file = dir + java.io.File.separator + "ems_backup_" +
                        java.time.LocalDate.now().toString().replace("-","") + ".sql";
                try {
                    com.ems.config.DBConfig cfg = new com.ems.config.DBConfig();
                    ProcessBuilder pb = new ProcessBuilder(
                            "mysqldump",
                            "-u", cfg.getUser(),
                            "-p" + cfg.getPassword(),
                            cfg.getDatabase()
                    );
                    pb.redirectOutput(new java.io.File(file));
                    pb.redirectErrorStream(true);
                    Process p = pb.start();
                    p.waitFor();
                    auditService.log("SYSTEM", "BACKUP", null, null, null, file);
                    UiUtil.info(this, "Backup saved to:\n" + file);
                } catch (Exception ex) {
                    UiUtil.error(this, ex);
                }
            }
        });

        loadAuditBtn.addActionListener(e -> {
            try {
                var logs = auditService.all();
                sessionModel.setRowCount(0);
                for (var l : logs) {
                    sessionModel.addRow(new Object[]{
                            l.getAuditId(), l.getUsername(), l.getAction(),
                            l.getTableName(), l.getRecordKey(),
                            l.getOldValue(), l.getNewValue(), l.getLoggedAt()
                    });
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        filterBtn.addActionListener(e -> {
            try {
                String user = filterUserField.getText().trim();
                var logs = user.isEmpty() ? auditService.all() : auditService.byUser(user);
                sessionModel.setRowCount(0);
                for (var l : logs) {
                    sessionModel.addRow(new Object[]{
                            l.getAuditId(), l.getUsername(), l.getAction(),
                            l.getTableName(), l.getRecordKey(),
                            l.getOldValue(), l.getNewValue(), l.getLoggedAt()
                    });
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });
    }
}
