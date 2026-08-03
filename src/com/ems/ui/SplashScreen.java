package com.ems.ui;

import com.ems.util.AnimationEngine;
import com.ems.util.AppTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Animated splash screen displayed during application startup.
 * Features gradient background, animated logo, typewriter title, and progress bar.
 */
public class SplashScreen extends JFrame {

    private float logoScale = 0f;
    private float titleAlpha = 0f;
    private float progressValue = 0f;
    private String statusText = "Initializing…";
    private final String[] statusMessages = {
            "Initializing…",
            "Connecting to database…",
            "Loading modules…",
            "Preparing dashboard…",
            "Ready!"
    };
    private int statusIndex = 0;

    public SplashScreen() {
        setUndecorated(true);
        setSize(520, 340);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                // Rounded window with gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 25, 55),
                        w, h, new Color(20, 40, 80));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 24, 24));

                // Border
                g2.setColor(new Color(37, 99, 235, 50));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 24, 24));

                // Ambient decorative orbs
                g2.setColor(new Color(37, 99, 235, 15));
                g2.fillOval(w - 200, -80, 300, 300);
                g2.setColor(new Color(139, 92, 246, 10));
                g2.fillOval(-100, h - 180, 250, 250);

                // Logo emoji
                int centerX = w / 2;
                int logoY = 80;
                float scale = Math.max(0.01f, logoScale);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, (int) (48 * scale)));
                FontMetrics fm = g2.getFontMetrics();
                String logo = "🎓";
                int logoWidth = fm.stringWidth(logo);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, logoScale)));
                g2.drawString(logo, centerX - logoWidth / 2, logoY);

                // Title
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, titleAlpha)));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
                fm = g2.getFontMetrics();
                String title = "Examination Management System";
                g2.setColor(Color.WHITE);
                g2.drawString(title, centerX - fm.stringWidth(title) / 2, 140);

                // Subtitle
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(new Color(148, 163, 184));
                fm = g2.getFontMetrics();
                String sub = "Smart Exam Classroom Allocation — PS-6";
                g2.drawString(sub, centerX - fm.stringWidth(sub) / 2, 170);

                // Progress bar background
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                int barX = 60, barY = 230, barW = w - 120, barH = 6;
                g2.setColor(new Color(30, 41, 59));
                g2.fill(new RoundRectangle2D.Float(barX, barY, barW, barH, barH, barH));

                // Progress bar fill
                int fillW = (int) (barW * progressValue);
                if (fillW > 0) {
                    GradientPaint progressGP = new GradientPaint(barX, barY, AppTheme.PRIMARY,
                            barX + fillW, barY, AppTheme.ACCENT);
                    g2.setPaint(progressGP);
                    g2.fill(new RoundRectangle2D.Float(barX, barY, fillW, barH, barH, barH));

                    // Glow effect on progress tip
                    g2.setColor(new Color(96, 165, 250, 60));
                    g2.fillOval(barX + fillW - 8, barY - 4, 16, 14);
                }

                // Status text
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(new Color(148, 163, 184, 180));
                fm = g2.getFontMetrics();
                g2.drawString(statusText, centerX - fm.stringWidth(statusText) / 2, barY + 28);

                // Version
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2.setColor(new Color(100, 116, 139, 120));
                String ver = "v2.0 — PS-6 Smart Allocation Engine";
                fm = g2.getFontMetrics();
                g2.drawString(ver, centerX - fm.stringWidth(ver) / 2, h - 24);

                g2.dispose();
            }
        };
        content.setOpaque(false);
        setContentPane(content);
    }

    /**
     * Play the splash animation, then call onComplete when done.
     */
    public void playAndThen(Runnable onComplete) {
        setVisible(true);

        // Phase 1: Logo scale-in (0 → 1)
        AnimationEngine.animate(600, AnimationEngine::easeOutBack, t -> {
            logoScale = t;
            repaint();
        }, () -> {
            // Phase 2: Title fade-in
            AnimationEngine.animate(400, AnimationEngine::easeOutCubic, t -> {
                titleAlpha = t;
                repaint();
            }, () -> {
                // Phase 3: Progress bar
                animateProgress(onComplete);
            });
        });
    }

    private void animateProgress(Runnable onComplete) {
        float[] targets = {0.2f, 0.5f, 0.75f, 0.95f, 1.0f};
        animateProgressStep(0, targets, onComplete);
    }

    private void animateProgressStep(int step, float[] targets, Runnable onComplete) {
        if (step >= targets.length) {
            // Fade out splash
            Timer fadeOut = new Timer(16, null);
            final float[] opacity = {1f};
            fadeOut.addActionListener(e -> {
                opacity[0] -= 0.05f;
                if (opacity[0] <= 0) {
                    fadeOut.stop();
                    setVisible(false);
                    dispose();
                    if (onComplete != null) onComplete.run();
                } else {
                    setOpacity(opacity[0]);
                }
            });
            fadeOut.start();
            return;
        }

        statusIndex = Math.min(step, statusMessages.length - 1);
        statusText = statusMessages[statusIndex];

        float from = progressValue;
        float to = targets[step];
        int duration = step < targets.length - 1 ? 350 : 250;

        AnimationEngine.animate(duration, AnimationEngine::easeOutCubic, t -> {
            progressValue = from + (to - from) * t;
            repaint();
        }, () -> {
            Timer delay = new Timer(100, e2 -> animateProgressStep(step + 1, targets, onComplete));
            delay.setRepeats(false);
            delay.start();
        });
    }
}
