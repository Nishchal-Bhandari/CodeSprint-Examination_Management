package com.ems.ui.panels;

import com.ems.dao.RoomBenchDAO;
import com.ems.model.BenchMap;
import com.ems.model.SeatDetail;
import com.ems.service.AllocationService;
import com.ems.util.AnimationEngine;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class SeatingAllocationPanel extends JPanel {
    private final AllocationService service = new AllocationService();
    private final DefaultTableModel model;
    private final VisualSeatingPanel visualSeatingPanel;
    private final JLabel kpiCandidatesLbl;
    private final JLabel kpiRoomsLbl;
    private final JLabel kpiStatusLbl;

    private SeatDetail firstSelectedSeat = null;

    public SeatingAllocationPanel() {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        add(UiUtil.buildSectionBanner(
                "Smart Seat Allocation & Visual Map",
                "Automated seat distribution algorithm with interactive 2D classroom seating map & Manual Swap Suite"
        ), BorderLayout.NORTH);

        // Form Card
        JPanel formCard = UiUtil.buildSurfaceCard();
        formCard.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(6, 6, 12, 6));

        JTextField examIdField = new JTextField("5001", 8);
        UiUtil.styleInput(examIdField, 140);
        UiUtil.allowDigitsOnly(examIdField);

        JComboBox<String> roomCombo = new JComboBox<>();
        roomCombo.setPreferredSize(new Dimension(180, 38));

        JButton allocateBtn = UiUtil.buildPrimaryButton("⚡ Auto Allocate Seats");
        JButton viewBtn = UiUtil.buildSecondaryButton("🔍 View Allocation");
        JButton clearBtn = UiUtil.buildSecondaryButton("🗑 Clear Allocation");
        JButton viewMapBtn = UiUtil.buildSecondaryButton("🗺 Visual 2D Map");
        JButton manualSwapBtn = UiUtil.buildSecondaryButton("🔄 Manual Swap Seats");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(UiUtil.buildLabel("Exam ID"), gbc);
        gbc.gridx = 1;
        form.add(examIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(UiUtil.buildLabel("Room No"), gbc);
        gbc.gridx = 1;
        form.add(roomCombo, gbc);

        JPanel actions = UiUtil.buildActionRow();
        actions.add(allocateBtn);
        actions.add(viewBtn);
        actions.add(clearBtn);
        actions.add(viewMapBtn);
        actions.add(manualSwapBtn);

        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 2;
        gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(actions, gbc);

        formCard.add(form, BorderLayout.CENTER);

        // Top KPI Cards Panel
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        kpiPanel.setOpaque(false);

        kpiCandidatesLbl = new JLabel("0", SwingConstants.LEFT);
        kpiRoomsLbl = new JLabel("0", SwingConstants.LEFT);
        kpiStatusLbl = new JLabel("Ready", SwingConstants.LEFT);

        kpiPanel.add(createKpiCard("📊 Candidates Allocated", kpiCandidatesLbl, AppTheme.PRIMARY_LIGHT));
        kpiPanel.add(createKpiCard("🏫 Classroom Rooms", kpiRoomsLbl, AppTheme.ACCENT));
        kpiPanel.add(createKpiCard("⚙ Algorithm Status", kpiStatusLbl, AppTheme.SUCCESS));

        // Table & Map Tabs
        model = new DefaultTableModel(new Object[]{"Seat ID", "USN", "Student Name", "Room", "Bench", "Position"}, 0);
        JTable table = new JTable(model);
        UiUtil.styleTable(table);

        JPanel tableCard = UiUtil.buildSurfaceCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        visualSeatingPanel = new VisualSeatingPanel();
        JScrollPane mapScroll = new JScrollPane(visualSeatingPanel);
        mapScroll.setOpaque(false);
        mapScroll.getViewport().setOpaque(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.addTab("📋 Allocation Table", tableCard);
        tabs.addTab("🗺 Interactive 2D Room Map", mapScroll);

        JPanel contentStack = new JPanel(new BorderLayout(0, 14));
        contentStack.setOpaque(false);
        contentStack.add(formCard, BorderLayout.NORTH);
        contentStack.add(kpiPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);
        centerPanel.add(contentStack, BorderLayout.NORTH);
        centerPanel.add(tabs, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Action Listeners
        allocateBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                allocateBtn.setEnabled(false);
                allocateBtn.setText("Allocating…");
                kpiStatusLbl.setText("Running Algorithm…");

                SwingWorker<String, Void> worker = new SwingWorker<>() {
                    @Override protected String doInBackground() throws Exception {
                        return service.autoAllocate(examId);
                    }
                    @Override protected void done() {
                        allocateBtn.setEnabled(true);
                        allocateBtn.setText("⚡ Auto Allocate Seats");
                        try {
                            String msg = get();
                            UiUtil.info(SeatingAllocationPanel.this, msg);
                            kpiStatusLbl.setText("Allocation Complete");
                            loadExam(examId);
                        } catch (Exception ex) {
                            UiUtil.error(SeatingAllocationPanel.this, ex);
                            kpiStatusLbl.setText("Error");
                        }
                    }
                };
                worker.execute();
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            }
        });

        viewBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                loadExam(examId);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            }
        });

        clearBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                int deleted = service.clearExam(examId);
                UiUtil.info(this, "Cleared " + deleted + " seat allocation(s)");
                loadExam(examId);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        // Populate rooms
        try {
            RoomBenchDAO rdao = new RoomBenchDAO();
            List<String[]> inv = rdao.getRoomBenchInventory();
            for (String[] r : inv) roomCombo.addItem(r[0]);
            if (roomCombo.getItemCount() > 0) {
                roomCombo.setSelectedIndex(0);
            }
        } catch (Exception ex) {
            // ignore initial load population error
        }

        viewMapBtn.addActionListener(e -> {
            try {
                int examId = Integer.parseInt(examIdField.getText().trim());
                String roomNo = (String) roomCombo.getSelectedItem();
                if (roomNo == null || roomNo.trim().isEmpty()) {
                    UiUtil.error(this, new IllegalArgumentException("Select a room"));
                    return;
                }
                List<BenchMap> benches = service.forExamRoom(examId, roomNo);
                visualSeatingPanel.setBenchMap(benches, seat -> {
                    if (firstSelectedSeat == null) {
                        firstSelectedSeat = seat;
                        kpiStatusLbl.setText("Selected: " + seat.getUsn() + " (Click 2nd seat to swap)");
                    } else if (firstSelectedSeat.getUsn().equals(seat.getUsn())) {
                        firstSelectedSeat = null;
                        kpiStatusLbl.setText("Selection cleared");
                    } else {
                        // Prompt swap confirmation
                        int choice = JOptionPane.showConfirmDialog(
                                this,
                                "Swap seat of " + firstSelectedSeat.getUsn() + " (" + firstSelectedSeat.getName() + ")\n" +
                                "with " + seat.getUsn() + " (" + seat.getName() + ")?",
                                "Coordinator Seat Swap Override",
                                JOptionPane.YES_NO_OPTION
                        );
                        if (choice == JOptionPane.YES_OPTION) {
                            try {
                                long s1 = findSeatId(firstSelectedSeat.getUsn());
                                long s2 = findSeatId(seat.getUsn());
                                if (s1 > 0 && s2 > 0) {
                                    service.swapSeats(s1, s2);
                                    UiUtil.info(this, "Successfully swapped seats for USNs " + firstSelectedSeat.getUsn() + " and " + seat.getUsn());
                                    loadExam(examId);
                                    List<BenchMap> refreshed = service.forExamRoom(examId, roomNo);
                                    visualSeatingPanel.setBenchMap(refreshed, null);
                                }
                            } catch (Exception ex) {
                                UiUtil.error(this, ex);
                            }
                        }
                        firstSelectedSeat = null;
                    }
                });
                tabs.setSelectedIndex(1);
            } catch (NumberFormatException nfe) {
                UiUtil.error(this, new IllegalArgumentException("Exam ID must be numeric"));
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        });

        manualSwapBtn.addActionListener(e -> showManualSwapDialog());
    }

    private JPanel createKpiCard(String title, JLabel valueLbl, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient fill
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PANEL_BG,
                        getWidth(), getHeight(), new Color(AppTheme.PANEL_BG.getRed() + 8,
                        AppTheme.PANEL_BG.getGreen() + 10, AppTheme.PANEL_BG.getBlue() + 15));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                // Top accent line
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 3, 3, 3));
                // Border
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

    private void loadExam(int examId) {
        try {
            List<String[]> rows = service.forExam(examId);
            model.setRowCount(0);
            for (String[] row : rows) model.addRow(row);
            // Animated counter
            int newCount = rows.size();
            AnimationEngine.animateCounter(kpiCandidatesLbl, 0, newCount, AppTheme.ANIM_SLOW);
            long roomsCount = rows.stream().map(r -> r[3]).distinct().count();
            AnimationEngine.animateCounter(kpiRoomsLbl, 0, (int) roomsCount, AppTheme.ANIM_SLOW);
        } catch (Exception ex) {
            UiUtil.error(this, ex);
        }
    }

    private long findSeatId(String usn) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (usn.equalsIgnoreCase(String.valueOf(model.getValueAt(i, 1)))) {
                return Long.parseLong(String.valueOf(model.getValueAt(i, 0)));
            }
        }
        return -1;
    }

    private void showManualSwapDialog() {
        JTextField seatId1Field = new JTextField(10);
        JTextField seatId2Field = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("First Seat ID:"));
        panel.add(seatId1Field);
        panel.add(new JLabel("Second Seat ID:"));
        panel.add(seatId2Field);

        int result = JOptionPane.showConfirmDialog(
                this, panel, "Coordinator Manual Seat Swap", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                long s1 = Long.parseLong(seatId1Field.getText().trim());
                long s2 = Long.parseLong(seatId2Field.getText().trim());
                boolean swapped = service.swapSeats(s1, s2);
                if (swapped) {
                    UiUtil.info(this, "Successfully swapped seats #" + s1 + " and #" + s2);
                }
            } catch (Exception ex) {
                UiUtil.error(this, ex);
            }
        }
    }
}
