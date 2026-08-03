package com.ems.ui.panels;

import com.ems.service.RoomBenchService;
import com.ems.util.AppTheme;
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

public class RoomBenchPanel extends JPanel {
    private final RoomBenchService service = new RoomBenchService();
    private final DefaultTableModel model;

    public RoomBenchPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Room and Bench Setup",
                "Define rooms and benches before seat allocation"
        ), BorderLayout.NORTH);

        JPanel roomCard = UiUtil.buildSurfaceCard();
        roomCard.setLayout(new BorderLayout());

        JPanel roomForm = new JPanel(new GridBagLayout());
        roomForm.setOpaque(false);
        roomForm.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField roomNoField = new JTextField(6);
        JTextField blockField = new JTextField(12);
        JTextField totalBenchesField = new JTextField(5);
        JTextField defaultCapacityField = new JTextField(5);
        JTextField rowsField = new JTextField(5);

        JTextField benchNoField = new JTextField(8);
        JTextField benchRoomField = new JTextField(6);
        JTextField benchCapacityField = new JTextField(5);

        UiUtil.styleInput(roomNoField, 100);
        UiUtil.styleInput(blockField, 150);
        UiUtil.styleInput(totalBenchesField, 80);
        UiUtil.styleInput(defaultCapacityField, 80);
        UiUtil.styleInput(rowsField, 80);
        UiUtil.styleInput(benchNoField, 100);
        UiUtil.styleInput(benchRoomField, 100);
        UiUtil.styleInput(benchCapacityField, 80);
        UiUtil.allowDigitsOnly(totalBenchesField);
        UiUtil.allowDigitsOnly(defaultCapacityField);
        UiUtil.allowDigitsOnly(rowsField);
        UiUtil.allowDigitsOnly(benchCapacityField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        roomForm.add(UiUtil.buildLabel("Room No"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomForm.add(roomNoField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        roomForm.add(UiUtil.buildLabel("Block"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomForm.add(blockField, gbc);
        
        gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        roomForm.add(UiUtil.buildLabel("Benches"), gbc);
        gbc.gridx = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomForm.add(totalBenchesField, gbc);

        // Row 1
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        roomForm.add(UiUtil.buildLabel("Rows"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomForm.add(rowsField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        roomForm.add(UiUtil.buildLabel("Capacity"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomForm.add(defaultCapacityField, gbc);
        
        gbc.gridx = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        javax.swing.JButton addRoomBtn = UiUtil.buildPrimaryButton("Add Room");
        javax.swing.JButton deleteRoomBtn = UiUtil.buildSecondaryButton("Delete Room");
        JPanel roomActions = UiUtil.buildActionRow();
        roomActions.add(addRoomBtn);
        roomActions.add(deleteRoomBtn);
        roomForm.add(roomActions, gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        // Row 2: Manual bench label/inputs
        gbc.gridy = 2;
        gbc.gridx = 0; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        roomForm.add(UiUtil.buildLabel("Bench No"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomForm.add(benchNoField, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        roomForm.add(UiUtil.buildLabel("Room No"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomForm.add(benchRoomField, gbc);
        
        gbc.gridx = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        roomForm.add(UiUtil.buildLabel("Capacity"), gbc);
        gbc.gridx = 5; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        roomForm.add(benchCapacityField, gbc);

        // Row 3: Manual bench actions
        gbc.gridy = 3;
        gbc.gridx = 0; gbc.gridwidth = 6; gbc.anchor = GridBagConstraints.EAST;
        javax.swing.JButton addBenchBtn = UiUtil.buildSecondaryButton("Add Bench");
        javax.swing.JButton deleteBenchBtn = UiUtil.buildSecondaryButton("Delete Bench");
        javax.swing.JButton refreshBtn = UiUtil.buildSecondaryButton("Refresh");
        JPanel benchActions = UiUtil.buildActionRow();
        benchActions.add(addBenchBtn);
        benchActions.add(deleteBenchBtn);
        benchActions.add(refreshBtn);
        roomForm.add(benchActions, gbc);
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        roomCard.add(roomForm, BorderLayout.CENTER);

        model = new DefaultTableModel(new Object[]{"Room", "Block", "Declared Benches", "Created Benches", "Total Seats"}, 0);
        JTable table = new JTable(model);
        UiUtil.styleTable(table);

        JPanel tableCard = UiUtil.buildSurfaceCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel stack = new JPanel(new BorderLayout(0, 14));
        stack.setOpaque(false);
        stack.add(roomCard, BorderLayout.NORTH);
        stack.add(tableCard, BorderLayout.CENTER);

        add(stack, BorderLayout.CENTER);

        addRoomBtn.addActionListener(e -> {
            try {
                String roomNo = roomNoField.getText().trim();
                String block = blockField.getText().trim();
                if (roomNo.isEmpty() || block.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("Room No and Block are required"));
                    return;
                }
                int total = Integer.parseInt(totalBenchesField.getText().trim());
                int cap = Integer.parseInt(defaultCapacityField.getText().trim());
                String rowsText = rowsField.getText().trim();
                int numRows = rowsText.isEmpty() ? 1 : Integer.parseInt(rowsText);
                
                service.addRoom(roomNo, block, total);
                
                // Auto generate benches row-wise
                int baseBenches = total / numRows;
                int remainder = total % numRows;
                
                for (int r = 1; r <= numRows; r++) {
                    int benchesInThisRow = baseBenches + (r > numRows - remainder ? 1 : 0);
                    for (int i = 1; i <= benchesInThisRow; i++) {
                        String benchNo = numRows > 1 ? (roomNo + "-R" + r + "B" + i) : (roomNo + "-" + i);
                        service.addBench(benchNo, roomNo, cap);
                    }
                }
                
                UiUtil.info(this, "Room added and " + total + " benches auto-assigned");
                refresh();
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Benches, Rows, and Capacity must be numbers"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        addBenchBtn.addActionListener(e -> {
            try {
                String benchNo = benchNoField.getText().trim();
                String room = benchRoomField.getText().trim();
                if (benchNo.isEmpty() || room.isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("Bench No and Room No are required"));
                    return;
                }
                int cap = Integer.parseInt(benchCapacityField.getText().trim());
                service.addBench(benchNo, room, cap);
                UiUtil.info(this, "Bench added");
                refresh();
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Capacity must be a number"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        deleteBenchBtn.addActionListener(e -> {
            try {
                String bench = benchNoField.getText().trim();
                if (bench.isEmpty()) { UiUtil.error(this, new IllegalArgumentException("Bench No is required")); return; }
                int res = javax.swing.JOptionPane.showConfirmDialog(this, "Delete bench " + bench + "?", "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
                if (res == javax.swing.JOptionPane.YES_OPTION) {
                    service.deleteBench(bench);
                    UiUtil.info(this, "Bench deleted");
                    refresh();
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        deleteRoomBtn.addActionListener(e -> {
            try {
                String room = roomNoField.getText().trim();
                if (room.isEmpty()) { UiUtil.error(this, new IllegalArgumentException("Room No is required")); return; }
                int res = javax.swing.JOptionPane.showConfirmDialog(this, "Delete room " + room + "?\nNote: this will also delete its benches if allowed by DB", "Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
                if (res == javax.swing.JOptionPane.YES_OPTION) {
                    service.deleteRoom(room);
                    UiUtil.info(this, "Room deleted");
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
            java.util.List<String[]> rows = service.inventory();
            model.setRowCount(0);
            for (String[] row : rows) model.addRow(row);
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }
}
