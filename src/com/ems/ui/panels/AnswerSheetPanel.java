package com.ems.ui.panels;

import com.ems.service.AnswerSheetService;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Answer Sheet Handling panel – register scripts, barcode tracking, sealing bundles.
 */
public class AnswerSheetPanel extends JPanel {
    private final AnswerSheetService service = new AnswerSheetService();
    private final DefaultTableModel sheetModel;
    private final DefaultTableModel bundleModel;

    public AnswerSheetPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Answer Sheet Handling",
                "Register scripts with barcodes, manage bundles, seal packs, and track spare sheets"
        ), BorderLayout.NORTH);

        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 10, 4));

        JTextField examIdField    = new JTextField(8);  UiUtil.styleInput(examIdField, 120);    UiUtil.allowDigitsOnly(examIdField);
        JTextField usnField       = new JTextField(12); UiUtil.styleInput(usnField, 170);
        JTextField barcodeField   = new JTextField(14); UiUtil.styleInput(barcodeField, 190);
        JTextField bundleField    = new JTextField(6);  UiUtil.styleInput(bundleField, 100);    UiUtil.allowDigitsOnly(bundleField);
        JTextField roomField      = new JTextField(8);  UiUtil.styleInput(roomField, 130);
        JTextField invigField     = new JTextField(6);  UiUtil.styleInput(invigField, 100);     UiUtil.allowDigitsOnly(invigField);
        JCheckBox  spareCheck     = new JCheckBox("Spare Sheet");
        spareCheck.setOpaque(false);
        spareCheck.setFont(com.ems.util.AppTheme.FONT_BODY);

        // Seal bundle row
        JTextField sealExamField  = new JTextField(8);  UiUtil.styleInput(sealExamField, 120);  UiUtil.allowDigitsOnly(sealExamField);
        JTextField sealRoomField  = new JTextField(8);  UiUtil.styleInput(sealRoomField, 130);
        JTextField sealBundleField= new JTextField(6);  UiUtil.styleInput(sealBundleField, 100);UiUtil.allowDigitsOnly(sealBundleField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,8,6,8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0 – add sheet
        gbc.gridy = 0;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Exam ID"), gbc);
        gbc.gridx = 1; form.add(examIdField, gbc);
        gbc.gridx = 2; form.add(UiUtil.buildLabel("USN"), gbc);
        gbc.gridx = 3; form.add(usnField, gbc);
        gbc.gridx = 4; form.add(UiUtil.buildLabel("Barcode"), gbc);
        gbc.gridx = 5; form.add(barcodeField, gbc);
        gbc.gridx = 6; form.add(UiUtil.buildLabel("Bundle No"), gbc);
        gbc.gridx = 7; form.add(bundleField, gbc);

        // Row 1 – more fields + buttons
        gbc.gridy = 1;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Room No"), gbc);
        gbc.gridx = 1; form.add(roomField, gbc);
        gbc.gridx = 2; form.add(UiUtil.buildLabel("Invigilator ID"), gbc);
        gbc.gridx = 3; form.add(invigField, gbc);
        gbc.gridx = 4; form.add(spareCheck, gbc);

        JButton addBtn     = UiUtil.buildPrimaryButton("Register Script");
        JButton viewBtn    = UiUtil.buildSecondaryButton("View Scripts");
        JButton refreshBtn = UiUtil.buildSecondaryButton("Refresh");
        JPanel row1Btns = UiUtil.buildActionRow();
        row1Btns.add(addBtn); row1Btns.add(viewBtn); row1Btns.add(refreshBtn);
        gbc.gridx = 5; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(row1Btns, gbc);

        // Row 2 – seal bundle
        gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; form.add(UiUtil.buildLabel("Seal Exam"), gbc);
        gbc.gridx = 1; form.add(sealExamField, gbc);
        gbc.gridx = 2; form.add(UiUtil.buildLabel("Seal Room"), gbc);
        gbc.gridx = 3; form.add(sealRoomField, gbc);
        gbc.gridx = 4; form.add(UiUtil.buildLabel("Bundle No"), gbc);
        gbc.gridx = 5; form.add(sealBundleField, gbc);
        JButton sealBtn     = UiUtil.buildSecondaryButton("Seal Bundle");
        JButton summaryBtn  = UiUtil.buildSecondaryButton("Bundle Summary");
        JPanel row2Btns = UiUtil.buildActionRow();
        row2Btns.add(sealBtn); row2Btns.add(summaryBtn);
        gbc.gridx = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(row2Btns, gbc);

        formCard.add(form, BorderLayout.CENTER);

        // ---- Tables ----
        sheetModel = new DefaultTableModel(new Object[]{
                "Sheet ID","USN","Name","Barcode","Type","Status","Bundle","Room","Invigilator","Created At"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable sheetTable = new JTable(sheetModel);
        UiUtil.styleTable(sheetTable);

        bundleModel = new DefaultTableModel(new Object[]{"Room","Bundle No","Total Sheets","Sealed"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable bundleTable = new JTable(bundleModel);
        UiUtil.styleTable(bundleTable);

        JTabbedPane tabs = new JTabbedPane();
        JPanel sheetCard  = UiUtil.buildSurfaceCard(); sheetCard.setLayout(new BorderLayout());  sheetCard.add(new JScrollPane(sheetTable), BorderLayout.CENTER);
        JPanel bundleCard = UiUtil.buildSurfaceCard(); bundleCard.setLayout(new BorderLayout()); bundleCard.add(new JScrollPane(bundleTable), BorderLayout.CENTER);
        tabs.addTab("Scripts", sheetCard);
        tabs.addTab("Bundle Summary", bundleCard);

        JPanel stack = new JPanel(new BorderLayout(0, 14));
        stack.setOpaque(false);
        stack.add(formCard, BorderLayout.NORTH);
        stack.add(tabs, BorderLayout.CENTER);
        add(stack, BorderLayout.CENTER);

        // ---- Listeners ----
        addBtn.addActionListener(e -> {
            try {
                int examId  = Integer.parseInt(examIdField.getText().trim());
                int bundle  = Integer.parseInt(bundleField.getText().trim());
                String invig = invigField.getText().trim();
                Integer invigilatorId = invig.isEmpty() ? null : Integer.parseInt(invig);
                service.addSheet(
                        usnField.getText().trim(), examId,
                        barcodeField.getText().trim(),
                        spareCheck.isSelected(), bundle,
                        roomField.getText().trim(), invigilatorId
                );
                UiUtil.info(this, "Script registered");
                loadSheets(examId);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID, Bundle No, and Invigilator ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        viewBtn.addActionListener(e -> {
            try {
                loadSheets(Integer.parseInt(examIdField.getText().trim()));
                tabs.setSelectedIndex(0);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        refreshBtn.addActionListener(e -> {
            try {
                loadSheets(Integer.parseInt(examIdField.getText().trim()));
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        sealBtn.addActionListener(e -> {
            try {
                int examId  = Integer.parseInt(sealExamField.getText().trim());
                int bundle  = Integer.parseInt(sealBundleField.getText().trim());
                service.sealBundle(examId, sealRoomField.getText().trim(), bundle);
                UiUtil.info(this, "Bundle sealed");
                loadSheets(examId);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID and Bundle No must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        summaryBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(sealExamField.getText().trim());
                List<String[]> rows = service.bundleSummary(examId);
                bundleModel.setRowCount(0);
                for (String[] r : rows) bundleModel.addRow(r);
                tabs.setSelectedIndex(1);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });
    }

    private void loadSheets(int examId) throws Exception {
        List<String[]> rows = service.sheetsForExam(examId);
        sheetModel.setRowCount(0);
        for (String[] r : rows) sheetModel.addRow(r);
    }
}
