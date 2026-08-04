package com.ems.ui.panels;

import com.ems.service.ai.OpenRouterClient;
import com.ems.util.AnimationEngine;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Conversational EMS AI Copilot Panel.
 * Provides interactive AI chat capabilities powered by OpenRouter (Llama 3.3 70B Free).
 */
public class AiCopilotPanel extends JPanel {

    private final OpenRouterClient aiClient = new OpenRouterClient();
    private final JPanel chatBox;
    private final JScrollPane scrollPane;
    private final JTextField inputField;
    private final JButton sendBtn;
    private final JLabel statusTextLabel;

    public AiCopilotPanel() {
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);

        // Header
        add(createHeader(), BorderLayout.NORTH);

        // Chat Container
        chatBox = new JPanel();
        chatBox.setLayout(new BoxLayout(chatBox, BoxLayout.Y_AXIS));
        chatBox.setOpaque(false);
        chatBox.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = UiUtil.buildDarkScrollPane(chatBox);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel chatCard = UiUtil.buildSurfaceCard();
        chatCard.setLayout(new BorderLayout());
        chatCard.add(scrollPane, BorderLayout.CENTER);

        add(chatCard, BorderLayout.CENTER);

        // Bottom Controls: Quick Chips + Input Row
        JPanel bottomStack = new JPanel(new BorderLayout(0, 8));
        bottomStack.setOpaque(false);

        bottomStack.add(createQuickChipsBar(), BorderLayout.NORTH);

        // Input Form Card
        JPanel inputCard = UiUtil.buildSurfaceCard();
        inputCard.setLayout(new BorderLayout(10, 0));
        inputCard.setBorder(new EmptyBorder(8, 12, 8, 12));

        inputField = new JTextField();
        UiUtil.styleInput(inputField, 400);

        sendBtn = UiUtil.buildPrimaryButton("Send 🚀");
        statusTextLabel = new JLabel("⚡ Powered by OpenRouter Free Auto-Router API");
        statusTextLabel.setFont(AppTheme.FONT_CAPTION);
        statusTextLabel.setForeground(AppTheme.TEXT_LIGHT);

        JPanel inputRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        inputRight.setOpaque(false);
        inputRight.add(statusTextLabel);
        inputRight.add(sendBtn);

        inputCard.add(inputField, BorderLayout.CENTER);
        inputCard.add(inputRight, BorderLayout.EAST);

        bottomStack.add(inputCard, BorderLayout.SOUTH);
        add(bottomStack, BorderLayout.SOUTH);

        // Event Handlers
        sendBtn.addActionListener(e -> sendMessage());
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        // Initial welcome message
        appendAiMessage("Hello! 👋 I am your **Examination Assistant**.\n\n"
                + "I can help you with:\n"
                + "• Drafting official malpractice reports\n"
                + "• Reviewing invigilator duty rules & department conflict constraints\n"
                + "• Checking exam seating regulations & washroom policies\n\n"
                + "Ask me any question below or click a quick action chip!");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = UiUtil.buildPageHeader(
                "🤖 Examination Assistant",
                "Conversational Assistant powered by OpenRouter (Free Auto-Router Engine)"
        );

        // Status badge
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.SUCCESS_SOFT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(AppTheme.SUCCESS);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(4, 10, 4, 10));

        JLabel dot = new JLabel("●");
        dot.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dot.setForeground(AppTheme.SUCCESS);

        JLabel badgeText = new JLabel("AI Online (Free Tier)");
        badgeText.setFont(AppTheme.FONT_CAPTION);
        badgeText.setForeground(Color.WHITE);

        badge.add(dot);
        badge.add(badgeText);

        header.add(titles, BorderLayout.WEST);
        header.add(badge, BorderLayout.EAST);

        return header;
    }

    private JPanel createQuickChipsBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setOpaque(false);

        JLabel promptLabel = new JLabel("Quick Actions:");
        promptLabel.setFont(AppTheme.FONT_CAPTION);
        promptLabel.setForeground(AppTheme.TEXT_LIGHT);
        bar.add(promptLabel);

        bar.add(createChip("📝 Malpractice Report Draft", "Draft a formal malpractice incident report for a student caught copying from a mobile phone."));
        bar.add(createChip("🛡️ Invigilator Duty Rules", "What are the department constraint rules for assigning faculty invigilators to exam rooms?"));
        bar.add(createChip("🚽 Washroom Log Policy", "Summarize the student washroom exit and return logging procedure during exams."));
        bar.add(createChip("🎟️ Hall Ticket Criteria", "What criteria must a student meet to receive an official Hall Ticket?"));

        return bar;
    }

    private JButton createChip(String label, String query) {
        JButton chip = UiUtil.buildSecondaryButton(label);
        chip.setFont(AppTheme.FONT_CAPTION);
        chip.addActionListener(e -> {
            inputField.setText(query);
            sendMessage();
        });
        return chip;
    }

    private void sendMessage() {
        String query = inputField.getText().trim();
        if (query.isEmpty()) return;

        inputField.setText("");
        sendBtn.setEnabled(false);
        statusTextLabel.setText("🤖 Thinking…");

        // Add user message bubble
        appendUserMessage(query);

        // Async API Call
        aiClient.askAsync(query).thenAccept(reply -> SwingUtilities.invokeLater(() -> {
            sendBtn.setEnabled(true);
            statusTextLabel.setText("⚡ Powered by Llama 3.3 70B via OpenRouter Free API");
            appendAiMessage(reply);
        }));
    }

    private void appendUserMessage(String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JPanel bubble = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel msg = new JLabel("<html><body style='width: 380px; color: #FFFFFF; font-family: Segoe UI; font-size: 13px;'>"
                + escapeHtml(text) + "</body></html>");
        bubble.add(msg, BorderLayout.CENTER);

        row.add(bubble);
        chatBox.add(row);
        chatBox.add(Box.createVerticalStrut(8));
        scrollToBottom();
    }

    private void appendAiMessage(String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        row.setOpaque(false);

        JPanel bubble = new JPanel(new BorderLayout(8, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PANEL_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                // Left accent border
                g2.setColor(AppTheme.ACCENT);
                g2.fill(new RoundRectangle2D.Float(0, 0, 4, getHeight(), 4, 4));
                g2.setColor(AppTheme.BORDER);
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(12, 16, 12, 16));

        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String htmlText = formatMarkdownToHtml(text);

        JLabel msg = new JLabel("<html><body style='width: 540px; color: #F8FAFC; font-family: Segoe UI; font-size: 13px;'>"
                + htmlText
                + "<br><div style='text-align: right; color: #94A3B8; font-size: 10px; margin-top: 4px;'>" + time + " • Examination Assistant</div>"
                + "</body></html>");

        bubble.add(msg, BorderLayout.CENTER);

        row.add(bubble);
        chatBox.add(row);
        chatBox.add(Box.createVerticalStrut(8));
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatBox.revalidate();
        chatBox.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
    }

    private String formatMarkdownToHtml(String text) {
        if (text == null) return "";
        String html = escapeHtml(text);
        // Basic markdown formatting: **bold**, *italic*, bullet points
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        html = html.replaceAll("\\*(.*?)\\*", "<i>$1</i>");
        html = html.replaceAll("• ", "&bull; ");
        return html;
    }
}
