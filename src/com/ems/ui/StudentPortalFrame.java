package com.ems.ui;

import com.ems.model.HallTicketEntry;
import com.ems.model.Notification;
import com.ems.model.Student;
import com.ems.service.AuthService;
import com.ems.service.StudentPortalService;
import com.ems.util.AnimationEngine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.TextAttribute;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentPortalFrame extends JFrame {
    private static final Color NAVY_BG = new Color(26, 39, 68); // #1a2744
    private static final Color MAIN_BG = new Color(243, 244, 246);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED_WHITE = new Color(255, 255, 255, 160);
    private static final Color BORDER_COLOR = new Color(220, 224, 230);
    private static final Color TEXT_MAIN = new Color(30, 41, 59);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    
    private final String usn;
    private final StudentPortalService portalService = new StudentPortalService();
    private final AuthService authService = new AuthService();

    private JLabel nameLabel, usnDeptSemLabel;
    private JPanel notifContainer;
    private JLabel notifBadge;
    private DefaultTableModel ticketModel;
    private JTable ticketTable;
    private int hoveredRow = -1;

    public StudentPortalFrame(String usn) {
        this.usn = usn;
        setTitle("EMS Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 760);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);
        setContentPane(root);
        
        loadData();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(NAVY_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));

        // Top brand
        JPanel brand = new JPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setOpaque(false);
        brand.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel brandTitle = new JLabel("EMS Portal");
        brandTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        brandTitle.setForeground(WHITE);
        brandTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel brandSub = new JLabel("Student access");
        brandSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        brandSub.setForeground(MUTED_WHITE);
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        brand.add(brandTitle);
        brand.add(Box.createVerticalStrut(4));
        brand.add(brandSub);

        // Divider
        JPanel div = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(255, 255, 255, 30));
                g2.setStroke(new BasicStroke(0.5f));
                g2.drawLine(20, 0, getWidth() - 20, 0);
            }
        };
        div.setOpaque(false);
        div.setMaximumSize(new Dimension(200, 1));
        div.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.add(Box.createVerticalStrut(15));
        brand.add(div);
        
        // Profile
        JPanel profile = new JPanel();
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        profile.setOpaque(false);
        profile.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(40, 80, 160));
                g2.fillOval(0, 0, 40, 40);
                String initials = "ST";
                if (nameLabel != null && nameLabel.getText().length() > 0) {
                    initials = nameLabel.getText().substring(0, 1);
                }
                g2.setColor(WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                FontMetrics fm = g2.getFontMetrics();
                int x = (40 - fm.stringWidth(initials)) / 2;
                int y = ((40 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setMaximumSize(new Dimension(40, 40));
        avatar.setOpaque(false);
        avatar.setAlignmentX(Component.LEFT_ALIGNMENT);

        nameLabel = new JLabel("Loading...");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(WHITE);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        usnDeptSemLabel = new JLabel(usn);
        usnDeptSemLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        usnDeptSemLabel.setForeground(MUTED_WHITE);
        usnDeptSemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        profile.add(avatar);
        profile.add(Box.createVerticalStrut(10));
        profile.add(nameLabel);
        profile.add(Box.createVerticalStrut(4));
        profile.add(usnDeptSemLabel);

        // Nav Items
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        nav.add(buildNavItem("\uD83D\uDD14 Notifications", true));
        nav.add(Box.createVerticalStrut(4));
        nav.add(buildNavItem("\uD83D\uDEAA Hall Ticket", false));

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        topSection.add(brand);
        topSection.add(profile);
        topSection.add(nav);
        sidebar.add(topSection, BorderLayout.NORTH);

        // Bottom
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(160, 32));
        JLabel l = new JLabel("\uD83D\uDEAA Sign out");
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setForeground(WHITE);
        btn.add(l);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(255,255,255,20)); btn.setOpaque(true); btn.repaint(); }
            @Override public void mouseExited(MouseEvent e) { btn.setOpaque(false); btn.repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                authService.logout(usn);
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
        bottom.add(btn);
        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel buildNavItem(String text, boolean active) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        p.setOpaque(active);
        p.setBackground(new Color(255,255,255,20));
        p.setMaximumSize(new Dimension(180, 34));
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        l.setForeground(active ? WHITE : MUTED_WHITE);
        p.add(l);
        p.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (!active) { p.setBackground(new Color(255,255,255,10)); p.setOpaque(true); p.repaint(); } }
            @Override public void mouseExited(MouseEvent e) { if (!active) { p.setOpaque(false); p.repaint(); } }
        });
        return p;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(MAIN_BG);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(0.5f));
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        topBar.setBackground(WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JPanel titleArea = new JPanel();
        titleArea.setLayout(new BoxLayout(titleArea, BoxLayout.Y_AXIS));
        titleArea.setOpaque(false);
        JLabel title = new JLabel("Student portal");
        title.setFont(new Font("SansSerif", Font.PLAIN, 17)); // 500 medium roughly
        title.setForeground(TEXT_MAIN);
        JLabel sub = new JLabel("View your exam notifications and download your hall ticket");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        titleArea.add(title);
        titleArea.add(Box.createVerticalStrut(4));
        titleArea.add(sub);
        topBar.add(titleArea, BorderLayout.WEST);

        JPanel refreshBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(0.5f));
                g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
            }
        };
        refreshBtn.setOpaque(false);
        refreshBtn.setPreferredSize(new Dimension(100, 32));
        JLabel rL = new JLabel("\u21BB Refresh");
        rL.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rL.setForeground(TEXT_MAIN);
        refreshBtn.add(rL);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { refreshBtn.setBackground(new Color(245,245,245)); refreshBtn.setOpaque(true); refreshBtn.repaint(); }
            @Override public void mouseExited(MouseEvent e) { refreshBtn.setOpaque(false); refreshBtn.repaint(); }
            @Override public void mouseClicked(MouseEvent e) { loadData(); }
        });
        JPanel rightFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        rightFlow.setOpaque(false);
        rightFlow.add(refreshBtn);
        topBar.add(rightFlow, BorderLayout.EAST);
        
        main.add(topBar, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(MAIN_BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 20, 0);
        content.add(buildNotificationsCard(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.6;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(buildHallTicketCard(), gbc);

        main.add(content, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildCard() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(0.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
    }

    private JPanel buildNotificationsCard() {
        JPanel card = buildCard();
        card.setLayout(new BorderLayout());
        card.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        JLabel title = new JLabel("\uD83D\uDD14 Notifications");
        title.setFont(new Font("SansSerif", Font.PLAIN, 15));
        title.setForeground(TEXT_MAIN);
        
        notifBadge = new JLabel("0");
        notifBadge.setFont(new Font("SansSerif", Font.PLAIN, 11));
        notifBadge.setForeground(TEXT_MUTED);
        notifBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        notifBadge.setOpaque(true);
        notifBadge.setBackground(MAIN_BG);
        
        titleRow.add(title);
        titleRow.add(notifBadge);

        JLabel sub = new JLabel("Exam alerts, timetable changes and announcements");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);

        header.add(titleRow, BorderLayout.NORTH);
        header.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        header.add(sub, BorderLayout.SOUTH);

        notifContainer = new JPanel();
        notifContainer.setLayout(new BoxLayout(notifContainer, BoxLayout.Y_AXIS));
        notifContainer.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(notifContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildHallTicketCard() {
        JPanel card = buildCard();
        card.setLayout(new BorderLayout());
        card.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

        JLabel title = new JLabel("\uD83D\uDEAA Hall ticket / exam schedule");
        title.setFont(new Font("SansSerif", Font.PLAIN, 15));
        title.setForeground(TEXT_MAIN);
        
        JLabel sub = new JLabel("Your confirmed exam schedule with room and seat details");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);

        JPanel leftTitle = new JPanel();
        leftTitle.setLayout(new BoxLayout(leftTitle, BoxLayout.Y_AXIS));
        leftTitle.setOpaque(false);
        leftTitle.add(title);
        leftTitle.add(Box.createVerticalStrut(5));
        leftTitle.add(sub);

        JPanel printBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        printBtn.setBackground(NAVY_BG);
        printBtn.setPreferredSize(new Dimension(120, 30));
        JLabel pL = new JLabel("\uD83D\uDDA8 Print / Save");
        pL.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pL.setForeground(WHITE);
        printBtn.add(pL);
        printBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { openHallTicket(); }
        });

        header.add(leftTitle, BorderLayout.WEST);
        header.add(printBtn, BorderLayout.EAST);

        // Table
        String[] cols = {"Date", "Subject", "Subject name", "Type", "Room", "Bench", "Seat"};
        ticketModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ticketTable = new JTable(ticketModel) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // draw bottom border for all rows except last? handled by basic grid if needed, or custom renderer
            }
        };
        ticketTable.setRowHeight(40);
        ticketTable.setShowGrid(false);
        ticketTable.setIntercellSpacing(new Dimension(0, 0));
        ticketTable.setBorder(BorderFactory.createEmptyBorder());
        ticketTable.setOpaque(false);

        // Hover tracking
        ticketTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int row = ticketTable.rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    ticketTable.repaint();
                }
            }
        });
        ticketTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                ticketTable.repaint();
            }
        });

        // Header renderer
        JTableHeader th = ticketTable.getTableHeader();
        th.setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel p = new JPanel(new BorderLayout()) {
                    @Override protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setColor(BORDER_COLOR);
                        g2.setStroke(new BasicStroke(0.5f));
                        g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                    }
                };
                p.setBackground(WHITE);
                p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                JLabel l = new JLabel(value != null ? value.toString().toUpperCase() : "");
                l.setFont(new Font("SansSerif", Font.BOLD, 11));
                l.setForeground(TEXT_MUTED);
                
                Map<TextAttribute, Object> attributes = new HashMap<>(l.getFont().getAttributes());
                attributes.put(TextAttribute.TRACKING, 0.05); // approx 0.5px letter spacing
                l.setFont(l.getFont().deriveFont(attributes));
                
                p.add(l, BorderLayout.WEST);
                return p;
            }
        });
        th.setPreferredSize(new Dimension(0, 40));

        // Cell renderer
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    JLabel l = (JLabel) c;
                    l.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                    l.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    if ("TBA".equals(value)) {
                        l.setFont(new Font("SansSerif", Font.ITALIC, 13));
                        l.setForeground(TEXT_MUTED);
                    } else {
                        l.setForeground(TEXT_MAIN);
                    }
                }
                if (row == hoveredRow) {
                    setBackground(new Color(248, 250, 252));
                } else {
                    setBackground(WHITE);
                }
                return c;
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Bottom border
                int row = ticketTable.getEditingRow() == -1 ? -1 : ticketTable.getEditingRow(); // Hacky, use model check
            }
        };

        // Apply borders in a panel wrapper for rows
        class CustomRenderer implements TableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel p = new JPanel(new BorderLayout()) {
                    @Override protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        if (row < table.getRowCount() - 1) {
                            Graphics2D g2 = (Graphics2D) g;
                            g2.setColor(BORDER_COLOR);
                            g2.setStroke(new BasicStroke(0.5f));
                            g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                        }
                    }
                };
                p.setBackground(row == hoveredRow ? new Color(248, 250, 252) : WHITE);
                
                if (column == 1) { // Subject code chip
                    JPanel chipCont = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
                    chipCont.setOpaque(false);
                    JLabel chip = new JLabel(value.toString()) {
                        @Override protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(241, 245, 249));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                            g2.dispose();
                            super.paintComponent(g);
                        }
                    };
                    chip.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    chip.setForeground(TEXT_MAIN);
                    chip.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                    chipCont.add(chip);
                    p.add(chipCont, BorderLayout.CENTER);
                } else if (column == 3) { // Type pill
                    JPanel pillCont = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
                    pillCont.setOpaque(false);
                    String v = value.toString();
                    boolean isEndSem = v.toLowerCase().contains("end");
                    Color bg = isEndSem ? new Color(219, 234, 254) : new Color(220, 252, 231);
                    Color fg = isEndSem ? new Color(30, 64, 175) : new Color(22, 101, 52);
                    
                    JLabel pill = new JLabel(v) {
                        @Override protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(bg);
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                            g2.dispose();
                            super.paintComponent(g);
                        }
                    };
                    pill.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    pill.setForeground(fg);
                    pill.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                    pillCont.add(pill);
                    p.add(pillCont, BorderLayout.CENTER);
                } else {
                    JLabel l = new JLabel(value != null ? value.toString() : "TBA");
                    l.setFont(new Font("SansSerif", Font.PLAIN, 13));
                    l.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                    if ("TBA".equals(value)) {
                        l.setFont(new Font("SansSerif", Font.ITALIC, 13));
                        l.setForeground(TEXT_MUTED);
                    } else {
                        l.setForeground(TEXT_MAIN);
                    }
                    p.add(l, BorderLayout.CENTER);
                }
                return p;
            }
        }
        
        CustomRenderer cr = new CustomRenderer();
        for (int i=0; i<cols.length; i++) {
            ticketTable.getColumnModel().getColumn(i).setCellRenderer(cr);
        }

        JScrollPane scroll = new JScrollPane(ticketTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        scroll.setOpaque(false);

        card.add(header, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            Student student;
            List<Notification> notifs;
            List<HallTicketEntry> tickets;

            @Override protected Void doInBackground() throws Exception {
                student = portalService.getStudent(usn);
                notifs = portalService.getNotificationsForStudent(usn);
                tickets = portalService.getHallTicket(usn);
                return null;
            }

            @Override protected void done() {
                try {
                    if (student != null) {
                        nameLabel.setText(student.getName());
                        usnDeptSemLabel.setText(student.getUsn() + " • Dept " + student.getDeptId() + " • Sem " + student.getSemester());
                    }
                    
                    notifBadge.setText(String.valueOf(notifs != null ? notifs.size() : 0));
                    notifContainer.removeAll();
                    if (notifs == null || notifs.isEmpty()) {
                        JPanel empty = new JPanel();
                        empty.setLayout(new BoxLayout(empty, BoxLayout.Y_AXIS));
                        empty.setOpaque(false);
                        empty.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
                        JLabel icon = new JLabel("\uD83D\uDD15");
                        icon.setFont(new Font("SansSerif", Font.PLAIN, 28));
                        icon.setForeground(new Color(100, 116, 139, 100)); // muted with opacity
                        icon.setAlignmentX(CENTER_ALIGNMENT);
                        JLabel msg = new JLabel("No notifications yet");
                        msg.setFont(new Font("SansSerif", Font.PLAIN, 13));
                        msg.setForeground(TEXT_MUTED);
                        msg.setAlignmentX(CENTER_ALIGNMENT);
                        empty.add(icon);
                        empty.add(Box.createVerticalStrut(10));
                        empty.add(msg);
                        notifContainer.add(empty);
                    } else {
                        for (int i=0; i<notifs.size(); i++) {
                            Notification n = notifs.get(i);
                            JPanel item = new JPanel(new BorderLayout()) {
                                @Override protected void paintComponent(Graphics g) {
                                    super.paintComponent(g);
                                    Graphics2D g2 = (Graphics2D) g;
                                    g2.setColor(BORDER_COLOR);
                                    g2.setStroke(new BasicStroke(0.5f));
                                    g2.drawLine(20, getHeight()-1, getWidth()-20, getHeight()-1);
                                }
                            };
                            item.setOpaque(false);
                            item.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
                            
                            JPanel text = new JPanel();
                            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
                            text.setOpaque(false);
                            JLabel nT = new JLabel(n.getTitle());
                            nT.setFont(new Font("SansSerif", Font.PLAIN, 14)); // medium approx
                            nT.setForeground(TEXT_MAIN);
                            JLabel nB = new JLabel(n.getBody());
                            nB.setFont(new Font("SansSerif", Font.PLAIN, 12));
                            nB.setForeground(TEXT_MUTED);
                            text.add(nT);
                            text.add(Box.createVerticalStrut(4));
                            text.add(nB);
                            item.add(text, BorderLayout.CENTER);
                            notifContainer.add(item);
                        }
                    }
                    notifContainer.revalidate();
                    notifContainer.repaint();

                    ticketModel.setRowCount(0);
                    if (tickets != null) {
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
                        for (HallTicketEntry e : tickets) {
                            ticketModel.addRow(new Object[]{
                                e.getExamDate() != null ? e.getExamDate().format(fmt) : "TBA",
                                e.getSubjectCode(),
                                e.getSubjectName(),
                                e.getExamType(),
                                e.getRoomNo() != null ? e.getRoomNo() : "TBA",
                                e.getBenchNo() != null ? e.getBenchNo() : "TBA",
                                e.getSeatPosition() != null ? e.getSeatPosition() : "TBA"
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void openHallTicket() {
        try {
            Student student = portalService.getStudent(usn);
            List<HallTicketEntry> entries = portalService.getHallTicket(usn);
            if (student == null || entries == null || entries.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No exam schedule found.");
                return;
            }
            HallTicketDialog.promptAndOpen(this, student, entries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
