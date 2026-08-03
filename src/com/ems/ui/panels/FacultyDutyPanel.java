package com.ems.ui.panels;

import com.ems.service.FacultyDutyService;
import com.ems.util.AnimationEngine;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class FacultyDutyPanel extends JPanel {
    private final FacultyDutyService service = new FacultyDutyService();
    private final DefaultTableModel model;

    private final JLabel kpiDutiesLbl;
    private final JLabel kpiFacultyLbl;
    private final JLabel kpiDeptConstraintLbl;

    public FacultyDutyPanel() {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Smart Faculty Invigilator Allocation",
                "Automated invigilator duty assignment with Department Constraint Engine & Manual Coordinator Swap"
        ), BorderLayout.NORTH);

        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(6, 6, 12, 6));

        JTextField examIdField = new JTextField("5001", 6);
        JTextField roomNoField = new JTextField("A101", 6);
        JTextField roleField = new JTextField("INVIGILATOR", 12);
        JTextField countField = new JTextField("1", 4);

        UiUtil.styleInput(examIdField, 110);
        UiUtil.styleInput(roomNoField, 130);
        UiUtil.styleInput(roleField, 180);
        UiUtil.styleInput(countField, 90);
        UiUtil.allowDigitsOnly(examIdField);
        UiUtil.allowDigitsOnly(countField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(UiUtil.buildLabel("Exam ID"), gbc);
        gbc.gridx = 1;
        form.add(examIdField, gbc);

        gbc.gridx = 2;
        form.add(UiUtil.buildLabel("Room No"), gbc);
        gbc.gridx = 3;
        form.add(roomNoField, gbc);

        gbc.gridx = 4;
        form.add(UiUtil.buildLabel("Role"), gbc);
        gbc.gridx = 5;
        form.add(roleField, gbc);

        gbc.gridx = 6;
        form.add(UiUtil.buildLabel("Required Count"), gbc);
        gbc.gridx = 7;
        form.add(countField, gbc);

        JButton assignBtn = UiUtil.buildPrimaryButton("⚡ Auto Assign Invigilator");
        JButton manualAssignBtn = UiUtil.buildSecondaryButton("➕ Manual Assign");
        JButton swapBtn = UiUtil.buildSecondaryButton("🔄 Swap Invigilators");
        JButton refreshBtn = UiUtil.buildSecondaryButton("🔄 Refresh");
        JButton clearBtn = UiUtil.buildSecondaryButton("🗑 Clear Duties");

        JPanel actions = UiUtil.buildActionRow();
        actions.add(assignBtn);
        actions.add(manualAssignBtn);
        actions.add(swapBtn);
        actions.add(refreshBtn);
        actions.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 8;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(actions, gbc);

        formCard.add(form, BorderLayout.CENTER);

        // KPI Summary Panel
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        kpiPanel.setOpaque(false);

        kpiDutiesLbl = new JLabel("0", SwingConstants.LEFT);
        kpiFacultyLbl = new JLabel("0", SwingConstants.LEFT);
        kpiDeptConstraintLbl = new JLabel("ACTIVE", SwingConstants.LEFT);

        kpiPanel.add(createKpiCard("📋 Total Duties Assigned", kpiDutiesLbl, AppTheme.PRIMARY_LIGHT));
        kpiPanel.add(createKpiCard("👨‍🏫 Unique Faculty Assigned", kpiFacultyLbl, AppTheme.ACCENT));
        kpiPanel.add(createKpiCard("🛡 Department Conflict Shield", kpiDeptConstraintLbl, AppTheme.SUCCESS));

        // Table
        model = new DefaultTableModel(new Object[]{"Duty ID", "Exam ID", "Room No", "Role", "Faculty ID", "Faculty Name", "Faculty Department"}, 0);
        JTable table = new JTable(model);
        UiUtil.styleTable(table);

        JPanel tableCard = UiUtil.buildSurfaceCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel topStack = new JPanel(new BorderLayout(0, 14));
        topStack.setOpaque(false);
        topStack.add(formCard, BorderLayout.NORTH);
        topStack.add(kpiPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);
        centerPanel.add(topStack, BorderLayout.NORTH);
        centerPanel.add(tableCard, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Listeners
        assignBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                String roomNo = roomNoField.getText().trim();
                String role = roleField.getText().trim();
                int reqCount = Integer.parseInt(countField.getText().trim());

                assignBtn.setEnabled(false);
                assignBtn.setText("Assigning…");

                SwingWorker<String, Void> worker = new SwingWorker<>() {
                    @Override protected String doInBackground() throws Exception {
                        return service.autoAssign(examId, roomNo, role, reqCount);
                    }
                    @Override protected void done() {
                        assignBtn.setEnabled(true);
                        assignBtn.setText("⚡ Auto Assign Invigilator");
                        try {
                            String msg = get();
                            UiUtil.info(FacultyDutyPanel.this, msg);
                            refresh();
                        } catch (Exception ex) {
                            UiUtil.error(FacultyDutyPanel.this, ex);
                        }
                    }
                };
                worker.execute();
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID and Required Count must be numeric"));
            }
        });

        manualAssignBtn.addActionListener(e -> showManualAssignDialog());

        swapBtn.addActionListener(e -> showSwapDialog());

        refreshBtn.addActionListener(e -> refresh());

        clearBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                int deleted = service.clearExam(examId);
                UiUtil.info(this, "Cleared " + deleted + " faculty duty record(s)");
                refresh();
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        refresh();
    }

    private JPanel createKpiCard(String title, JLabel valueLbl, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PANEL_BG,
                        getWidth(), getHeight(), new Color(AppTheme.PANEL_BG.getRed() + 8,
                        AppTheme.PANEL_BG.getGreen() + 10, AppTheme.PANEL_BG.getBlue() + 15));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 3, 3, 3));
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(16, 16, 12, 16));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(AppTheme.FONT_CAPTION);
        tLbl.setForeground(AppTheme.TEXT_LIGHT);

        valueLbl.setFont(AppTheme.FONT_KPI);
        valueLbl.setForeground(accent);

        card.add(tLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLbl);

        return card;
    }

    private void refresh() {
        try {
            List<String[]> rows = service.all();
            model.setRowCount(0);
            for (String[] row : rows) model.addRow(row);
            AnimationEngine.animateCounter(kpiDutiesLbl, 0, rows.size(), AppTheme.ANIM_SLOW);
            long uniqueFaculty = rows.stream().map(r -> r[4]).distinct().count();
            AnimationEngine.animateCounter(kpiFacultyLbl, 0, (int) uniqueFaculty, AppTheme.ANIM_SLOW);
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }

    private void showManualAssignDialog() {
        JTextField examIdF = new JTextField("5001", 10);
        JTextField roomNoF = new JTextField("A101", 10);
        JTextField facultyIdF = new JTextField("101", 10);
        JTextField roleF = new JTextField("INVIGILATOR", 10);

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 8));
        panel.add(new JLabel("Exam ID:")); panel.add(examIdF);
        panel.add(new JLabel("Room No:")); panel.add(roomNoF);
        panel.add(new JLabel("Faculty ID:")); panel.add(facultyIdF);
        panel.add(new JLabel("Role:")); panel.add(roleF);

        int res = JOptionPane.showConfirmDialog(
                this, panel, "Coordinator Manual Invigilator Assignment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (res == JOptionPane.OK_OPTION) {
            try {
                int examId = Integer.parseInt(examIdF.getText().trim());
                String roomNo = roomNoF.getText().trim();
                int facultyId = Integer.parseInt(facultyIdF.getText().trim());
                String role = roleF.getText().trim();

                boolean ok = service.manualAssign(examId, roomNo, facultyId, role);
                if (ok) {
                    UiUtil.info(this, "Manually assigned Faculty #" + facultyId + " to Room " + roomNo);
                    refresh();
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        }
    }

    private void showSwapDialog() {
        JTextField dutyId1F = new JTextField(10);
        JTextField dutyId2F = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("First Duty ID:")); panel.add(dutyId1F);
        panel.add(new JLabel("Second Duty ID:")); panel.add(dutyId2F);

        int res = JOptionPane.showConfirmDialog(
                this, panel, "Coordinator Invigilator Swap", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (res == JOptionPane.OK_OPTION) {
            try {
                long d1 = Long.parseLong(dutyId1F.getText().trim());
                long d2 = Long.parseLong(dutyId2F.getText().trim());

                boolean ok = service.swapDuties(d1, d2);
                if (ok) {
                    UiUtil.info(this, "Successfully swapped invigilators for Duty #" + d1 + " and Duty #" + d2);
                    refresh();
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        }
    }
}
