package com.ems.util;

import java.awt.Color;
import java.awt.Font;

public final class AppTheme {
    // Deep Slate Dark Palette
    public static final Color BG = new Color(15, 23, 42); // Slate 900
    public static final Color PANEL_BG = new Color(30, 41, 59); // Slate 800
    public static final Color SURFACE_BG = new Color(51, 65, 85); // Slate 700
    
    public static final Color PRIMARY = new Color(37, 99, 235); // Royal Blue
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color PRIMARY_LIGHT = new Color(96, 165, 250);
    
    public static final Color ACCENT = new Color(139, 92, 246); // Purple 500
    public static final Color ACCENT_SOFT = new Color(49, 46, 129);
    
    public static final Color SECONDARY_BG = new Color(30, 41, 59);
    public static final Color SECONDARY_HOVER = new Color(71, 85, 105); // Slate 600
    
    public static final Color TEXT = new Color(248, 250, 252); // Slate 50
    public static final Color TEXT_LIGHT = new Color(148, 163, 184); // Slate 400
    public static final Color BORDER = new Color(71, 85, 105);
    
    // Status & KPI Colors
    public static final Color SUCCESS = new Color(16, 185, 129); // Emerald 500
    public static final Color WARNING = new Color(245, 158, 11); // Amber 500
    public static final Color DANGER = new Color(239, 68, 68); // Red 500
    public static final Color INFO = new Color(14, 165, 233); // Sky 500

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_CAPTION = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 13);

    private AppTheme() {
    }
}
