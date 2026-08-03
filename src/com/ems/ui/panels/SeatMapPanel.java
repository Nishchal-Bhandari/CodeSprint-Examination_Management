package com.ems.ui.panels;

import com.ems.model.BenchMap;
import com.ems.model.SeatDetail;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

public class SeatMapPanel extends JPanel {
    public SeatMapPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(new EmptyBorder(8, 8, 8, 8));
    }

    public void setBenchMap(List<BenchMap> benches) {
        removeAll();
        if (benches == null || benches.isEmpty()) {
            JLabel empty = new JLabel("No benches to display");
            empty.setFont(AppTheme.FONT_BODY);
            empty.setForeground(AppTheme.TEXT);
            add(empty);
            revalidate();
            repaint();
            return;
        }

        for (BenchMap b : benches) {
            JPanel benchCard = UiUtil.buildSurfaceCard();
            benchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));

            JLabel benchLabel = new JLabel("Bench: " + b.getBenchNo() + " (cap: " + b.getCapacity() + ")");
            benchLabel.setFont(new Font(AppTheme.FONT_CAPTION.getName(), Font.BOLD, 12));
            benchLabel.setForeground(AppTheme.TEXT);
            benchCard.add(benchLabel);

            // render seats
            for (int i = 1; i <= b.getCapacity(); i++) {
                SeatDetail sd = null;
                for (SeatDetail s : b.getSeats()) {
                    if (s.getSeatPosition() == i) { sd = s; break; }
                }
                String text = sd == null ? String.valueOf(i) : (i + " - " + sd.getUsn());
                JLabel seat = new JLabel(text);
                seat.setOpaque(true);
                seat.setBorder(new EmptyBorder(6, 8, 6, 8));
                seat.setBackground(sd == null ? AppTheme.SURFACE_BG : AppTheme.ACCENT_SOFT);
                seat.setForeground(sd == null ? AppTheme.TEXT : Color.BLACK);
                benchCard.add(seat);
            }

            add(benchCard);
        }
        revalidate();
        repaint();
    }
}
