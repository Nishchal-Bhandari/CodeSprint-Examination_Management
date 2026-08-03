package com.ems.util;

import javax.swing.BorderFactory;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.JTextComponent;
import javax.swing.table.DefaultTableCellRenderer;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public final class UiUtil {
    private UiUtil() {
    }

    // ═══════════════════════ ANIMATED PRIMARY BUTTON ═══════════════════════

    public static JButton buildPrimaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg;
                Object hc = getClientProperty("hoverColor");
                if (!isEnabled()) {
                    currentBg = AppTheme.BORDER;
                } else if (getModel().isPressed()) {
                    currentBg = AppTheme.PRIMARY_DARK;
                } else if (hc instanceof Color) {
                    currentBg = (Color) hc;
                } else {
                    currentBg = AppTheme.PRIMARY;
                }

                // Gradient fill
                GradientPaint gp = new GradientPaint(0, 0, currentBg, getWidth(), getHeight(),
                        AnimationEngine.lerpColor(currentBg, AppTheme.ACCENT, 0.15f));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                // Subtle inner highlight
                g2.setColor(new Color(255, 255, 255, 15));
                g2.fill(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() / 2f, 10, 10));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFocusPainted(false);
        button.setForeground(java.awt.Color.WHITE);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFont(AppTheme.FONT_BUTTON);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Animated hover effect
        AnimationEngine.installHoverEffect(button, AppTheme.PRIMARY, AppTheme.PRIMARY_HOVER, AppTheme.ANIM_FAST);

        return button;
    }

    // ═══════════════════════ ANIMATED SECONDARY BUTTON ═══════════════════════

    public static JButton buildSecondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg;
                Object hc = getClientProperty("hoverColor");
                if (getModel().isPressed()) {
                    currentBg = AppTheme.SURFACE_BG;
                } else if (hc instanceof Color) {
                    currentBg = (Color) hc;
                } else {
                    currentBg = AppTheme.SECONDARY_BG;
                }

                g2.setColor(currentBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                // Border with glow on hover
                float hoverProgress = AnimationEngine.getHoverProgress(this);
                Color borderColor = AnimationEngine.lerpColor(AppTheme.BORDER, AppTheme.PRIMARY_LIGHT, hoverProgress * 0.6f);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFocusPainted(false);
        button.setForeground(AppTheme.TEXT);
        button.setBorder(new EmptyBorder(9, 16, 9, 16));
        button.setFont(AppTheme.FONT_BUTTON);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Animated hover
        AnimationEngine.installHoverEffect(button, AppTheme.SECONDARY_BG, AppTheme.SECONDARY_HOVER, AppTheme.ANIM_FAST);

        return button;
    }

    // ═══════════════════════ PAGE HEADER ═══════════════════════

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

    // ═══════════════════════ SURFACE CARD ═══════════════════════

    public static JPanel buildSurfaceCard() {
        return new RoundedPanel(AppTheme.SURFACE_BG, AppTheme.BORDER, 12);
    }

    // ═══════════════════════ MODULE ROOT (GRADIENT BG) ═══════════════════════

    public static JPanel buildModuleRoot() {
        return new GradientBackgroundPanel();
    }

    // ═══════════════════════ METRIC CHIP ═══════════════════════

    public static JPanel buildMetricChip(String text) {
        RoundedPanel chip = new RoundedPanel(AppTheme.ACCENT_SOFT, new Color(201, 235, 227), 999);
        chip.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_CAPTION);
        label.setForeground(AppTheme.ACCENT);
        chip.add(label);
        return chip;
    }

    // ═══════════════════════ SECTION BANNER ═══════════════════════

    public static JPanel buildSectionBanner(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.add(buildPageHeader(title, subtitle), BorderLayout.NORTH);
        return panel;
    }

    // ═══════════════════════ LABEL ═══════════════════════

    public static JLabel buildLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.FONT_BODY);
        label.setForeground(AppTheme.TEXT);
        return label;
    }

    // ═══════════════════════ INPUT FIELD ═══════════════════════

    public static void styleInput(JTextComponent field, int width) {
        field.setFont(AppTheme.FONT_BODY);
        field.setForeground(AppTheme.TEXT);
        field.setBackground(AppTheme.BG);
        field.setCaretColor(AppTheme.TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        field.setPreferredSize(new Dimension(width, 38));

        // Animated focus ring
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.PRIMARY, 2),
                        BorderFactory.createEmptyBorder(5, 7, 5, 7)));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AppTheme.BORDER),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)));
            }
        });
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

    // ═══════════════════════ ACTION ROW ═══════════════════════

    public static JPanel buildActionRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        row.setOpaque(false);
        return row;
    }

    // ═══════════════════════ STYLED TABLE ═══════════════════════

    public static void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(AppTheme.FONT_BODY);
        table.setBackground(AppTheme.SURFACE_BG);
        table.setForeground(AppTheme.TEXT);
        table.setGridColor(new Color(AppTheme.BORDER.getRed(), AppTheme.BORDER.getGreen(), AppTheme.BORDER.getBlue(), 60));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(AppTheme.PRIMARY_DARK);
        table.setSelectionForeground(Color.WHITE);

        // Alternating rows + hover
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private int hoveredRow = -1;
            {
                table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int row = table.rowAtPoint(e.getPoint());
                        if (row != hoveredRow) {
                            hoveredRow = row;
                            table.repaint();
                        }
                    }
                });
                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoveredRow = -1;
                        table.repaint();
                    }
                });
            }

            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row == hoveredRow) {
                        c.setBackground(AppTheme.TABLE_ROW_HOVER);
                    } else if (row % 2 == 0) {
                        c.setBackground(AppTheme.SURFACE_BG);
                    } else {
                        c.setBackground(AppTheme.TABLE_ROW_ALT);
                    }
                    c.setForeground(AppTheme.TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                label.setBackground(AppTheme.PANEL_BG);
                label.setForeground(AppTheme.TEXT_LIGHT);
                label.setFont(AppTheme.FONT_BUTTON);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, AppTheme.PRIMARY),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                return label;
            }
        });
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);
    }

    // ═══════════════════════ CUSTOM SCROLL PANE ═══════════════════════

    public static JScrollPane buildDarkScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Custom scrollbar styling
        styleScrollBar(scrollPane.getVerticalScrollBar());
        styleScrollBar(scrollPane.getHorizontalScrollBar());

        return scrollPane;
    }

    private static void styleScrollBar(JScrollBar scrollBar) {
        scrollBar.setPreferredSize(new Dimension(8, 8));
        scrollBar.setOpaque(false);
        scrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = AppTheme.BORDER;
                this.trackColor = AppTheme.BG;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                return btn;
            }

            @Override
            protected void paintThumb(Graphics g, javax.swing.JComponent c, java.awt.Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(AppTheme.TEXT_LIGHT.getRed(), AppTheme.TEXT_LIGHT.getGreen(),
                        AppTheme.TEXT_LIGHT.getBlue(), 80));
                g2.fill(new RoundRectangle2D.Float(thumbBounds.x + 1, thumbBounds.y + 1,
                        thumbBounds.width - 2, thumbBounds.height - 2, 6, 6));
                g2.dispose();
            }

            @Override
            protected void paintTrack(Graphics g, javax.swing.JComponent c, java.awt.Rectangle trackBounds) {
                // Transparent track
            }
        });
    }

    // ═══════════════════════ TOAST-BASED NOTIFICATIONS ═══════════════════════

    public static void info(Component parent, String message) {
        ToastManager.success(parent, message);
    }

    public static void error(Component parent, String message) {
        ToastManager.error(parent, message);
    }

    public static void error(Component parent, Exception ex) {
        String msg = ex == null ? "Unknown error" : ex.getMessage();
        ToastManager.error(parent, msg != null ? msg : "An unexpected error occurred");
    }

    // ═══════════════════════ GRADIENT BACKGROUND PANEL ═══════════════════════

    private static final class GradientBackgroundPanel extends JPanel {
        private GradientBackgroundPanel() {
            setBackground(AppTheme.BG);
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Diagonal gradient background
            GradientPaint gradient = new GradientPaint(
                    0, 0, AppTheme.GRADIENT_START,
                    getWidth(), getHeight(), AppTheme.GRADIENT_END);
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Decorative ambient orbs
            g2.setColor(new Color(37, 99, 235, 8));
            g2.fillOval(getWidth() - 300, -100, 500, 500);

            g2.setColor(new Color(139, 92, 246, 6));
            g2.fillOval(-150, getHeight() - 250, 400, 400);

            g2.setColor(new Color(16, 185, 129, 5));
            g2.fillOval(getWidth() / 2 - 150, getHeight() / 2 - 150, 300, 300);

            g2.dispose();
        }
    }

    // ═══════════════════════ ROUNDED PANEL ═══════════════════════

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

            // Subtle shadow
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 1, getHeight() - 1, radius, radius));

            // Fill
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, radius, radius));

            // Border
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, radius, radius));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ═══════════════════════ EMPTY STATE ═══════════════════════

    public static JPanel buildEmptyState(String icon, String message) {
        JPanel panel = new JPanel();
        panel.setLayout(new javax.swing.BoxLayout(panel, javax.swing.BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(40, 20, 40, 20));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 36));
        iconLabel.setForeground(AppTheme.TEXT_LIGHT);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(AppTheme.FONT_BODY);
        msgLabel.setForeground(AppTheme.TEXT_LIGHT);
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(javax.swing.Box.createVerticalStrut(12));
        panel.add(msgLabel);

        return panel;
    }
}
