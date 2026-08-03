package com.ems.ui;

import com.ems.service.AuthService;
import com.ems.ui.panels.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class DashboardFrame extends JFrame {
    private static final Color C_SIDEBAR = new Color(22, 34, 60);
    private static final Color C_SIDEBAR_ACTIVE = new Color(43, 62, 92);
    private static final Color C_SIDEBAR_HOVER = new Color(30, 45, 75);
    private static final Color C_TOPBAR = new Color(31, 41, 66);
    private static final Color C_MAIN_BG = new Color(26, 26, 26);
    
    private static final Color C_TEXT_WHITE = Color.WHITE;
    private static final Color C_TEXT_MUTED = new Color(130, 150, 180);
    private static final Color C_TEXT_SECTION = new Color(100, 120, 150);

    private final AuthService authService = new AuthService();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentCardPanel = new JPanel(cardLayout);
    
    // Track active button for styling
    private CustomNavButton activeButton = null;

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
        
        rightArea.add(contentCardPanel, BorderLayout.CENTER);
        root.add(rightArea, BorderLayout.CENTER);
        
        setContentPane(root);
    }

    private JPanel buildSidebar(String username, String role) {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255,255,255,20));
                g.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
            }
        };
        sidebar.setBackground(C_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        
        // --- Header ---
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel title = new JLabel("EMS Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(C_TEXT_WHITE);
        
        JLabel userLabel = new JLabel("User: " + username + " \u2022 Role: " + role);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        userLabel.setForeground(C_TEXT_MUTED);
        
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(userLabel);
        
        // Divider
        JPanel div = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255,255,255,20));
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        div.setOpaque(false);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        header.add(Box.createVerticalStrut(15));
        header.add(div);
        
        sidebar.add(header, BorderLayout.NORTH);
        
        // --- Navigation ---
        JPanel navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setOpaque(false);
        
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
        
        navContainer.add(Box.createVerticalStrut(15));
        navContainer.add(createSectionHeader("EXAM DAY"));
        navContainer.add(createNavButton("👤", "Faculty assignment", "duty"));
        navContainer.add(createNavButton("📋", "Faculty management", "faculty"));
        CustomNavButton attendanceBtn = createNavButton("✅", "Attendance & conduct", "attendance");
        navContainer.add(attendanceBtn);
        navContainer.add(createNavButton("📄", "Answer sheets", "answer"));
        
        navContainer.add(Box.createVerticalStrut(15));
        navContainer.add(createSectionHeader("SYSTEM"));
        navContainer.add(createNavButton("📊", "Reports & audit", "reports"));
        navContainer.add(createNavButton("🔔", "Notifications", "notifications"));
        navContainer.add(createNavButton("🔒", "Access & security", "security"));
        
        // Select default
        attendanceBtn.doClick();
        
        sidebar.add(scroll, BorderLayout.CENTER);
        
        // --- Footer ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255,255,255,20));
                g.drawLine(0, 0, getWidth(), 0);
            }
        };
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(240, 70));
        
        JButton logoutBtn = new JButton("Logout") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(255,255,255,20) : new Color(255,255,255,10));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(255,255,255,40));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setOpaque(false);
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setForeground(C_TEXT_WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setPreferredSize(new Dimension(200, 36));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
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

        public CustomNavButton(String icon, String text) {
            super();
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setPreferredSize(new Dimension(240, 40));
            setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            iconLabel.setForeground(C_TEXT_MUTED);

            textLabel = new JLabel(text);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textLabel.setForeground(C_TEXT_MUTED);

            add(iconLabel);
            add(Box.createHorizontalStrut(12));
            add(textLabel);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { 
                    if (!active) { setBackground(C_SIDEBAR_HOVER); setOpaque(true); repaint(); }
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!active) { setOpaque(false); repaint(); }
                }
                @Override public void mouseClicked(MouseEvent e) {
                    if (actionListener != null) actionListener.actionPerformed(null);
                }
            });
        }
        
        public void addActionListener(java.awt.event.ActionListener l) {
            this.actionListener = l;
        }
        
        public void doClick() {
            if (actionListener != null) actionListener.actionPerformed(null);
        }

        public void setActive(boolean a) {
            this.active = a;
            setOpaque(a);
            setBackground(a ? C_SIDEBAR_ACTIVE : null);
            iconLabel.setForeground(a ? C_TEXT_WHITE : C_TEXT_MUTED);
            textLabel.setForeground(a ? C_TEXT_WHITE : C_TEXT_MUTED);
            textLabel.setFont(new Font("Segoe UI", a ? Font.BOLD : Font.PLAIN, 13));
            repaint();
        }
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(C_TOPBAR);
        top.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        
        JLabel title = new JLabel("Examination Management Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(C_TEXT_WHITE);
        
        JLabel sub = new JLabel("Manage students, exams, rooms, and faculty from one workspace");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(C_TEXT_MUTED);
        
        left.add(title);
        left.add(Box.createVerticalStrut(4));
        left.add(sub);
        
        JLabel flow = new JLabel("Flow: Students \u2192 Exams \u2192 Rooms \u2192 Seats \u2192 Faculty \u2192 Attendance \u2192 Reports");
        flow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        flow.setForeground(C_TEXT_MUTED);
        
        top.add(left, BorderLayout.WEST);
        top.add(flow, BorderLayout.EAST);
        
        return top;
    }
}
