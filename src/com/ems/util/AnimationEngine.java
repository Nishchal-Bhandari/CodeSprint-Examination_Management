package com.ems.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * Reusable Swing Timer-based animation engine.
 * Provides easing functions, fade, slide, scale, and color interpolation.
 */
public final class AnimationEngine {

    private static final int FRAME_RATE = 16; // ~60fps

    private AnimationEngine() {}

    // ═══════════════════════ EASING FUNCTIONS ═══════════════════════

    /** Ease-in-out cubic: slow start, fast middle, slow end */
    public static float easeInOutCubic(float t) {
        return t < 0.5f
                ? 4f * t * t * t
                : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
    }

    /** Ease-out cubic: fast start, slow end */
    public static float easeOutCubic(float t) {
        return 1f - (float) Math.pow(1f - t, 3);
    }

    /** Ease-out back: slight overshoot then settle */
    public static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }

    /** Ease-out elastic: bouncy spring effect */
    public static float easeOutElastic(float t) {
        if (t == 0f || t == 1f) return t;
        float c4 = (float) (2 * Math.PI / 3);
        return (float) (Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1);
    }

    /** Linear: no easing */
    public static float linear(float t) {
        return t;
    }

    // ═══════════════════════ GENERIC ANIMATOR ═══════════════════════

    @FunctionalInterface
    public interface EasingFunction {
        float apply(float t);
    }

    /**
     * Animate a float value from 0 to 1 over the given duration.
     * @param durationMs animation duration in milliseconds
     * @param easing easing function to apply
     * @param onUpdate called each frame with eased value [0..1]
     * @param onComplete called when animation finishes (may be null)
     * @return the Timer (can be used to cancel)
     */
    public static Timer animate(int durationMs, EasingFunction easing,
                                Consumer<Float> onUpdate, Runnable onComplete) {
        final long startTime = System.currentTimeMillis();
        Timer timer = new Timer(FRAME_RATE, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float raw = Math.min(1f, (float) elapsed / durationMs);
                float eased = easing.apply(raw);
                onUpdate.accept(eased);
                if (raw >= 1f) {
                    timer.stop();
                    if (onComplete != null) onComplete.run();
                }
            }
        });
        timer.setRepeats(true);
        timer.start();
        return timer;
    }

    /** Shorthand with easeInOutCubic and no completion callback */
    public static Timer animate(int durationMs, Consumer<Float> onUpdate) {
        return animate(durationMs, AnimationEngine::easeInOutCubic, onUpdate, null);
    }

    // ═══════════════════════ FADE IN / OUT ═══════════════════════

    /**
     * Fade a component from transparent to fully opaque.
     * Uses JLayer or custom painting via the component's opacity.
     */
    public static Timer fadeIn(JComponent component, int durationMs) {
        component.setVisible(true);
        return animate(durationMs, AnimationEngine::easeOutCubic, t -> {
            component.putClientProperty("animAlpha", t);
            component.repaint();
        }, null);
    }

    /** Fade a component out, then optionally hide it */
    public static Timer fadeOut(JComponent component, int durationMs, Runnable onComplete) {
        return animate(durationMs, AnimationEngine::easeInOutCubic, t -> {
            component.putClientProperty("animAlpha", 1f - t);
            component.repaint();
        }, () -> {
            component.setVisible(false);
            if (onComplete != null) onComplete.run();
        });
    }

    /** Get the current animation alpha for a component (default 1.0) */
    public static float getAlpha(JComponent component) {
        Object val = component.getClientProperty("animAlpha");
        return val instanceof Float ? (Float) val : 1f;
    }

    // ═══════════════════════ SLIDE IN ═══════════════════════

    public enum Direction { LEFT, RIGHT, UP, DOWN }

    /**
     * Slide a component in from the given direction.
     * Modifies the component's location relative to its parent.
     */
    public static Timer slideIn(JComponent component, Direction direction, int distance, int durationMs) {
        Point originalLocation = component.getLocation();
        int startX = originalLocation.x;
        int startY = originalLocation.y;

        switch (direction) {
            case LEFT  -> component.setLocation(startX - distance, startY);
            case RIGHT -> component.setLocation(startX + distance, startY);
            case UP    -> component.setLocation(startX, startY - distance);
            case DOWN  -> component.setLocation(startX, startY + distance);
        }

        Point slideStart = component.getLocation();
        component.setVisible(true);

        return animate(durationMs, AnimationEngine::easeOutCubic, t -> {
            int x = slideStart.x + (int) ((startX - slideStart.x) * t);
            int y = slideStart.y + (int) ((startY - slideStart.y) * t);
            component.setLocation(x, y);
            component.putClientProperty("animAlpha", t);
            component.repaint();
        }, null);
    }

    // ═══════════════════════ COLOR INTERPOLATION ═══════════════════════

    /** Linearly interpolate between two colors */
    public static Color lerpColor(Color from, Color to, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (from.getRed()   + (to.getRed()   - from.getRed())   * t);
        int g = (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * t);
        int b = (int) (from.getBlue()  + (to.getBlue()  - from.getBlue())  * t);
        int a = (int) (from.getAlpha() + (to.getAlpha() - from.getAlpha()) * t);
        return new Color(r, g, b, a);
    }

    /**
     * Animate a smooth color transition.
     * @return the Timer
     */
    public static Timer animateColor(Color from, Color to, int durationMs,
                                     Consumer<Color> onUpdate, Runnable onComplete) {
        return animate(durationMs, AnimationEngine::easeInOutCubic, t -> {
            onUpdate.accept(lerpColor(from, to, t));
        }, onComplete);
    }

    // ═══════════════════════ NUMBER COUNTER ═══════════════════════

    /**
     * Animate a counting effect from startVal to endVal, updating a JLabel.
     */
    public static Timer animateCounter(JLabel label, int startVal, int endVal, int durationMs) {
        return animate(durationMs, AnimationEngine::easeOutCubic, t -> {
            int current = startVal + (int) ((endVal - startVal) * t);
            label.setText(String.valueOf(current));
        }, null);
    }

    // ═══════════════════════ SHAKE EFFECT ═══════════════════════

    /**
     * Shake a component horizontally (for error feedback).
     */
    public static Timer shake(JComponent component, int intensity, int durationMs) {
        Point original = component.getLocation();
        final long startTime = System.currentTimeMillis();
        Timer timer = new Timer(FRAME_RATE, null);
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float raw = Math.min(1f, (float) elapsed / durationMs);
            if (raw >= 1f) {
                component.setLocation(original);
                timer.stop();
            } else {
                // Damped sine wave
                double decay = 1.0 - raw;
                double offset = Math.sin(raw * Math.PI * 8) * intensity * decay;
                component.setLocation(original.x + (int) offset, original.y);
            }
        });
        timer.setRepeats(true);
        timer.start();
        return timer;
    }

    // ═══════════════════════ HOVER COLOR MANAGER ═══════════════════════

    /**
     * Utility to manage smooth hover color transitions for any component.
     * Stores the current interpolated color as a client property.
     */
    public static void installHoverEffect(JComponent component, Color normalColor, Color hoverColor, int durationMs) {
        component.putClientProperty("hoverProgress", 0f);
        component.putClientProperty("hoverColor", normalColor);

        component.addMouseListener(new java.awt.event.MouseAdapter() {
            private Timer currentTimer;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (currentTimer != null) currentTimer.stop();
                float startProgress = getHoverProgress(component);
                currentTimer = animate(durationMs, AnimationEngine::easeOutCubic, t -> {
                    float progress = startProgress + (1f - startProgress) * t;
                    component.putClientProperty("hoverProgress", progress);
                    component.putClientProperty("hoverColor", lerpColor(normalColor, hoverColor, progress));
                    component.repaint();
                }, null);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (currentTimer != null) currentTimer.stop();
                float startProgress = getHoverProgress(component);
                currentTimer = animate(durationMs, AnimationEngine::easeOutCubic, t -> {
                    float progress = startProgress * (1f - t);
                    component.putClientProperty("hoverProgress", progress);
                    component.putClientProperty("hoverColor", lerpColor(normalColor, hoverColor, progress));
                    component.repaint();
                }, null);
            }
        });
    }

    /** Get the current hover progress [0..1] for a component */
    public static float getHoverProgress(JComponent component) {
        Object val = component.getClientProperty("hoverProgress");
        return val instanceof Float ? (Float) val : 0f;
    }

    /** Get the current hover-interpolated color for a component */
    public static Color getHoverColor(JComponent component) {
        Object val = component.getClientProperty("hoverColor");
        return val instanceof Color ? (Color) val : null;
    }

    // ═══════════════════════ PULSE GLOW EFFECT ═══════════════════════

    /**
     * Creates a continuous pulse glow effect (for selected items, status indicators).
     * @return the Timer (caller must stop it when no longer needed)
     */
    public static Timer pulseGlow(JComponent component, Color baseColor, Color glowColor, int cycleDurationMs) {
        final long startTime = System.currentTimeMillis();
        Timer timer = new Timer(FRAME_RATE, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float phase = (float) (elapsed % cycleDurationMs) / cycleDurationMs;
            // Sine wave for smooth pulse
            float intensity = (float) (0.5 + 0.5 * Math.sin(phase * 2 * Math.PI));
            component.putClientProperty("glowColor", lerpColor(baseColor, glowColor, intensity));
            component.repaint();
        });
        timer.setRepeats(true);
        timer.start();
        return timer;
    }
}
