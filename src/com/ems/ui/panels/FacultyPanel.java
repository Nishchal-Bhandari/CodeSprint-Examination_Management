package com.ems.ui.panels;

import com.ems.model.Faculty;
import com.ems.service.FacultyService;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class FacultyPanel extends JPanel {
    private final FacultyService service = new FacultyService();
    private final DefaultTableModel model;

    private final JLabel kpiTotalLbl = new JLabel("0", SwingConstants.LEFT);
    private final JLabel kpiLightLbl = new JLabel("0", SwingConstants.LEFT);
    private final JLabel kpiModLbl = new JLabel("0", SwingConstants.LEFT);
    private final JLabel kpiFullLbl = new JLabel("0", SwingConstants.LEFT);

    public FacultyPanel() {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Faculty Management",
                "View all faculties, add new faculty records, monitor visual workload, and delete faculty"
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
        javax.swing.JButton updateAvailabilityBtn = UiUtil.buildSecondaryButton("⚡ Update Availability");
        javax.swing.JButton deleteBtn = UiUtil.buildSecondaryButton("Delete by ID");
        javax.swing.JButton refreshBtn = UiUtil.buildSecondaryButton("Refresh");

        JPanel actions = UiUtil.buildActionRow();
        actions.add(addBtn);
        actions.add(updateAvailabilityBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(actions, gbc);

        formCard.add(form, BorderLayout.CENTER);

        // KPI Workload Summary Header
        JPanel kpiPanel = new JPanel(new java.awt.GridLayout(1, 4, 12, 0));
        kpiPanel.setOpaque(false);

        kpiPanel.add(createKpiCard("👥 Total Faculty", kpiTotalLbl, com.ems.util.AppTheme.PRIMARY_LIGHT));
        kpiPanel.add(createKpiCard("🟢 Light Workload (0)", kpiLightLbl, com.ems.util.AppTheme.SUCCESS));
        kpiPanel.add(createKpiCard("🟡 Moderate (1)", kpiModLbl, com.ems.util.AppTheme.WARNING));
        kpiPanel.add(createKpiCard("🔴 Max Cap (2+)", kpiFullLbl, com.ems.util.AppTheme.DANGER));

        model = new DefaultTableModel(new Object[]{"ID", "Name", "Dept", "Workload Visual", "Availability", "Email"}, 0);
        JTable table = new JTable(model);
        UiUtil.styleTable(table);
        table.setRowHeight(32);

        // Populate fields when table row is selected
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = table.getSelectedRow();
                if (r != -1) {
                    idField.setText(String.valueOf(model.getValueAt(r, 0)));
                    nameField.setText(String.valueOf(model.getValueAt(r, 1)));
                    deptField.setText(String.valueOf(model.getValueAt(r, 2)));
                    workloadField.setText(String.valueOf(model.getValueAt(r, 3)));
                    String avail = String.valueOf(model.getValueAt(r, 4));
                    availabilityBox.setSelectedItem(avail);
                    Object emailObj = model.getValueAt(r, 5);
                    emailField.setText(emailObj != null ? emailObj.toString() : "");
                }
            }
        });

        // Visual Workload Bar Renderer
        table.getColumnModel().getColumn(3).setCellRenderer((t, val, sel, foc, r, c) -> {
            int wl = 0;
            if (val instanceof Integer) wl = (Integer) val;
            else if (val != null) {
                try { wl = Integer.parseInt(val.toString()); } catch (Exception ignored) {}
            }
            JProgressBar bar = new JProgressBar(0, 2);
            bar.setValue(Math.min(wl, 2));
            bar.setStringPainted(true);
            bar.setFont(com.ems.util.AppTheme.FONT_CAPTION);
            if (wl == 0) {
                bar.setForeground(new Color(16, 185, 129));
                bar.setString("🟢 0 / 2 (Light)");
            } else if (wl == 1) {
                bar.setForeground(new Color(245, 158, 11));
                bar.setString("🟡 1 / 2 (Moderate)");
            } else {
                bar.setForeground(new Color(239, 68, 68));
                bar.setString("🔴 2 / 2 (Max Cap)");
            }
            if (sel) {
                bar.setBackground(t.getSelectionBackground());
            } else {
                bar.setBackground(new Color(241, 245, 249));
            }
            return bar;
        });

        JPanel tableCard = UiUtil.buildSurfaceCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel topStack = new JPanel(new BorderLayout(0, 14));
        topStack.setOpaque(false);
        topStack.add(formCard, BorderLayout.NORTH);
        topStack.add(kpiPanel, BorderLayout.SOUTH);

        JPanel stack = new JPanel(new BorderLayout(0, 14));
        stack.setOpaque(false);
        stack.add(topStack, BorderLayout.NORTH);
        stack.add(tableCard, BorderLayout.CENTER);

        add(stack, BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> refresh());

        updateAvailabilityBtn.addActionListener(e -> {
            try {
                String idText = idField.getText().trim();
                if (idText.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("Faculty ID is required to update availability"));
                    return;
                }
                int facultyId = Integer.parseInt(idText);
                String availability = (String) availabilityBox.getSelectedItem();
                service.updateAvailability(facultyId, availability);
                UiUtil.info(this, "Faculty #" + facultyId + " availability updated to " + availability);
                refresh();
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Faculty ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

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

    private JPanel createKpiCard(String title, JLabel valueLbl, java.awt.Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(com.ems.util.AppTheme.PANEL_BG);
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(accent);
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), 3, 3, 3));
                g2.setColor(com.ems.util.AppTheme.BORDER);
                g2.setStroke(new java.awt.BasicStroke(0.8f));
                g2.draw(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new javax.swing.BoxLayout(card, javax.swing.BoxLayout.Y_AXIS));
        card.setBorder(new javax.swing.border.EmptyBorder(12, 14, 10, 14));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(com.ems.util.AppTheme.FONT_CAPTION);
        tLbl.setForeground(com.ems.util.AppTheme.TEXT_LIGHT);

        valueLbl.setFont(com.ems.util.AppTheme.FONT_KPI);
        valueLbl.setForeground(accent);

        card.add(tLbl);
        card.add(javax.swing.Box.createVerticalStrut(4));
        card.add(valueLbl);

        return card;
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            int total = 0, light = 0, mod = 0, full = 0;
            for (Faculty faculty : service.all()) {
                total++;
                int wl = faculty.getWorkload();
                if (wl == 0) light++;
                else if (wl == 1) mod++;
                else full++;

                model.addRow(new Object[]{
                        faculty.getFacultyId(),
                        faculty.getFacultyName(),
                        faculty.getDeptId(),
                        faculty.getWorkload(),
                        faculty.getAvailability(),
                        faculty.getEmail()
                });
            }
            com.ems.util.AnimationEngine.animateCounter(kpiTotalLbl, 0, total, com.ems.util.AppTheme.ANIM_SLOW);
            com.ems.util.AnimationEngine.animateCounter(kpiLightLbl, 0, light, com.ems.util.AppTheme.ANIM_SLOW);
            com.ems.util.AnimationEngine.animateCounter(kpiModLbl, 0, mod, com.ems.util.AppTheme.ANIM_SLOW);
            com.ems.util.AnimationEngine.animateCounter(kpiFullLbl, 0, full, com.ems.util.AppTheme.ANIM_SLOW);
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }
}
