package com.ems.ui;

import com.ems.service.AuthService;
import com.ems.ui.panels.*;
import com.ems.util.AnimationEngine;
import com.ems.util.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DashboardFrame extends JFrame {
    private static final Color C_SIDEBAR = new Color(13, 22, 45);
    private static final Color C_SIDEBAR_ACTIVE = new Color(37, 99, 235, 25);
    private static final Color C_SIDEBAR_HOVER = new Color(37, 99, 235, 12);
    private static final Color C_TOPBAR = new Color(20, 30, 55);
    private static final Color C_MAIN_BG = new Color(15, 23, 42);
    
    private static final Color C_TEXT_WHITE = Color.WHITE;
    private static final Color C_TEXT_MUTED = new Color(130, 150, 180);
    private static final Color C_TEXT_SECTION = new Color(80, 100, 140);
    private static final Color C_ACCENT = new Color(37, 99, 235);

    private final AuthService authService = new AuthService();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCardPanel = new JPanel(cardLayout);
    
    // Track active button for styling
    private CustomNavButton activeButton = null;
    private JLabel clockLabel;

    public DashboardFrame(String username, String role) {
        setTitle("EMS Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1360, 840);
        setMinimumSize(new Dimension(1024, 768));
        setLocationRelativeTo(null);
        
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_MAIN_BG);
        
        root.add(buildSidebar(username, role), BorderLayout.WEST);
        
        JPanel rightArea = new JPanel(new BorderLayout());
        rightArea.setOpaque(false);
        rightArea.add(buildTopBar(), BorderLayout.NORTH);
        
        contentCardPanel.setOpaque(false);
        contentCardPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add panels
        contentCardPanel.add(new StudentPanel(), "students");
        contentCardPanel.add(new SubjectPanel(), "subjects");
        contentCardPanel.add(new DepartmentPanel(), "departments");
        contentCardPanel.add(new ExamPanel(), "exams");
        contentCardPanel.add(new RoomBenchPanel(), "rooms");
        contentCardPanel.add(new SeatingAllocationPanel(), "seat");
        contentCardPanel.add(new FacultyDutyPanel(), "duty");
        contentCardPanel.add(new FacultyPanel(), "faculty");
        contentCardPanel.add(new AttendanceConductPanel(), "attendance");
        contentCardPanel.add(new AnswerSheetPanel(), "answer");
        contentCardPanel.add(new ReportsAuditPanel(), "reports");
        contentCardPanel.add(new NotificationsPanel(), "notifications");
        contentCardPanel.add(new AccessSecurityPanel(), "security");
        contentCardPanel.add(new AiCopilotPanel(), "ai_copilot");
        
        rightArea.add(contentCardPanel, BorderLayout.CENTER);
        root.add(rightArea, BorderLayout.CENTER);
        
        setContentPane(root);

        // Start live clock
        startClock();
    }

    private JPanel buildSidebar(String username, String role) {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // Gradient sidebar
                GradientPaint gp = new GradientPaint(0, 0, C_SIDEBAR,
                        0, getHeight(), new Color(8, 15, 35));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right edge line
                g2.setColor(new Color(37, 99, 235, 30));
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(250, 0));
        
        // --- Header ---
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 20, 15, 20));
        
        // Brand with accent dot
        JPanel brandRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brandRow.setOpaque(false);
        brandRow.setAlignmentX(LEFT_ALIGNMENT);
        
        JPanel accentDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_ACCENT);
                g2.fillOval(0, 4, 10, 10);
                // Glow
                g2.setColor(new Color(37, 99, 235, 40));
                g2.fillOval(-3, 1, 16, 16);
                g2.dispose();
            }
        };
        accentDot.setOpaque(false);
        accentDot.setPreferredSize(new Dimension(14, 18));
        
        JLabel title = new JLabel("EMS Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(C_TEXT_WHITE);
        
        brandRow.add(accentDot);
        brandRow.add(title);
        
        // User info with avatar
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userPanel.setOpaque(false);
        userPanel.setAlignmentX(LEFT_ALIGNMENT);
        userPanel.setBorder(new EmptyBorder(12, 0, 0, 0));
        
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Ring
                GradientPaint ringGP = new GradientPaint(0, 0, C_ACCENT, 32, 32, AppTheme.ACCENT);
                g2.setPaint(ringGP);
                g2.fillOval(0, 0, 32, 32);
                // Inner circle
                g2.setColor(new Color(30, 50, 90));
                g2.fillOval(2, 2, 28, 28);
                // Initials
                g2.setColor(C_TEXT_WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String initials = username.substring(0, Math.min(2, username.length())).toUpperCase();
                g2.drawString(initials, 16 - fm.stringWidth(initials)/2, 16 + fm.getAscent()/2 - 1);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(32, 32));
        
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);
        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(C_TEXT_WHITE);
        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(C_TEXT_MUTED);
        userInfo.add(userLabel);
        userInfo.add(roleLabel);
        
        userPanel.add(avatar);
        userPanel.add(userInfo);
        
        header.add(brandRow);
        header.add(userPanel);
        
        // Divider
        JPanel div = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(20, 0, new Color(37, 99, 235, 40),
                        getWidth() - 20, 0, new Color(139, 92, 246, 20));
                g2.setPaint(gp);
                g2.fillRect(20, 0, getWidth() - 40, 1);
                g2.dispose();
            }
        };
        div.setOpaque(false);
        div.setPreferredSize(new Dimension(250, 1));
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setAlignmentX(LEFT_ALIGNMENT);
        header.add(Box.createVerticalStrut(15));
        header.add(div);
        
        sidebar.add(header, BorderLayout.NORTH);
        
        // --- Navigation ---
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setOpaque(false);
        navContainer.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        JScrollPane scroll = new JScrollPane(navContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Management Section
        navContainer.add(createSectionHeader("MANAGEMENT"));
        navContainer.add(createNavButton("👥", "Student management", "students"));
        navContainer.add(createNavButton("📖", "Subject management", "subjects"));
        navContainer.add(createNavButton("🏢", "Department", "departments"));
        navContainer.add(createNavButton("📅", "Exam scheduling", "exams"));
        navContainer.add(createNavButton("🪑", "Room & bench", "rooms"));
        navContainer.add(createNavButton("🎟️", "Seat allocation", "seat"));
        
        navContainer.add(Box.createVerticalStrut(10));
        navContainer.add(createSectionHeader("EXAM DAY"));
        navContainer.add(createNavButton("👤", "Faculty assignment", "duty"));
        navContainer.add(createNavButton("📋", "Faculty management", "faculty"));
        CustomNavButton attendanceBtn = createNavButton("✅", "Attendance & conduct", "attendance");
        navContainer.add(attendanceBtn);
        navContainer.add(createNavButton("📄", "Answer sheets", "answer"));
        
        navContainer.add(Box.createVerticalStrut(10));
        navContainer.add(createSectionHeader("SYSTEM"));
        navContainer.add(createNavButton("🤖", "AI Copilot", "ai_copilot"));
        navContainer.add(createNavButton("📊", "Reports & audit", "reports"));
        navContainer.add(createNavButton("🔔", "Notifications", "notifications"));
        navContainer.add(createNavButton("🔒", "Access & security", "security"));
        
        // Select default
        attendanceBtn.doClick();
        
        sidebar.add(scroll, BorderLayout.CENTER);
        
        // --- Footer ---
        JPanel footer = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(20, 0, new Color(37, 99, 235, 30),
                        getWidth() - 20, 0, new Color(139, 92, 246, 15));
                g2.setPaint(gp);
                g2.fillRect(20, 0, getWidth() - 40, 1);
                g2.dispose();
            }
        };
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(12, 15, 15, 15));
        
        JButton logoutBtn = new JButton("Sign Out") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg;
                Object hc = getClientProperty("hoverColor");
                if (hc instanceof Color) bg = (Color) hc;
                else bg = new Color(255,255,255,8);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(255,255,255,25));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setOpaque(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setForeground(C_TEXT_MUTED);
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        logoutBtn.setAlignmentX(LEFT_ALIGNMENT);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        AnimationEngine.installHoverEffect(logoutBtn, new Color(255,255,255,8), new Color(255,255,255,20), 150);
        logoutBtn.addActionListener(e -> {
            authService.logout(username);
            new LoginFrame().setVisible(true);
            dispose();
        });
        
        footer.add(logoutBtn);
        sidebar.add(footer, BorderLayout.SOUTH);
        
        return sidebar;
    }

    private JPanel createSectionHeader(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(C_TEXT_SECTION);
        p.add(l);
        return p;
    }

    private CustomNavButton createNavButton(String icon, String text, String cardName) {
        CustomNavButton btn = new CustomNavButton(icon, text);
        btn.addActionListener(e -> {
            if (activeButton != null) {
                activeButton.setActive(false);
            }
            btn.setActive(true);
            activeButton = btn;
            cardLayout.show(contentCardPanel, cardName);
        });
        return btn;
    }

    private class CustomNavButton extends JPanel {
        private boolean active = false;
        private JLabel iconLabel;
        private JLabel textLabel;
        private java.awt.event.ActionListener actionListener;
        private float hoverAnim = 0f;
        private Timer hoverTimer;

        public CustomNavButton(String icon, String text) {
            super();
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setPreferredSize(new Dimension(250, 38));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            iconLabel.setForeground(C_TEXT_MUTED);

            textLabel = new JLabel(text);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textLabel.setForeground(C_TEXT_MUTED);

            add(Box.createHorizontalStrut(12));
            add(iconLabel);
            add(Box.createHorizontalStrut(12));
            add(textLabel);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { 
                    if (!active) animateHover(true);
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!active) animateHover(false);
                }
                @Override public void mouseClicked(MouseEvent e) {
                    if (actionListener != null) actionListener.actionPerformed(null);
                }
            });
        }

        private void animateHover(boolean entering) {
            if (hoverTimer != null) hoverTimer.stop();
            float target = entering ? 1f : 0f;
            float start = hoverAnim;
            hoverTimer = AnimationEngine.animate(150, AnimationEngine::easeOutCubic, t -> {
                hoverAnim = start + (target - start) * t;
                repaint();
            }, null);
        }
        
        public void addActionListener(java.awt.event.ActionListener l) {
            this.actionListener = l;
        }
        
        public void doClick() {
            if (actionListener != null) actionListener.actionPerformed(null);
        }

        public void setActive(boolean a) {
            this.active = a;
            hoverAnim = a ? 1f : 0f;
            iconLabel.setForeground(a ? C_TEXT_WHITE : C_TEXT_MUTED);
            textLabel.setForeground(a ? C_TEXT_WHITE : C_TEXT_MUTED);
            textLabel.setFont(new Font("Segoe UI", a ? Font.BOLD : Font.PLAIN, 13));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (active || hoverAnim > 0.01f) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                float alpha = active ? 1f : hoverAnim;
                Color bgColor = active ? C_SIDEBAR_ACTIVE : C_SIDEBAR_HOVER;
                g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(),
                        (int) (bgColor.getAlpha() * alpha)));
                g2.fill(new RoundRectangle2D.Float(6, 1, getWidth() - 12, getHeight() - 2, 8, 8));

                // Left accent indicator
                if (active) {
                    g2.setColor(C_ACCENT);
                    g2.fill(new RoundRectangle2D.Float(0, 6, 3, getHeight() - 12, 3, 3));
                }

                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_TOPBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Bottom accent line
                GradientPaint gp = new GradientPaint(0, getHeight()-2, C_ACCENT,
                        getWidth(), getHeight()-2, new Color(139, 92, 246, 60));
                g2.setPaint(gp);
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
                g2.dispose();
            }
        };
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(15, 25, 17, 25));
        
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        
        JLabel title = new JLabel("Examination Management Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(C_TEXT_WHITE);
        
        JLabel sub = new JLabel("Manage students, exams, rooms, and faculty from one workspace");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(C_TEXT_MUTED);
        
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);
        
        // Right side: clock + status
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        clockLabel = new JLabel("");
        clockLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        clockLabel.setForeground(C_TEXT_MUTED);
        clockLabel.setAlignmentX(RIGHT_ALIGNMENT);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        statusRow.setOpaque(false);
        JPanel statusDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.SUCCESS);
                g2.fillOval(2, 2, 8, 8);
                g2.setColor(new Color(16, 185, 129, 40));
                g2.fillOval(0, 0, 12, 12);
                g2.dispose();
            }
        };
        statusDot.setOpaque(false);
        statusDot.setPreferredSize(new Dimension(12, 12));
        JLabel statusLabel = new JLabel("Connected");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(AppTheme.SUCCESS);
        statusRow.add(statusDot);
        statusRow.add(statusLabel);

        rightPanel.add(clockLabel);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(statusRow);
        
        top.add(left, BorderLayout.WEST);
        top.add(rightPanel, BorderLayout.EAST);
        
        return top;
    }

    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timer clockTimer = new Timer(1000, e -> {
            if (clockLabel != null) {
                clockLabel.setText(LocalTime.now().format(fmt));
            }
        });
        clockTimer.setInitialDelay(0);
        clockTimer.start();
    }
}
