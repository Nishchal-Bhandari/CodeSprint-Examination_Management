package com.ems.ui.panels;

import com.ems.model.BenchMap;
import com.ems.model.SeatDetail;
import com.ems.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.function.Consumer;

public class VisualSeatingPanel extends JPanel {

    private List<BenchMap> benches;
    private SeatDetail selectedSeat;
    private String selectedBenchNo;
    private Consumer<SeatDetail> onSeatSelected;

    public VisualSeatingPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 16));
        setBackground(AppTheme.BG);
        setOpaque(true);
    }

    public void setBenchMap(List<BenchMap> benches, Consumer<SeatDetail> onSeatSelected) {
        this.benches = benches;
        this.onSeatSelected = onSeatSelected;
        this.selectedSeat = null;
        this.selectedBenchNo = null;
        renderMap();
    }

    private void renderMap() {
        removeAll();
        if (benches == null || benches.isEmpty()) {
            JLabel emptyLbl = new JLabel("No classroom seating data available for this room.");
            emptyLbl.setFont(AppTheme.FONT_SUBTITLE);
            emptyLbl.setForeground(AppTheme.TEXT_LIGHT);
            emptyLbl.setBorder(new EmptyBorder(30, 30, 30, 30));
            add(emptyLbl);
        } else {
            for (BenchMap b : benches) {
                add(createBenchCard(b));
            }
        }
        revalidate();
        repaint();
    }

    private JPanel createBenchCard(BenchMap b) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PANEL_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(AppTheme.BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));
        card.setPreferredSize(new Dimension(220, 150));

        // Header: Bench No & Capacity Badge
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel benchTitle = new JLabel("🪑 " + b.getBenchNo());
        benchTitle.setFont(AppTheme.FONT_SUBTITLE);
        benchTitle.setForeground(AppTheme.TEXT);

        JLabel capBadge = new JLabel("Cap: " + b.getCapacity(), SwingConstants.CENTER);
        capBadge.setFont(AppTheme.FONT_CAPTION);
        capBadge.setForeground(AppTheme.TEXT_LIGHT);
        capBadge.setOpaque(true);
        capBadge.setBackground(AppTheme.SURFACE_BG);
        capBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

        header.add(benchTitle, BorderLayout.WEST);
        header.add(capBadge, BorderLayout.EAST);

        // Seats container (Grid according to capacity)
        JPanel seatsGrid = new JPanel(new GridLayout(1, b.getCapacity(), 8, 0));
        seatsGrid.setOpaque(false);

        for (int p = 1; p <= b.getCapacity(); p++) {
            SeatDetail seat = b.getSeatAt(p);
            seatsGrid.add(createSeatWidget(b.getBenchNo(), p, seat));
        }

        card.add(header, BorderLayout.NORTH);
        card.add(seatsGrid, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSeatWidget(String benchNo, int pos, SeatDetail seat) {
        boolean isOccupied = (seat != null);
        boolean isSelected = (selectedSeat != null && selectedSeat.getUsn() != null &&
                              seat != null && selectedSeat.getUsn().equals(seat.getUsn()));

        Color boxBg = isSelected ? AppTheme.WARNING
                    : isOccupied ? AppTheme.SUCCESS
                    : AppTheme.SURFACE_BG;

        JPanel seatBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(boxBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        seatBox.setOpaque(false);
        seatBox.setLayout(new BoxLayout(seatBox, BoxLayout.Y_AXIS));
        seatBox.setBorder(new EmptyBorder(6, 6, 6, 6));
        seatBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel posLbl = new JLabel("P" + pos, SwingConstants.CENTER);
        posLbl.setFont(AppTheme.FONT_CAPTION);
        posLbl.setForeground(isOccupied ? Color.WHITE : AppTheme.TEXT_LIGHT);
        posLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel usnLbl = new JLabel(isOccupied ? seat.getUsn() : "VACANT", SwingConstants.CENTER);
        usnLbl.setFont(AppTheme.FONT_MONO);
        usnLbl.setForeground(Color.WHITE);
        usnLbl.setAlignmentX(CENTER_ALIGNMENT);

        seatBox.add(posLbl);
        seatBox.add(Box.createVerticalStrut(4));
        seatBox.add(usnLbl);

        if (isOccupied) {
            seatBox.setToolTipText("<html><b>" + seat.getName() + "</b><br>USN: " + seat.getUsn() + "<br>Bench: " + benchNo + " (Pos " + pos + ")</html>");
        } else {
            seatBox.setToolTipText("Vacant Seat (Position " + pos + ")");
        }

        seatBox.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (isOccupied && onSeatSelected != null) {
                    selectedSeat = seat;
                    selectedBenchNo = benchNo;
                    renderMap();
                    onSeatSelected.accept(seat);
                }
            }
        });

        return seatBox;
    }
}
