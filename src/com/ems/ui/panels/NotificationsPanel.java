package com.ems.ui.panels;

import com.ems.model.Notification;
import com.ems.service.NotificationService;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Notifications panel – compose and dispatch hall ticket alerts, timetable updates, duty reminders, etc.
 */
public class NotificationsPanel extends JPanel {
    private final NotificationService service = new NotificationService();
    private final DefaultTableModel model;

    public NotificationsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Notifications",
                "Dispatch hall ticket alerts, timetable updates, room changes, duty reminders, and malpractice notices"
        ), BorderLayout.NORTH);

        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JComboBox<String> typeBox = new JComboBox<>(new String[]{
                "HALL_TICKET","TIMETABLE","ROOM_CHANGE","DUTY_REMINDER","MALPRACTICE"
        });
        typeBox.setFont(AppTheme.FONT_BODY);
        typeBox.setPreferredSize(new Dimension(180, 38));

        JComboBox<String> roleBox = new JComboBox<>(new String[]{
                "ALL","ADMIN","EXAM_CELL","FACULTY","STUDENT"
        });
        roleBox.setFont(AppTheme.FONT_BODY);
        roleBox.setPreferredSize(new Dimension(150, 38));

        JTextField titleField  = new JTextField(24); UiUtil.styleInput(titleField, 320);
        JTextField usnField    = new JTextField(14); UiUtil.styleInput(usnField, 180);

        JTextArea bodyArea = new JTextArea(3, 40);
        bodyArea.setFont(AppTheme.FONT_BODY);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        bodyArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 186, 205)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        JScrollPane bodyScroll = new JScrollPane(bodyArea);
        bodyScroll.setPreferredSize(new Dimension(500, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,8,6,8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Type"), gbc);
        gbc.gridx = 1; form.add(typeBox, gbc);
        gbc.gridx = 2; form.add(UiUtil.buildLabel("Target Role"), gbc);
        gbc.gridx = 3; form.add(roleBox, gbc);
        gbc.gridx = 4; form.add(UiUtil.buildLabel("Target USN (opt.)"), gbc);
        gbc.gridx = 5; form.add(usnField, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Title"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(titleField, gbc);

        gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Body"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 5; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(bodyScroll, gbc);

        JButton createBtn  = UiUtil.buildPrimaryButton("Create Notification");
        JButton sendBtn    = UiUtil.buildPrimaryButton("Mark as Sent");
        JButton deleteBtn  = UiUtil.buildSecondaryButton("Delete");
        JButton refreshBtn = UiUtil.buildSecondaryButton("Refresh");

        JPanel actions = UiUtil.buildActionRow();
        actions.add(createBtn); actions.add(sendBtn); actions.add(deleteBtn); actions.add(refreshBtn);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 6; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(actions, gbc);

        formCard.add(form, BorderLayout.CENTER);

        // ---- Status legend ----
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        legend.setOpaque(false);
        JLabel sentLbl = new JLabel("● Sent");
        sentLbl.setFont(AppTheme.FONT_CAPTION);
        sentLbl.setForeground(AppTheme.ACCENT);
        JLabel unsentLbl = new JLabel("● Pending");
        unsentLbl.setFont(AppTheme.FONT_CAPTION);
        unsentLbl.setForeground(AppTheme.WARNING);
        legend.add(sentLbl);
        legend.add(unsentLbl);

        // ---- Table ----
        model = new DefaultTableModel(new Object[]{
                "ID","Type","Title","Target Role","Target USN","Sent","Created At","Sent At"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UiUtil.styleTable(table);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    Object sentVal = t.getModel().getValueAt(r, 5);
                    boolean isSent = Boolean.TRUE.equals(sentVal) || "true".equals(String.valueOf(sentVal));
                    comp.setBackground(isSent ? new Color(230, 255, 240) : new Color(255, 250, 230));
                }
                return comp;
            }
        });

        JPanel tableCard = UiUtil.buildSurfaceCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(legend, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel stack = new JPanel(new BorderLayout(0, 14));
        stack.setOpaque(false);
        stack.add(formCard, BorderLayout.NORTH);
        stack.add(tableCard, BorderLayout.CENTER);
        add(stack, BorderLayout.CENTER);

        // ---- Listeners ----
        createBtn.addActionListener(e -> {
            try {
                service.create(
                        (String) typeBox.getSelectedItem(),
                        titleField.getText().trim(),
                        bodyArea.getText().trim(),
                        (String) roleBox.getSelectedItem(),
                        usnField.getText().trim()
                );
                UiUtil.info(this, "Notification created");
                refresh();
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        sendBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) { UiUtil.error(this, "Select a notification to mark as sent"); return; }
            try {
                long notifId = (Long) model.getValueAt(sel, 0);
                service.markSent(notifId);
                UiUtil.info(this, "Marked as sent");
                refresh();
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        deleteBtn.addActionListener(e -> {
            int sel = table.getSelectedRow();
            if (sel < 0) { UiUtil.error(this, "Select a notification to delete"); return; }
            try {
                long notifId = (Long) model.getValueAt(sel, 0);
                int res = JOptionPane.showConfirmDialog(this, "Delete this notification?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    service.delete(notifId);
                    UiUtil.info(this, "Notification deleted");
                    refresh();
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        refreshBtn.addActionListener(e -> refresh());

        refresh();
    }

    private void refresh() {
        try {
            List<Notification> list = service.all();
            model.setRowCount(0);
            for (Notification n : list) {
                model.addRow(new Object[]{
                        n.getNotifId(), n.getNotifType(), n.getTitle(),
                        n.getTargetRole(), n.getTargetUsn(),
                        n.isSent(), n.getCreatedAt(), n.getSentAt()
                });
            }
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }
}
