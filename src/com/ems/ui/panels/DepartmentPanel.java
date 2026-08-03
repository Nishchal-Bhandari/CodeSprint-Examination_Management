package com.ems.ui.panels;

import com.ems.model.Department;
import com.ems.service.DepartmentService;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.BorderFactory;
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
import java.util.List;

public class DepartmentPanel extends JPanel {
    private final DepartmentService service = new DepartmentService();
    private final DefaultTableModel model;

    public DepartmentPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
            "Department Management",
            "View all departments and add or delete them with confidence"
        ), BorderLayout.NORTH);

        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField idField = new JTextField(6);
        JTextField nameField = new JTextField(20);
        UiUtil.styleInput(idField, 120);
        UiUtil.styleInput(nameField, 300);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,8,6,8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(UiUtil.buildLabel("Dept ID"), gbc);
        gbc.gridx = 1; form.add(idField, gbc);
        gbc.gridx = 2; form.add(UiUtil.buildLabel("Name"), gbc);
        gbc.gridx = 3; form.add(nameField, gbc);

        javax.swing.JButton addBtn = UiUtil.buildPrimaryButton("Add");
        javax.swing.JButton deleteBtn = UiUtil.buildSecondaryButton("Delete by ID");
        javax.swing.JButton refreshBtn = UiUtil.buildSecondaryButton("Refresh");

        JPanel actions = UiUtil.buildActionRow();
        actions.add(addBtn); actions.add(deleteBtn); actions.add(refreshBtn);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(actions, gbc);

        formCard.add(form, BorderLayout.CENTER);

        model = new DefaultTableModel(new Object[]{"Dept ID", "Dept Name"}, 0);
        JTable table = new JTable(model);
        UiUtil.styleTable(table);
        table.setPreferredScrollableViewportSize(new Dimension(600, 400));

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
                if (idText.isEmpty() || name.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("Dept ID and Name are required"));
                    return;
                }
                int id = Integer.parseInt(idText);
                Department d = new Department(id, name);
                service.add(d);
                UiUtil.info(this, "Department added");
                refresh();
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Dept ID must be a number"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                int res = javax.swing.JOptionPane.showConfirmDialog(this, "Delete department " + id + "?", "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
                if (res == javax.swing.JOptionPane.YES_OPTION) {
                    service.delete(id);
                    UiUtil.info(this, "Department deleted");
                    refresh();
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        refresh();
    }

    private void refresh() {
        try {
            model.setRowCount(0);
            List<Department> list = service.all();
            for (Department d : list) {
                model.addRow(new Object[]{d.getDeptId(), d.getDeptName()});
            }
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }
}
