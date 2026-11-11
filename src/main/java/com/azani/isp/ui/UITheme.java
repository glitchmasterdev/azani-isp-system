package com.azani.isp.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern Design System & Theme Provider for Azani ISP Information System.
 * Clean, modern SaaS / Web Dashboard look & feel with rounded cards, vibrant accent palette,
 * polished typography, custom buttons, and styled tables.
 */
public class UITheme {

    // --- Modern Web / Dashboard Color Palette ---
    public static final Color SIDEBAR_BG       = new Color(15, 23, 42);      // Slate 900 (Deep modern navbar/sidebar)
    public static final Color SIDEBAR_HOVER    = new Color(30, 41, 59);      // Slate 800
    public static final Color SIDEBAR_ACTIVE   = new Color(37, 99, 235);     // Indigo / Royal Blue 600
    public static final Color SIDEBAR_TEXT     = new Color(148, 163, 184);   // Slate 400
    public static final Color SIDEBAR_TEXT_ACT = Color.WHITE;

    public static final Color APP_BG           = new Color(241, 245, 249);   // Slate 100 (clean off-white background)
    public static final Color CARD_BG          = Color.WHITE;                // Crisp card container background
    public static final Color BORDER_COLOR     = new Color(226, 232, 240);   // Slate 200 border
    public static final Color BORDER_FOCUS     = new Color(59, 130, 246);    // Blue 500

    public static final Color TEXT_PRIMARY     = new Color(15, 23, 42);      // Slate 900
    public static final Color TEXT_SECONDARY   = new Color(100, 116, 139);   // Slate 500
    public static final Color TEXT_MUTED       = new Color(148, 163, 184);   // Slate 400

    // Semantic Accents
    public static final Color PRIMARY_BLUE     = new Color(37, 99, 235);     // Blue 600
    public static final Color PRIMARY_HOVER    = new Color(29, 78, 216);     // Blue 700
    public static final Color SUCCESS_EMERALD  = new Color(16, 185, 129);    // Emerald 500
    public static final Color SUCCESS_HOVER    = new Color(5, 150, 105);     // Emerald 600
    public static final Color DANGER_ROSE      = new Color(239, 68, 68);     // Rose 500
    public static final Color DANGER_HOVER     = new Color(220, 38, 38);     // Rose 600
    public static final Color WARNING_AMBER    = new Color(245, 158, 11);    // Amber 500
    public static final Color PURPLE_ACCENT    = new Color(147, 51, 234);    // Purple 600
    public static final Color PURPLE_HOVER     = new Color(126, 34, 206);    // Purple 700

    // Table Styling
    public static final Color TABLE_HEADER_BG  = new Color(248, 250, 252);   // Slate 50
    public static final Color TABLE_GRID       = new Color(241, 245, 249);
    public static final Color TABLE_ROW_ALT    = new Color(249, 250, 251);
    public static final Color TABLE_SEL_BG     = new Color(224, 231, 255);   // Indigo 100
    public static final Color TABLE_SEL_FG     = new Color(30, 27, 75);

    // --- Fonts ---
    public static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_SUBTITLE     = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SECTION      = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD         = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO         = new Font("Consolas", Font.PLAIN, 12);

    /**
     * Creates a rounded card container with crisp border and padding.
     */
    public static JPanel createCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.setColor(BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        if (title != null && !title.isEmpty()) {
            JPanel header = new JPanel(new GridLayout(subtitle != null ? 2 : 1, 1, 0, 2));
            header.setOpaque(false);
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(FONT_SECTION);
            lblTitle.setForeground(TEXT_PRIMARY);
            header.add(lblTitle);

            if (subtitle != null && !subtitle.isEmpty()) {
                JLabel lblSub = new JLabel(subtitle);
                lblSub.setFont(FONT_SUBTITLE);
                lblSub.setForeground(TEXT_SECONDARY);
                header.add(lblSub);
            }
            card.add(header, BorderLayout.NORTH);
        }

        return card;
    }

    /**
     * Modern Styled Button with custom hover animation and color styles.
     */
    public static JButton createButton(String text, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? (hovered ? hoverColor : baseColor) : new Color(203, 213, 225));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
                int textX = (getWidth() - stringBounds.width) / 2;
                int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

                g2.setColor(isEnabled() ? Color.WHITE : new Color(148, 163, 184));
                g2.setFont(getFont());
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };

        btn.setFont(FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 24, 38));
        return btn;
    }

    /**
     * Convenience button creators for semantic palettes.
     */
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY_BLUE, PRIMARY_HOVER);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, SUCCESS_EMERALD, SUCCESS_HOVER);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER_ROSE, DANGER_HOVER);
    }

    public static JButton createPurpleButton(String text) {
        return createButton(text, PURPLE_ACCENT, PURPLE_HOVER);
    }

    /**
     * Modern Outline / Secondary Button
     */
    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? new Color(241, 245, 249) : Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.setColor(hovered ? PRIMARY_BLUE : BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

                FontMetrics fm = g2.getFontMetrics();
                Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
                int textX = (getWidth() - stringBounds.width) / 2;
                int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

                g2.setColor(hovered ? PRIMARY_BLUE : TEXT_PRIMARY);
                g2.setFont(getFont());
                g2.drawString(getText(), textX, textY);
                g2.dispose();
            }
        };

        btn.setFont(FONT_BOLD);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 20, 36));
        return btn;
    }

    /**
     * Styles text input fields with clean borders, padding, and subtle focus colors.
     */
    public static void styleTextField(JTextField f) {
        f.setFont(FONT_REGULAR);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(Color.WHITE);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    /**
     * Styles ComboBox to match modern form aesthetics.
     */
    public static void styleComboBox(JComboBox<?> cmb) {
        cmb.setFont(FONT_REGULAR);
        cmb.setBackground(Color.WHITE);
        cmb.setForeground(TEXT_PRIMARY);
        cmb.setFocusable(false);
    }

    /**
     * Styles JTable to look like modern SaaS Web DataTables:
     * High row height, muted header with clear typography, alternating rows, clean selection.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(TABLE_GRID);
        table.setSelectionBackground(TABLE_SEL_BG);
        table.setSelectionForeground(TABLE_SEL_FG);
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_SECONDARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean isSel, boolean hasFoc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, isSel, hasFoc, row, col);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (!isSel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ROW_ALT);
                    c.setForeground(TEXT_PRIMARY);
                } else {
                    c.setBackground(TABLE_SEL_BG);
                    c.setForeground(TABLE_SEL_FG);
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    /**
     * Creates a Stat/KPI Banner Card showing quick metric figures (like modern web dashboards).
     */
    public static JPanel createStatBadge(String title, String value, Color accentColor) {
        JPanel p = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                // Left accent bar
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, 5, getHeight() - 1, 4, 4));
                g2.setColor(BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblVal.setForeground(TEXT_PRIMARY);

        p.add(lblTitle, BorderLayout.NORTH);
        p.add(lblVal, BorderLayout.CENTER);
        return p;
    }
}
