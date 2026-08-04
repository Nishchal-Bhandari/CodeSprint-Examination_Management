package com.ems.ui;

import com.ems.service.AuthService;
import com.ems.util.AnimationEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    // Colour palette
    private static final Color C_LEFT_TOP  = new Color(10, 55, 100);
    private static final Color C_LEFT_BOT  = new Color(5, 30, 60);
    private static final Color C_RIGHT_BG  = new Color(246, 248, 252);
    private static final Color C_WHITE     = Color.WHITE;
    private static final Color C_ACCENT    = new Color(22, 115, 235);
    private static final Color C_HOVER     = new Color(12, 92, 200);
    private static final Color C_TEXT_HD   = new Color(15, 22, 36);
    private static final Color C_TEXT_MID  = new Color(75, 90, 112);
    private static final Color C_TEXT_LT   = new Color(150, 163, 180);
    private static final Color C_BORDER    = new Color(208, 218, 234);
    private static final Color C_FOCUS     = new Color(22, 115, 235);
    private static final Color C_ERR       = new Color(192, 50, 50);
    private static final Color C_SUCCESS   = new Color(22, 148, 90);

    private static final Font F_BOLD_32  = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font F_BOLD_18  = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font F_BOLD_15  = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font F_BOLD_13  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font F_BOLD_12  = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_BOLD_11  = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font F_REG_14   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font F_REG_13   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_REG_12   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_REG_11   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_MONO_12  = new Font("Consolas", Font.PLAIN, 12);

    private final AuthService authService = new AuthService();

    // Active tab: 0=Admin 1=Faculty 2=Student
    private int activeTab = 0;
    private JTextField  usernameField;
    private JPasswordField passwordField;
    private JLabel feedbackLbl;
    private JButton loginBtn;
    private JPanel tabAdmin, tabStudent;
    private JPanel rightCard;  // Reference for shake animation
    private JPanel hintPanel;

    // Credential data per role
    private static final String[][] CREDS = {
        {"admin",   "admin123",   "ADMIN — Full system access"},
        {"examcell","exam123",    "EXAM_CELL — Scheduling & reports"},
        {"f101",    "faculty123", "FACULTY — Duty & attendance"},
        {"<USN>",   "<USN>",      "STUDENT — Hall ticket & notifications"},
    };

    public LoginFrame() {
        setTitle("EMS — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 640);
        setMinimumSize(new Dimension(880, 560));
        setLocationRelativeTo(null);
        setContentPane(buildRoot());
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    // ═══════════════════════ ROOT ═══════════════════════

    private JPanel buildRoot() {
        JPanel root = new JPanel(new GridLayout(1, 2, 0, 0));
        root.add(buildLeft());
        root.add(buildRight());
        return root;
    }

    // ═══════════════════════ LEFT PANEL ═══════════════════════

    private JPanel buildLeft() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, C_LEFT_TOP, 0, getHeight(), C_LEFT_BOT));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255,255,255,14)); g2.fillOval(getWidth()-180, -70, 290, 290);
                g2.setColor(new Color(255,255,255,10)); g2.fillOval(-90, getHeight()-220, 300, 300);
                g2.dispose();
            }
        };
        p.setLayout(new GridBagLayout()); p.setOpaque(false);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBorder(new EmptyBorder(0, 44, 0, 44));

        // Icon + brand
        JLabel icon = new JLabel("🎓");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        icon.setAlignmentX(LEFT_ALIGNMENT);

        JLabel title = new JLabel("<html>Examination<br>Management<br>System</html>");
        title.setFont(F_BOLD_32); title.setForeground(C_WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Secure, organized exam operations");
        sub.setFont(F_REG_13); sub.setForeground(new Color(180,208,235));
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // Feature list
        JPanel features = new JPanel();
        features.setOpaque(false);
        features.setLayout(new BoxLayout(features, BoxLayout.Y_AXIS));
        features.setAlignmentX(LEFT_ALIGNMENT);
        features.setBorder(new EmptyBorder(28, 0, 28, 0));
        features.add(featureRow("👨‍💼", "Admin",   "Full system, audit, and security access"));
        features.add(Box.createVerticalStrut(16));
        features.add(featureRow("🎓", "Student", "Hall ticket download and notifications"));

        // Footer
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255,255,255,40));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JLabel footer = new JLabel("Trusted access for all stakeholders");
        footer.setFont(F_REG_12); footer.setForeground(new Color(140,175,215));
        footer.setAlignmentX(LEFT_ALIGNMENT);
        footer.setBorder(new EmptyBorder(12,0,0,0));

        inner.add(icon);
        inner.add(Box.createVerticalStrut(12));
        inner.add(title);
        inner.add(Box.createVerticalStrut(8));
        inner.add(sub);
        inner.add(features);
        inner.add(sep);
        inner.add(footer);

        p.add(inner);
        return p;
    }

    private JPanel featureRow(String emoji, String role, String desc) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false); row.setAlignmentX(LEFT_ALIGNMENT);

        JLabel em = new JLabel(emoji);
        em.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        em.setPreferredSize(new Dimension(34, 34));
        em.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel txt = new JPanel(); txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel r = new JLabel(role); r.setFont(F_BOLD_13); r.setForeground(C_WHITE);
        JLabel d = new JLabel(desc); d.setFont(F_REG_12); d.setForeground(new Color(172,204,232));
        txt.add(r); txt.add(d);

        row.add(em, BorderLayout.WEST);
        row.add(txt, BorderLayout.CENTER);
        return row;
    }

    // ═══════════════════════ RIGHT FORM PANEL ═══════════════════════

    private JPanel buildRight() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(C_RIGHT_BG);

        JPanel card = new JPanel();
        card.setBackground(C_WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 242), 1, true),
                new EmptyBorder(36, 40, 32, 40)
        ));
        card.setPreferredSize(new Dimension(420, 540));
        rightCard = card;  // store reference for shake animation

        // ── Header
        JLabel hd = new JLabel("Sign In");
        hd.setFont(F_BOLD_32); hd.setForeground(C_TEXT_HD);
        hd.setAlignmentX(LEFT_ALIGNMENT);

        JLabel hdSub = new JLabel("Select your role and enter your credentials");
        hdSub.setFont(F_REG_13); hdSub.setForeground(C_TEXT_MID);
        hdSub.setAlignmentX(LEFT_ALIGNMENT);

        // ── Role Tab selector
        JPanel tabs = buildRoleTabs();

        // ── Username field
        JLabel userLbl = fieldLabel("Username");
        usernameField = new JTextField();
        applyFieldStyle(usernameField);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        usernameField.setAlignmentX(LEFT_ALIGNMENT);

        // ── Password field
        JLabel passLbl = fieldLabel("Password");
        passwordField = new JPasswordField();
        applyFieldStyle(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        passwordField.setAlignmentX(LEFT_ALIGNMENT);

        // ── Show password
        JCheckBox showPw = new JCheckBox("Show password");
        showPw.setFont(F_REG_12); showPw.setForeground(C_TEXT_MID);
        showPw.setOpaque(false); showPw.setAlignmentX(LEFT_ALIGNMENT);
        showPw.setFocusPainted(false);
        char echo = passwordField.getEchoChar();
        showPw.addActionListener(e -> passwordField.setEchoChar(showPw.isSelected() ? '\0' : echo));

        // ── Login button
        loginBtn = buildLoginBtn();

        // ── Feedback
        feedbackLbl = new JLabel(" ");
        feedbackLbl.setFont(F_REG_12); feedbackLbl.setForeground(C_TEXT_LT);
        feedbackLbl.setAlignmentX(LEFT_ALIGNMENT);

        // ── Credentials hint box
        hintPanel = buildHintPanel(0);

        // Assemble card
        card.add(hd);
        card.add(Box.createVerticalStrut(4));
        card.add(hdSub);
        card.add(Box.createVerticalStrut(20));
        card.add(tabs);
        card.add(Box.createVerticalStrut(20));
        card.add(userLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(passLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(6));
        card.add(showPw);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(8));
        card.add(feedbackLbl);
        card.add(Box.createVerticalStrut(16));
        card.add(hintPanel);

        right.add(card);
        return right;
    }

    // ═══════════════════════ ROLE TABS ═══════════════════════

    private JPanel buildRoleTabs() {
        JPanel tabs = new JPanel(new GridLayout(1, 2, 6, 0));
        tabs.setOpaque(false);
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        tabs.setAlignmentX(LEFT_ALIGNMENT);

        tabAdmin   = buildTab("👨‍💼 Admin / Staff", 0);
        tabStudent = buildTab("🎓 Student",         1);

        tabs.add(tabAdmin);
        tabs.add(tabStudent);

        updateTabs();
        return tabs;
    }

    private JPanel buildTab(String label, int idx) {
        JPanel tab = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean active = (activeTab == idx);
                g2.setColor(active ? C_ACCENT : new Color(236, 240, 248));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tab.setOpaque(false);
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(F_BOLD_12);
        lbl.setForeground(activeTab == idx ? C_WHITE : C_TEXT_MID);
        tab.add(lbl);

        tab.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { selectTab(idx); }
            @Override public void mouseEntered(MouseEvent e) {
                if (activeTab != idx) tab.setBackground(new Color(220, 228, 245));
            }
            @Override public void mouseExited(MouseEvent e) {
                if (activeTab != idx) tab.setBackground(null);
            }
        });

        return tab;
    }

    private void selectTab(int idx) {
        activeTab = idx;
        updateTabs();
        prefillForTab(idx);
        // Swap hint panel content
        hintPanel.removeAll();
        JPanel inner = buildHintPanel(idx);
        hintPanel.setLayout(new BorderLayout());
        hintPanel.add(inner, BorderLayout.CENTER);
        hintPanel.revalidate();
        hintPanel.repaint();
    }

    private void updateTabs() {
        for (int i = 0; i < 2; i++) {
            JPanel t = i == 0 ? tabAdmin : tabStudent;
            JLabel l = (JLabel) t.getComponent(0);
            l.setForeground(activeTab == i ? C_WHITE : C_TEXT_MID);
            t.repaint();
        }
    }

    private void prefillForTab(int idx) {
        usernameField.setText("");
        passwordField.setText("");
        String placeholder = switch (idx) {
            case 0 -> "admin";
            case 1 -> "Your USN  e.g. 1CS23CS001";
            default -> "";
        };
        usernameField.setToolTipText(placeholder);
        usernameField.putClientProperty("placeholder", placeholder);
        feedbackLbl.setText(" ");
        usernameField.requestFocusInWindow();
    }

    // ═══════════════════════ CREDENTIALS HINT ═══════════════════════

    private JPanel buildHintPanel(int tabIdx) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(241, 245, 254));
        panel.setOpaque(true);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 222, 245), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel hintTitle = new JLabel("🔑  Quick Login Credentials");
        hintTitle.setFont(F_BOLD_13); hintTitle.setForeground(C_TEXT_HD);
        hintTitle.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(hintTitle);
        panel.add(Box.createVerticalStrut(10));

        if (tabIdx == 0) {
            panel.add(credRow("Admin", "admin", "admin123", "Full system access", panel));
        } else {
            JPanel helpBox = new JPanel();
            helpBox.setLayout(new BoxLayout(helpBox, BoxLayout.Y_AXIS));
            helpBox.setOpaque(false);
            helpBox.setBorder(new EmptyBorder(4, 4, 12, 4));
            
            JLabel s1 = new JLabel("ℹ\uFE0F First time login?");
            s1.setFont(F_BOLD_12); s1.setForeground(new Color(22, 100, 200)); s1.setAlignmentX(LEFT_ALIGNMENT);
            JLabel s2 = new JLabel("Your initial password is the same as your USN.");
            s2.setFont(F_REG_12); s2.setForeground(C_TEXT_MID); s2.setAlignmentX(LEFT_ALIGNMENT);
            
            helpBox.add(s1); helpBox.add(Box.createVerticalStrut(4)); helpBox.add(s2);
            panel.add(helpBox);

            panel.add(credRow("Student", "1CS23CS001", "1CS23CS001", "Computer Science", panel));
            panel.add(Box.createVerticalStrut(6));
            panel.add(credRow("Student", "1EC23EC001", "1EC23EC001", "Electronics", panel));
            panel.add(Box.createVerticalStrut(6));
            panel.add(credRow("Student", "1ME23ME001", "1ME23ME001", "Mechanical", panel));
        }

        return panel;
    }

    private JPanel credRow(String role, String user, String pass, String note, JPanel parent) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(true);
        row.setBackground(C_WHITE);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 244), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);
        JLabel roleLbl = new JLabel(role);
        roleLbl.setFont(F_BOLD_11); roleLbl.setForeground(C_TEXT_MID);
        roleLbl.setPreferredSize(new Dimension(60, 16));
        JLabel sep  = new JLabel("|"); sep.setForeground(C_BORDER); sep.setFont(F_REG_11);
        JLabel userL = new JLabel(user); userL.setFont(F_MONO_12); userL.setForeground(C_ACCENT);
        JLabel sep2  = new JLabel("/"); sep2.setForeground(C_BORDER); sep2.setFont(F_REG_11);
        JLabel passL = new JLabel(pass); passL.setFont(F_MONO_12); passL.setForeground(new Color(40,140,80));
        left.add(roleLbl); left.add(sep); left.add(userL); left.add(sep2); left.add(passL);

        JLabel noteL = new JLabel(note);
        noteL.setFont(F_REG_11); noteL.setForeground(C_TEXT_LT);

        row.add(left, BorderLayout.WEST);
        row.add(noteL, BorderLayout.EAST);

        Color normalBg = C_WHITE;
        Color hoverBg  = new Color(235, 243, 255);

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                usernameField.setText(user);
                passwordField.setText(pass);
                feedbackLbl.setText("✓ Credentials filled — click Sign In");
                feedbackLbl.setForeground(C_SUCCESS);
                loginBtn.requestFocusInWindow();
            }
            @Override public void mouseEntered(MouseEvent e) { row.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e)  { row.setBackground(normalBg); }
        });

        return row;
    }

    // ═══════════════════════ FIELD & BUTTON HELPERS ═══════════════════════

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_BOLD_13); l.setForeground(C_TEXT_HD);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void applyFieldStyle(JTextField field) {
        field.setFont(F_REG_14);
        field.setForeground(C_TEXT_HD);
        field.setBackground(C_WHITE);
        field.setCaretColor(C_ACCENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(10, 13, 10, 13)
        ));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_FOCUS, 2, true),
                        new EmptyBorder(9, 12, 9, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDER, 1, true),
                        new EmptyBorder(10, 13, 10, 13)));
            }
        });
    }

    private JButton buildLoginBtn() {
        JButton btn = new JButton("Sign In") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = !isEnabled() ? new Color(180,193,215)
                         : getModel().isRollover() ? C_HOVER : C_ACCENT;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BOLD_15);
        btn.setForeground(C_WHITE);
        btn.setOpaque(false); btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        btn.addActionListener(e -> attemptLogin());
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> attemptLogin());
        return btn;
    }

    // ═══════════════════════ LOGIN LOGIC ═══════════════════════

    private void attemptLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            feedbackLbl.setText("⚠  Please fill in both username and password.");
            feedbackLbl.setForeground(C_ERR);
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("Signing in…");
        feedbackLbl.setText("Authenticating…");
        feedbackLbl.setForeground(C_TEXT_LT);

        SwingWorker<String[], Void> worker = new SwingWorker<>() {
            @Override protected String[] doInBackground() throws Exception {
                return authService.login(user, pass);
            }
            @Override protected void done() {
                try {
                    String[] result = get();
                    String role = result[0], studentUsn = result[1];
                    SwingUtilities.invokeLater(() -> {
                        feedbackLbl.setText("✓ Login successful — loading…");
                        feedbackLbl.setForeground(C_SUCCESS);
                        if ("STUDENT".equals(role)) {
                            String usn = (studentUsn != null && !studentUsn.isBlank()) ? studentUsn : user;
                            new StudentPortalFrame(usn).setVisible(true);
                        } else {
                            new DashboardFrame(user, role).setVisible(true);
                        }
                        dispose();
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        feedbackLbl.setText("⚠  " + cause.getMessage());
                        feedbackLbl.setForeground(C_ERR);
                        loginBtn.setEnabled(true);
                        loginBtn.setText("Sign In");
                        passwordField.selectAll();
                        passwordField.requestFocusInWindow();
                        // Shake animation on error
                        if (rightCard != null) {
                            AnimationEngine.shake(rightCard, 12, 500);
                        }
                    });
                }
            }
        };
        worker.execute();
    }
}
