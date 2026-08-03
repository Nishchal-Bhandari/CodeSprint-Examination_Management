package com.ems.ui.panels;

import com.ems.model.BenchMap;
import com.ems.model.SeatDetail;
import com.ems.util.AnimationEngine;
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
        setLayout(new BorderLayout());
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
            add(emptyLbl, BorderLayout.CENTER);
        } else {
            // Legend bar at top
            add(buildLegend(), BorderLayout.NORTH);

            // Bench cards grid
            JPanel benchGrid = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
            benchGrid.setOpaque(false);
            benchGrid.setBorder(new EmptyBorder(8, 16, 16, 16));

            for (BenchMap b : benches) {
                benchGrid.add(createBenchCard(b));
            }
            add(benchGrid, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        legend.setOpaque(false);
        legend.setBorder(new EmptyBorder(12, 20, 4, 20));

        legend.add(createLegendItem(AppTheme.SUCCESS, "Occupied"));
        legend.add(createLegendItem(AppTheme.SURFACE_BG, "Vacant"));
        legend.add(createLegendItem(AppTheme.WARNING, "Selected (Swap)"));

        return legend;
    }

    private JPanel createLegendItem(Color color, String label) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 2, 14, 14, 4, 4);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(14, 18));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppTheme.FONT_CAPTION);
        lbl.setForeground(AppTheme.TEXT_LIGHT);

        item.add(dot);
        item.add(lbl);
        return item;
    }

    private JPanel createBenchCard(BenchMap b) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 25));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth(), getHeight(), 12, 12));

                // Card fill
                g2.setColor(AppTheme.PANEL_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 12, 12));

                // Border
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 12, 12));
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

        // Capacity badge with accent
        JLabel capBadge = new JLabel("Cap: " + b.getCapacity(), SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.ACCENT_SOFT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        capBadge.setFont(AppTheme.FONT_CAPTION);
        capBadge.setForeground(AppTheme.ACCENT_HOVER);
        capBadge.setOpaque(false);
        capBadge.setBorder(new EmptyBorder(2, 8, 2, 8));

        header.add(benchTitle, BorderLayout.WEST);
        header.add(capBadge, BorderLayout.EAST);

        // Seats container
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
            private float glowIntensity = 0f;

            {
                // Hover glow animation
                addMouseListener(new MouseAdapter() {
                    private Timer hoverTimer;
                    @Override public void mouseEntered(MouseEvent e) {
                        if (hoverTimer != null) hoverTimer.stop();
                        hoverTimer = AnimationEngine.animate(200, AnimationEngine::easeOutCubic, t -> {
                            glowIntensity = t;
                            repaint();
                        }, null);
                    }
                    @Override public void mouseExited(MouseEvent e) {
                        if (hoverTimer != null) hoverTimer.stop();
                        hoverTimer = AnimationEngine.animate(300, AnimationEngine::easeOutCubic, t -> {
                            glowIntensity = 1f - t;
                            repaint();
                        }, null);
                    }
                });
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Hover glow shadow
                if (glowIntensity > 0.01f) {
                    Color glowColor = isSelected ? AppTheme.WARNING : isOccupied ? AppTheme.SUCCESS : AppTheme.PRIMARY;
                    g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(),
                            (int)(40 * glowIntensity)));
                    g2.fill(new RoundRectangle2D.Float(-2, -2, getWidth() + 4, getHeight() + 4, 12, 12));
                }

                // Main fill
                Color fill = AnimationEngine.lerpColor(boxBg,
                        AnimationEngine.lerpColor(boxBg, Color.WHITE, 0.15f), glowIntensity);
                g2.setColor(fill);
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
        usnLbl.setForeground(isOccupied ? Color.WHITE : AppTheme.TEXT_LIGHT);
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
