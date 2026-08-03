package com.ems.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Non-blocking toast notification system.
 * Slide-in from top-right, auto-dismiss with fade-out.
 * Color-coded: SUCCESS, ERROR, INFO, WARNING.
 */
public final class ToastManager {

    public enum ToastType { SUCCESS, ERROR, INFO, WARNING }

    private static final int TOAST_WIDTH = 360;
    private static final int TOAST_HEIGHT = 64;
    private static final int MARGIN = 16;
    private static final int DISPLAY_MS = 3500;
    private static final int ANIM_MS = 300;
    private static final int GAP = 8;

    private static final List<JWindow> activeToasts = new ArrayList<>();

    private ToastManager() {}

    public static void show(Component parent, String message, ToastType type) {
        Window owner = parent instanceof Window ? (Window) parent
                : SwingUtilities.getWindowAncestor(parent);
        if (owner == null) return;

        // Determine colors
        Color bg, accent, fg;
        String icon;
        switch (type) {
            case SUCCESS -> { bg = new Color(6, 78, 59);    accent = AppTheme.SUCCESS; fg = new Color(209, 250, 229); icon = "✓"; }
            case ERROR   -> { bg = new Color(127, 29, 29);   accent = AppTheme.DANGER;  fg = new Color(254, 226, 226); icon = "✕"; }
            case WARNING -> { bg = new Color(120, 53, 15);   accent = AppTheme.WARNING; fg = new Color(254, 243, 199); icon = "⚠"; }
            default      -> { bg = new Color(12, 74, 110);   accent = AppTheme.INFO;    fg = new Color(224, 242, 254); icon = "ℹ"; }
        };

        JWindow toast = new JWindow(owner);
        toast.setSize(TOAST_WIDTH, TOAST_HEIGHT);
        toast.setAlwaysOnTop(true);

        JPanel content = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 2, getHeight() - 2, 12, 12));
                // Background
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, 12, 12));
                // Left accent strip
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, 4, getHeight() - 3, 4, 4));
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconLabel.setForeground(accent);
        iconLabel.setPreferredSize(new Dimension(28, 28));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Message
        JLabel msgLabel = new JLabel("<html><body style='width: 240px'>" + message + "</body></html>");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLabel.setForeground(fg);

        // Close button
        JLabel closeBtn = new JLabel("×");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeBtn.setForeground(new Color(255, 255, 255, 100));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { dismissToast(toast); }
            @Override public void mouseEntered(MouseEvent e) { closeBtn.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e) { closeBtn.setForeground(new Color(255, 255, 255, 100)); }
        });

        content.add(iconLabel, BorderLayout.WEST);
        content.add(msgLabel, BorderLayout.CENTER);
        content.add(closeBtn, BorderLayout.EAST);

        toast.setContentPane(content);

        // Position: top-right of owner, stacked below existing toasts
        Rectangle ownerBounds = owner.getBounds();
        int targetX = ownerBounds.x + ownerBounds.width - TOAST_WIDTH - MARGIN;
        int targetY = ownerBounds.y + MARGIN;
        for (JWindow existing : activeToasts) {
            if (existing.isVisible()) {
                targetY = existing.getY() + existing.getHeight() + GAP;
            }
        }

        int startX = ownerBounds.x + ownerBounds.width + 10; // Start off-screen right
        toast.setLocation(startX, targetY);
        toast.setOpacity(0f);
        toast.setVisible(true);
        activeToasts.add(toast);

        // Slide in animation
        final int finalTargetX = targetX;
        final int finalTargetY = targetY;
        AnimationEngine.animate(ANIM_MS, AnimationEngine::easeOutCubic, t -> {
            int x = startX + (int) ((finalTargetX - startX) * t);
            toast.setLocation(x, finalTargetY);
            toast.setOpacity(Math.min(1f, t * 1.5f));
        }, () -> {
            // Auto-dismiss timer
            Timer dismissTimer = new Timer(DISPLAY_MS, e -> dismissToast(toast));
            dismissTimer.setRepeats(false);
            dismissTimer.start();
        });

        // Log to console for audit
        if (type == ToastType.SUCCESS || type == ToastType.INFO) {
            LoggerUtil.info(message);
        } else if (type == ToastType.ERROR) {
            LoggerUtil.error(message, null);
        }
    }

    private static void dismissToast(JWindow toast) {
        if (!toast.isVisible()) return;
        float startOpacity = toast.getOpacity();
        AnimationEngine.animate(200, AnimationEngine::easeInOutCubic, t -> {
            toast.setOpacity(startOpacity * (1f - t));
        }, () -> {
            toast.setVisible(false);
            toast.dispose();
            activeToasts.remove(toast);
        });
    }

    /** Convenience methods */
    public static void success(Component parent, String message) { show(parent, message, ToastType.SUCCESS); }
    public static void error(Component parent, String message)   { show(parent, message, ToastType.ERROR); }
    public static void info(Component parent, String message)    { show(parent, message, ToastType.INFO); }
    public static void warning(Component parent, String message) { show(parent, message, ToastType.WARNING); }
}
