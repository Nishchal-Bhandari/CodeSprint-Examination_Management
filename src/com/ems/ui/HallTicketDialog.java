package com.ems.ui;

import com.ems.model.HallTicketEntry;
import com.ems.model.Student;
import com.ems.util.AppTheme;
import com.ems.util.UiUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

public class HallTicketDialog extends JDialog {
    public HallTicketDialog(java.awt.Frame owner, Student student, List<HallTicketEntry> entries) {
        super(owner, "Hall Ticket - " + student.getName() + " (" + student.getUsn() + ")", true);
        setSize(840, 960);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel();
        root.setBackground(Color.WHITE);
        root.setLayout(new BorderLayout());

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Official Hall Ticket", JLabel.CENTER);
        header.setFont(AppTheme.FONT_TITLE);
        header.setForeground(Color.BLACK);
        header.setBorder(BorderFactory.createEmptyBorder(18, 0, 8, 0));
        header.setOpaque(false);

        JPanel info = new JPanel();
        info.setBackground(Color.WHITE);
        info.setLayout(new GridBagLayout());

        GridBagConstraints infoGbc = new GridBagConstraints();
        infoGbc.insets = new Insets(4, 10, 4, 10);
        infoGbc.anchor = GridBagConstraints.WEST;

        JLabel nameLabel = new JLabel("Name: " + student.getName());
        JLabel usnLabel = new JLabel("USN: " + student.getUsn());
        JLabel deptLabel = new JLabel("Dept: " + student.getDeptId());
        JLabel semLabel = new JLabel("Semester: " + student.getSemester());

        JLabel[] detailLabels = {nameLabel, usnLabel, deptLabel, semLabel};
        for (JLabel label : detailLabels) {
            label.setFont(AppTheme.FONT_BODY);
            label.setForeground(Color.BLACK);
        }

        infoGbc.gridx = 0;
        infoGbc.gridy = 0;
        info.add(nameLabel, infoGbc);
        infoGbc.gridx = 1;
        info.add(usnLabel, infoGbc);
        infoGbc.gridx = 2;
        info.add(deptLabel, infoGbc);
        infoGbc.gridx = 3;
        info.add(semLabel, infoGbc);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Date", "Sub Code", "Subject", "Type", "Invigilator Sign"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(new Dimension(650, 600));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setGridColor(Color.BLACK);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setFont(AppTheme.FONT_SUBTITLE);

        // Signature cell renderer: render a white box with border and a small label "Invigilator" below
        TableCellRenderer sigRenderer = new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
                container.setOpaque(false);

                JPanel box = new JPanel();
                box.setBackground(Color.WHITE);
                box.setPreferredSize(new Dimension(160, 28));
                box.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

                javax.swing.JLabel lbl = new javax.swing.JLabel("Invigilator");
                lbl.setFont(AppTheme.FONT_CAPTION);
                lbl.setForeground(Color.BLACK);
                lbl.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

                container.add(box);
                container.add(lbl);
                return container;
            }
        };
        table.getColumnModel().getColumn(4).setCellRenderer(sigRenderer);
        table.setRowHeight(48);

        JPanel tableCard = new JPanel();
        tableCard.setBackground(Color.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.add(info, BorderLayout.NORTH);
        content.add(tableCard, BorderLayout.CENTER);

        for (HallTicketEntry e : entries) {
            model.addRow(new Object[]{
                e.getExamDate(),
                e.getSubjectCode(),
                e.getSubjectName(),
                e.getExamType(),
                "" // signature box rendered by renderer
            });
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        JButton printBtn = UiUtil.buildPrimaryButton("Print");
        JButton closeBtn = UiUtil.buildSecondaryButton("Close");
        actions.add(printBtn);
        actions.add(closeBtn);

        printBtn.addActionListener(ae -> {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("HallTicket-" + student.getUsn());
                job.setPrintable(new Printable() {
                    @Override
                    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                        if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
                        Graphics2D g2 = (Graphics2D) graphics;
                        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                        // scale to fit width
                        double scale = pageFormat.getImageableWidth() / card.getWidth();
                        g2.scale(scale, scale);
                        card.printAll(g2);
                        return Printable.PAGE_EXISTS;
                    }
                });
                if (job.printDialog()) {
                    job.print();
                }
            } catch (PrinterException ex) {
                UiUtil.error(this, ex);
            }
        });

        closeBtn.addActionListener(ae -> dispose());

        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setOpaque(false);
        outer.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        outer.add(card, BorderLayout.CENTER);
        outer.add(actions, BorderLayout.SOUTH);

        root.add(outer, BorderLayout.CENTER);
        setContentPane(root);
    }
}
