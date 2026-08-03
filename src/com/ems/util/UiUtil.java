package com.ems.util;

import javax.swing.BorderFactory;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import javax.swing.table.JTableHeader;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.RenderingHints;

public final class UiUtil {
    private UiUtil() {
    }

    public static JButton buildPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(AppTheme.PRIMARY);
        button.setForeground(java.awt.Color.WHITE);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setFont(AppTheme.FONT_BODY);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton buildSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(AppTheme.SECONDARY_BG);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setForeground(AppTheme.TEXT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        button.setFont(AppTheme.FONT_BODY);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JPanel buildPageHeader(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.FONT_TITLE);
        titleLabel.setForeground(AppTheme.TEXT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(AppTheme.FONT_CAPTION);
        subtitleLabel.setForeground(AppTheme.TEXT_LIGHT);

        header.add(titleLabel, BorderLayout.NORTH);
        header.add(subtitleLabel, BorderLayout.SOUTH);
        return header;
    }

    public static JPanel buildSurfaceCard() {
        return new RoundedPanel(AppTheme.SURFACE_BG, AppTheme.BORDER, 12);
    }

    public static JPanel buildModuleRoot() {
        return new GradientBackgroundPanel();
    }

    public static JPanel buildMetricChip(String text) {
        RoundedPanel chip = new RoundedPanel(AppTheme.ACCENT_SOFT, new Color(201, 235, 227), 999);
        chip.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_CAPTION);
        label.setForeground(AppTheme.ACCENT);
        chip.add(label);
        return chip;
    }

    public static JPanel buildSectionBanner(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.add(buildPageHeader(title, subtitle), BorderLayout.NORTH);
        return panel;
    }

    public static JLabel buildLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_BODY);
        label.setForeground(AppTheme.TEXT);
        return label;
    }

    public static void styleInput(JTextComponent field, int width) {
        field.setFont(AppTheme.FONT_BODY);
        field.setForeground(AppTheme.TEXT);
        field.setBackground(AppTheme.BG);
        field.setCaretColor(AppTheme.TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        field.setPreferredSize(new Dimension(width, 38));
    }

    public static void allowDigitsOnly(JTextComponent field) {
        if (field.getDocument() instanceof AbstractDocument document) {
            document.setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr)
                        throws javax.swing.text.BadLocationException {
                    if (string == null || string.chars().allMatch(Character::isDigit)) {
                        super.insertString(fb, offset, string, attr);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text,
                        javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    if (text == null || text.chars().allMatch(Character::isDigit)) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });
        }
    }

    public static JPanel buildActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        row.setOpaque(false);
        return row;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(AppTheme.FONT_BODY);
        table.setBackground(AppTheme.SURFACE_BG);
        table.setForeground(AppTheme.TEXT);
        table.setGridColor(AppTheme.BORDER);
        JTableHeader header = table.getTableHeader();
        header.setBackground(AppTheme.PRIMARY_DARK);
        header.setForeground(java.awt.Color.WHITE);
        header.setFont(AppTheme.FONT_SUBTITLE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
        com.ems.util.LoggerUtil.info(message);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
        com.ems.util.LoggerUtil.error(message, null);
    }

    public static void error(Component parent, Exception ex) {
        String msg = ex == null ? "Unknown error" : ex.getMessage();
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
        com.ems.util.LoggerUtil.error(msg, ex);
    }

    private static final class GradientBackgroundPanel extends JPanel {
        private GradientBackgroundPanel() {
            setBackground(AppTheme.BG);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(AppTheme.BG);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static final class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color border;
        private final int radius;

        private RoundedPanel(Color fill, Color border, int radius) {
            this.fill = fill;
            this.border = border;
            this.radius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(14, 14, 14, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
