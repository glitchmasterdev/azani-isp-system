package com.azani.isp.ui;

import com.azani.isp.dao.DatabaseConnection;
import com.azani.isp.model.*;
import com.azani.isp.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Website-styled Desktop UI for Azani ISP Information System.
 * Designed with a modern SaaS / Web Dashboard aesthetic:
 * - Sleek dark slate sidebar with active navigation pills & badge indicators
 * - Clean off-white canvas with card-based elevation and rounded corners
 * - Top-level KPI / quick stats bar
 * - Color-coded semantic buttons and state-aware interactive hover states
 * - Modern data tables with generous padding and row highlights
 */
public class MainWindow extends JFrame {
    private final InstitutionService institutionService;
    private final InfrastructureService infrastructureService;
    private final BillingService billingService;
    private final PaymentService paymentService;
    private final ComputationService computationService;
    private final ReportService reportService;

    // View Switching
    private CardLayout cardLayout;
    private JPanel contentContainer;
    private final List<JButton> navButtons = new ArrayList<>();

    // Quick Stats Bar Labels
    private JLabel statInstitutionsVal;
    private JLabel statActiveSubsVal;
    private JLabel statDefaultersVal;
    private JLabel statTotalRevenueVal;

    // Tables & Models
    private JTable institutionsTable;
    private DefaultTableModel institutionsModel;

    private JTable infraTable;
    private DefaultTableModel infraModel;

    private JTable subsTable;
    private DefaultTableModel subsModel;

    private JTable paymentsTable;
    private DefaultTableModel paymentsModel;

    private JTable billsTable;
    private DefaultTableModel billsModel;

    private JTextArea reportsTextArea;

    public MainWindow() {
        super("Azani ISP - Cloud Management Dashboard");
        this.institutionService = new InstitutionService();
        this.infrastructureService = new InfrastructureService();
        this.billingService = new BillingService();
        this.paymentService = new PaymentService();
        this.computationService = new ComputationService();
        this.reportService = new ReportService();

        initUI();
        refreshAllData();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1260, 800);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.APP_BG);
        setLayout(new BorderLayout());

        // 1. Sleek Modern Sidebar
        add(createSidebar(), BorderLayout.WEST);

        // 2. Main Work Area (Top Header + Dashboard Content)
        JPanel mainArea = new JPanel(new BorderLayout(0, 10));
        mainArea.setBackground(UITheme.APP_BG);
        mainArea.setBorder(BorderFactory.createEmptyBorder(12, 16, 14, 16));

        mainArea.add(createTopBar(), BorderLayout.NORTH);

        // 3. Central Card Container for Views
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setOpaque(false);

        contentContainer.add(createRegistrationView(), "VIEW_REGISTRATION");
        contentContainer.add(createInfrastructureView(), "VIEW_INFRASTRUCTURE");
        contentContainer.add(createSubscriptionView(), "VIEW_SUBSCRIPTIONS");
        contentContainer.add(createPaymentView(), "VIEW_PAYMENTS");
        contentContainer.add(createBillingView(), "VIEW_BILLING");
        contentContainer.add(createReportsView(), "VIEW_REPORTS");

        mainArea.add(contentContainer, BorderLayout.CENTER);
        add(mainArea, BorderLayout.CENTER);

        // Default to first view
        switchView(0, "VIEW_REGISTRATION");
    }

    /**
     * Creates modern SaaS Sidebar Navigation.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));

        // Brand Logo & Title
        JPanel brandPanel = new JPanel(new BorderLayout(10, 0));
        brandPanel.setOpaque(false);

        JLabel logoBadge = new JLabel(" A ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY_BLUE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoBadge.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoBadge.setForeground(Color.WHITE);
        logoBadge.setHorizontalAlignment(SwingConstants.CENTER);
        logoBadge.setPreferredSize(new Dimension(36, 36));

        JPanel brandText = new JPanel(new GridLayout(2, 1, 0, 1));
        brandText.setOpaque(false);
        JLabel brandName = new JLabel("Azani ISP");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandName.setForeground(Color.WHITE);

        JLabel brandDesc = new JLabel("Enterprise Portal");
        brandDesc.setFont(UITheme.FONT_SMALL);
        brandDesc.setForeground(UITheme.SIDEBAR_TEXT);

        brandText.add(brandName);
        brandText.add(brandDesc);

        brandPanel.add(logoBadge, BorderLayout.WEST);
        brandPanel.add(brandText, BorderLayout.CENTER);

        // Navigation Menu
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(24, 0, 0, 0));

        JLabel menuHeading = new JLabel("MANAGEMENT");
        menuHeading.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuHeading.setForeground(new Color(100, 116, 139));
        menuHeading.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 0));
        menuPanel.add(menuHeading);

        String[] navLabels = {
                "Institutions",
                "Site Assessment",
                "Bandwidth & Plans",
                "Payment Capture",
                "Billing & Overdue",
                "Analytics & Reports"
        };

        String[] cardNames = {
                "VIEW_REGISTRATION",
                "VIEW_INFRASTRUCTURE",
                "VIEW_SUBSCRIPTIONS",
                "VIEW_PAYMENTS",
                "VIEW_BILLING",
                "VIEW_REPORTS"
        };

        for (int i = 0; i < navLabels.length; i++) {
            final int index = i;
            final String cardName = cardNames[i];

            JButton navBtn = new JButton("  " + navLabels[i]) {
                private boolean hovered = false;

                {
                    addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                        public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                    });
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean isActive = !isEnabled();

                    if (isActive) {
                        g2.setColor(UITheme.SIDEBAR_ACTIVE);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    } else if (hovered) {
                        g2.setColor(UITheme.SIDEBAR_HOVER);
                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                    }

                    g2.dispose();
                    super.paintComponent(g);
                }
            };

            navBtn.setFont(UITheme.FONT_BOLD);
            navBtn.setForeground(UITheme.SIDEBAR_TEXT);
            navBtn.setHorizontalAlignment(SwingConstants.LEFT);
            navBtn.setContentAreaFilled(false);
            navBtn.setBorderPainted(false);
            navBtn.setFocusPainted(false);
            navBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            navBtn.setMaximumSize(new Dimension(240, 40));
            navBtn.setPreferredSize(new Dimension(240, 40));
            navBtn.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

            navBtn.addActionListener(e -> switchView(index, cardName));
            navButtons.add(navBtn);
            menuPanel.add(navBtn);
            menuPanel.add(Box.createVerticalStrut(4));
        }

        // Bottom section with Database pill & version
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setOpaque(false);

        JPanel dbBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.SIDEBAR_HOVER);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
            }
        };
        dbBadge.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setForeground(UITheme.SUCCESS_EMERALD);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel dbText = new JLabel("Live: " + DatabaseConnection.getActiveDbType().toUpperCase());
        dbText.setFont(UITheme.FONT_SMALL);
        dbText.setForeground(Color.WHITE);

        dbBadge.add(dot);
        dbBadge.add(dbText);

        JLabel version = new JLabel("Azani v1.0 • Stable Build");
        version.setFont(UITheme.FONT_SMALL);
        version.setForeground(UITheme.TEXT_MUTED);
        version.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));

        bottomPanel.add(dbBadge, BorderLayout.NORTH);
        bottomPanel.add(version, BorderLayout.SOUTH);

        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    /**
     * Switches view and updates sidebar active highlight.
     */
    private void switchView(int selectedIndex, String cardName) {
        cardLayout.show(contentContainer, cardName);
        for (int i = 0; i < navButtons.size(); i++) {
            JButton btn = navButtons.get(i);
            if (i == selectedIndex) {
                btn.setEnabled(false);
                btn.setForeground(UITheme.SIDEBAR_TEXT_ACT);
            } else {
                btn.setEnabled(true);
                btn.setForeground(UITheme.SIDEBAR_TEXT);
            }
        }
    }

    /**
     * Top bar with stats cards and action buttons.
     */
    private JPanel createTopBar() {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setOpaque(false);

        // Header Actions Row
        JPanel actionRow = new JPanel(new BorderLayout());
        actionRow.setOpaque(false);

        JPanel titleBlock = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBlock.setOpaque(false);
        JLabel pageTitle = new JLabel("Service Operations Dashboard");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel pageSub = new JLabel("Monitor registrations, installations, bandwidth plans, and automated invoicing");
        pageSub.setFont(UITheme.FONT_SUBTITLE);
        pageSub.setForeground(UITheme.TEXT_SECONDARY);

        titleBlock.add(pageTitle);
        titleBlock.add(pageSub);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBtns.setOpaque(false);

        JButton btnConfig = UITheme.createOutlineButton("⚙ Database Settings");
        btnConfig.addActionListener(e -> showDatabaseConfigDialog());

        JButton btnSeed = UITheme.createOutlineButton("⚡ Demo Data");
        btnSeed.addActionListener(e -> {
            DatabaseConnection.seedSampleData();
            refreshAllData();
            JOptionPane.showMessageDialog(this, "Sample records loaded successfully.", "Seed Data", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnRefresh = UITheme.createPrimaryButton("↻ Refresh All");
        btnRefresh.addActionListener(e -> {
            refreshAllData();
            JOptionPane.showMessageDialog(this, "All records and analytics refreshed.", "Synced", JOptionPane.INFORMATION_MESSAGE);
        });

        actionBtns.add(btnConfig);
        actionBtns.add(btnSeed);
        actionBtns.add(btnRefresh);

        actionRow.add(titleBlock, BorderLayout.WEST);
        actionRow.add(actionBtns, BorderLayout.EAST);

        // Quick Stats Row
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 12, 0));
        statsGrid.setOpaque(false);

        statInstitutionsVal = new JLabel("0");
        statActiveSubsVal = new JLabel("0");
        statDefaultersVal = new JLabel("0");
        statTotalRevenueVal = new JLabel("KSh 0.00");

        statsGrid.add(createKpiCard("TOTAL INSTITUTIONS", statInstitutionsVal, UITheme.PRIMARY_BLUE));
        statsGrid.add(createKpiCard("ACTIVE SUBSCRIPTIONS", statActiveSubsVal, UITheme.SUCCESS_EMERALD));
        statsGrid.add(createKpiCard("DEFAULTERS & OVERDUE", statDefaultersVal, UITheme.DANGER_ROSE));
        statsGrid.add(createKpiCard("PAYMENTS CAPTURED", statTotalRevenueVal, UITheme.PURPLE_ACCENT));

        container.add(actionRow, BorderLayout.NORTH);
        container.add(statsGrid, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createKpiCard(String label, JLabel valLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                // Accent left border
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, 5, getHeight() - 1, 4, 4));
                g2.setColor(UITheme.BORDER_COLOR);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        valLabel.setForeground(UITheme.TEXT_PRIMARY);

        card.add(lbl, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // VIEW 1: Institution Registration
    // =========================================================================
    private JPanel createRegistrationView() {
        JPanel view = new JPanel(new BorderLayout(12, 12));
        view.setOpaque(false);

        // Left Card: Form
        JPanel formCard = UITheme.createCard("Register Institution", "Add new school or college with authorized contact person");
        formCard.setPreferredSize(new Dimension(380, 0));

        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 4, 3, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField txtName = new JTextField(16);
        JComboBox<InstitutionCategory> cmbCategory = new JComboBox<>(InstitutionCategory.values());
        JTextField txtAddress = new JTextField(16);
        JTextField txtContactName = new JTextField(16);
        JTextField txtDesignation = new JTextField(16);
        JTextField txtPhone = new JTextField(16);
        JTextField txtEmail = new JTextField(16);
        JTextField txtNationalId = new JTextField(16);

        for (JTextField f : new JTextField[]{txtName, txtAddress, txtContactName, txtDesignation, txtPhone, txtEmail, txtNationalId}) {
            UITheme.styleTextField(f);
        }
        UITheme.styleComboBox(cmbCategory);

        addFormField(formBody, gbc, "Institution Name:", txtName);
        addFormField(formBody, gbc, "Category:", cmbCategory);
        addFormField(formBody, gbc, "Physical Address:", txtAddress);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JLabel sectionLbl = new JLabel("CONTACT PERSON DETAILS");
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sectionLbl.setForeground(UITheme.PRIMARY_BLUE);
        sectionLbl.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));
        formBody.add(sectionLbl, gbc);
        gbc.gridwidth = 1;

        addFormField(formBody, gbc, "Full Name:", txtContactName);
        addFormField(formBody, gbc, "Designation:", txtDesignation);
        addFormField(formBody, gbc, "Phone Number:", txtPhone);
        addFormField(formBody, gbc, "Email Address:", txtEmail);
        addFormField(formBody, gbc, "National/Staff ID:", txtNationalId);

        JButton btnRegister = UITheme.createPrimaryButton("Register Institution");
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 4, 4, 4);
        formBody.add(btnRegister, gbc);

        btnRegister.addActionListener(e -> {
            try {
                Institution inst = institutionService.registerInstitution(
                        txtName.getText(), (InstitutionCategory) cmbCategory.getSelectedItem(),
                        txtAddress.getText(), LocalDate.now(),
                        txtContactName.getText(), txtDesignation.getText(),
                        txtPhone.getText(), txtEmail.getText(), txtNationalId.getText()
                );
                JOptionPane.showMessageDialog(this, "Institution Registered Successfully!\n" + inst.getName() +
                        "\nMandatory Registration Fee: KSh 8,500", "Success", JOptionPane.INFORMATION_MESSAGE);
                txtName.setText(""); txtAddress.setText(""); txtContactName.setText("");
                txtDesignation.setText(""); txtPhone.setText(""); txtEmail.setText(""); txtNationalId.setText("");
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Registration Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(formBody, BorderLayout.CENTER);
        view.add(formCard, BorderLayout.WEST);

        // Right Card: Table
        JPanel tableCard = UITheme.createCard("Registered Learning Institutions", "All active, defaulter, or disconnected institutions in the database");
        institutionsModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Contact Person", "Phone", "Email", "Status"}, 0);
        institutionsTable = new JTable(institutionsModel);
        UITheme.styleTable(institutionsTable);

        JScrollPane scroll = new JScrollPane(institutionsTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        tableCard.add(scroll, BorderLayout.CENTER);

        // Revoke Action Footer
        JPanel actionFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        actionFooter.setOpaque(false);

        JButton btnRevoke = UITheme.createDangerButton("Revoke Selected Membership");
        btnRevoke.addActionListener(e -> {
            int row = institutionsTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select an institution row from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int instId = (Integer) institutionsModel.getValueAt(row, 0);
            String instName = (String) institutionsModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to permanently revoke membership for:\n\"" + instName + "\" (ID: " + instId + ")?\n\nThis will remove all associated infrastructure, subscriptions, and billing data.",
                    "Confirm Revocation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    institutionService.revokeInstitution(instId);
                    JOptionPane.showMessageDialog(this, "Membership revoked successfully for: " + instName, "Revoked", JOptionPane.INFORMATION_MESSAGE);
                    refreshAllData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error revoking membership: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actionFooter.add(btnRevoke);
        tableCard.add(actionFooter, BorderLayout.SOUTH);

        view.add(tableCard, BorderLayout.CENTER);
        return view;
    }

    // =========================================================================
    // VIEW 2: Infrastructure & Site Readiness
    // =========================================================================
    private JPanel createInfrastructureView() {
        JPanel view = new JPanel(new BorderLayout(12, 12));
        view.setOpaque(false);

        // Left Form Card
        JPanel formCard = UITheme.createCard("Site Assessment & Readiness", "Calculate computer laboratory hardware, LAN nodes, and installation costs");
        formCard.setPreferredSize(new Dimension(380, 0));

        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField txtInstId = new JTextField(14);
        JTextField txtUsers = new JTextField("50", 14);
        JCheckBox chkReady = new JCheckBox("Site already equipped and ready");
        chkReady.setFont(UITheme.FONT_REGULAR);
        chkReady.setOpaque(false);

        JTextField txtPcs = new JTextField("0", 14);
        JTextField txtLanNodes = new JTextField("0", 14);
        JLabel lblCostPreview = new JLabel("Est. Total Installation: KSh 10,000.00");
        lblCostPreview.setFont(UITheme.FONT_BOLD);
        lblCostPreview.setForeground(UITheme.PRIMARY_BLUE);

        for (JTextField f : new JTextField[]{txtInstId, txtUsers, txtPcs, txtLanNodes}) {
            UITheme.styleTextField(f);
        }

        addFormField(formBody, gbc, "Institution ID:", txtInstId);
        addFormField(formBody, gbc, "User Count:", txtUsers);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        formBody.add(chkReady, gbc);
        gbc.gridwidth = 1;

        addFormField(formBody, gbc, "PCs Required (KSh 40,000):", txtPcs);
        addFormField(formBody, gbc, "LAN Nodes (Tiered):", txtLanNodes);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        formBody.add(lblCostPreview, gbc);

        JButton btnSaveInfra = UITheme.createPrimaryButton("Save Assessment");
        gbc.gridy++;
        formBody.add(btnSaveInfra, gbc);

        btnSaveInfra.addActionListener(e -> {
            try {
                int instId = Integer.parseInt(txtInstId.getText().trim());
                int users = Integer.parseInt(txtUsers.getText().trim());
                boolean ready = chkReady.isSelected();
                int pcs = Integer.parseInt(txtPcs.getText().trim());
                int nodes = Integer.parseInt(txtLanNodes.getText().trim());

                Infrastructure inf = infrastructureService.assessInfrastructure(instId, users, ready, pcs, nodes);
                lblCostPreview.setText(String.format("Est. Total Installation: KSh %,.2f", inf.getTotalInstallationCost()));
                JOptionPane.showMessageDialog(this, "Assessment Saved!\nPC Cost: KSh " + String.format("%,.2f", inf.getPcCost()) +
                        "\nLAN Cost: KSh " + String.format("%,.2f", inf.getLanCost()) +
                        "\nTotal Installation: KSh " + String.format("%,.2f", inf.getTotalInstallationCost()), "Assessment Saved", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(formBody, BorderLayout.CENTER);
        view.add(formCard, BorderLayout.WEST);

        // Right Table Card
        JPanel tableCard = UITheme.createCard("Institutional Infrastructure Records", "Detailed breakdown of assessed requirements per institution");
        infraModel = new DefaultTableModel(new String[]{"Inst ID", "Users", "Ready?", "PCs Purchased", "PC Cost", "LAN Nodes", "LAN Cost", "Total Install Fee"}, 0);
        infraTable = new JTable(infraModel);
        UITheme.styleTable(infraTable);

        JScrollPane scroll = new JScrollPane(infraTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        tableCard.add(scroll, BorderLayout.CENTER);

        view.add(tableCard, BorderLayout.CENTER);
        return view;
    }

    // =========================================================================
    // VIEW 3: Subscriptions & Bandwidth Upgrades
    // =========================================================================
    private JPanel createSubscriptionView() {
        JPanel view = new JPanel(new BorderLayout(12, 12));
        view.setOpaque(false);

        // Left Form Card
        JPanel formCard = UITheme.createCard("Bandwidth Plans & Upgrades", "Provision internet bandwidth and apply 10% upgrade discounts");
        formCard.setPreferredSize(new Dimension(380, 0));

        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField txtSubInstId = new JTextField(14);
        UITheme.styleTextField(txtSubInstId);

        JComboBox<BandwidthPackage> cmbPackage = new JComboBox<>(BandwidthPackage.values());
        UITheme.styleComboBox(cmbPackage);

        JCheckBox chkUpgrade = new JCheckBox("Upgrade Package (10% Discount Applied)");
        chkUpgrade.setFont(UITheme.FONT_BOLD);
        chkUpgrade.setForeground(UITheme.SUCCESS_EMERALD);
        chkUpgrade.setOpaque(false);

        addFormField(formBody, gbc, "Institution ID:", txtSubInstId);
        addFormField(formBody, gbc, "Bandwidth Plan:", cmbPackage);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        formBody.add(chkUpgrade, gbc);

        JButton btnApply = UITheme.createSuccessButton("Save Subscription / Upgrade");
        gbc.gridy++;
        formBody.add(btnApply, gbc);

        btnApply.addActionListener(e -> {
            try {
                int instId = Integer.parseInt(txtSubInstId.getText().trim());
                BandwidthPackage pkg = (BandwidthPackage) cmbPackage.getSelectedItem();
                boolean isUpgrade = chkUpgrade.isSelected();

                Subscription sub;
                if (isUpgrade) {
                    sub = billingService.upgradeSubscription(instId, pkg);
                    JOptionPane.showMessageDialog(this, "Bandwidth upgrade applied!\n10% discount credited.\nNew Monthly Charge: KSh " +
                            String.format("%,.2f", sub.getFinalMonthlyCost()), "Upgrade Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    sub = billingService.subscribe(instId, pkg, LocalDate.now());
                    JOptionPane.showMessageDialog(this, "Subscription active!\nMonthly Charge: KSh " +
                            String.format("%,.2f", sub.getFinalMonthlyCost()), "Subscribed", JOptionPane.INFORMATION_MESSAGE);
                }
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(formBody, BorderLayout.CENTER);
        view.add(formCard, BorderLayout.WEST);

        // Right Table Card
        JPanel tableCard = UITheme.createCard("Active Subscriptions & Provisioning", "Provisioned packages, base rates, discounts, and final monthly fees");
        subsModel = new DefaultTableModel(new String[]{"Inst ID", "Bandwidth", "Base Rate (KSh)", "Upgraded?", "Discount", "Final Monthly (KSh)", "Active"}, 0);
        subsTable = new JTable(subsModel);
        UITheme.styleTable(subsTable);

        JScrollPane scroll = new JScrollPane(subsTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        tableCard.add(scroll, BorderLayout.CENTER);

        view.add(tableCard, BorderLayout.CENTER);
        return view;
    }

    // =========================================================================
    // VIEW 4: Payment Capture
    // =========================================================================
    private JPanel createPaymentView() {
        JPanel view = new JPanel(new BorderLayout(12, 12));
        view.setOpaque(false);

        // Left Form Card
        JPanel formCard = UITheme.createCard("Capture Institution Payment", "Record payments for Registration, Installation, Monthly internet, or Reconnection");
        formCard.setPreferredSize(new Dimension(380, 0));

        JPanel formBody = new JPanel(new GridBagLayout());
        formBody.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JComboBox<PaymentType> cmbPayType = new JComboBox<>(PaymentType.values());
        UITheme.styleComboBox(cmbPayType);

        JTextField txtPayInstId = new JTextField(14);
        JTextField txtAmount = new JTextField(14);
        JTextField txtRefNo = new JTextField(14);
        JTextField txtNotes = new JTextField(14);

        for (JTextField f : new JTextField[]{txtPayInstId, txtAmount, txtRefNo, txtNotes}) {
            UITheme.styleTextField(f);
        }

        cmbPayType.addActionListener(e -> {
            PaymentType pt = (PaymentType) cmbPayType.getSelectedItem();
            if (pt == PaymentType.REGISTRATION) {
                txtAmount.setText("8500.00");
                txtAmount.setEditable(false);
            } else if (pt == PaymentType.RECONNECTION) {
                txtAmount.setText("1000.00");
                txtAmount.setEditable(false);
            } else {
                txtAmount.setEditable(true);
            }
        });
        cmbPayType.setSelectedItem(PaymentType.REGISTRATION);

        addFormField(formBody, gbc, "Payment Category:", cmbPayType);
        addFormField(formBody, gbc, "Institution ID:", txtPayInstId);
        addFormField(formBody, gbc, "Amount (KSh):", txtAmount);
        addFormField(formBody, gbc, "Transaction Ref #:", txtRefNo);
        addFormField(formBody, gbc, "Payment Notes:", txtNotes);

        JButton btnCapture = UITheme.createPurpleButton("Capture & Reconcile Payment");
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        formBody.add(btnCapture, gbc);

        btnCapture.addActionListener(e -> {
            try {
                int instId = Integer.parseInt(txtPayInstId.getText().trim());
                PaymentType pt = (PaymentType) cmbPayType.getSelectedItem();
                String ref = txtRefNo.getText().trim();
                String notes = txtNotes.getText().trim();
                double amt = Double.parseDouble(txtAmount.getText().trim());

                Payment payment;
                if (pt == PaymentType.REGISTRATION) {
                    payment = paymentService.captureRegistrationFee(instId, ref, notes);
                } else if (pt == PaymentType.INSTALLATION) {
                    payment = paymentService.captureInstallationFee(instId, amt, ref, notes);
                } else if (pt == PaymentType.RECONNECTION) {
                    payment = paymentService.captureReconnectionFee(instId, ref, notes);
                } else {
                    List<Bill> bills = billingService.getBillsForInstitution(instId);
                    if (bills.isEmpty()) throw new IllegalStateException("No open bill found for this institution.");
                    payment = paymentService.captureMonthlyPayment(bills.get(0).getId(), amt, ref, notes);
                }

                JOptionPane.showMessageDialog(this, "Payment Captured Successfully!\nReference: " + payment.getReferenceNo() +
                        "\nAmount: KSh " + String.format("%,.2f", payment.getAmount()), "Receipt Generated", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Payment Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(formBody, BorderLayout.CENTER);
        view.add(formCard, BorderLayout.WEST);

        // Right Table Card
        JPanel tableCard = UITheme.createCard("Payment Transactions Audit Trail", "Historical payment entries reconciled into institution accounts");
        paymentsModel = new DefaultTableModel(new String[]{"ID", "Inst ID", "Type", "Amount (KSh)", "Date", "Reference", "Notes"}, 0);
        paymentsTable = new JTable(paymentsModel);
        UITheme.styleTable(paymentsTable);

        JScrollPane scroll = new JScrollPane(paymentsTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        tableCard.add(scroll, BorderLayout.CENTER);

        view.add(tableCard, BorderLayout.CENTER);
        return view;
    }

    // =========================================================================
    // VIEW 5: Billing & Overdue Evaluation
    // =========================================================================
    private JPanel createBillingView() {
        JPanel view = new JPanel(new BorderLayout(12, 12));
        view.setOpaque(false);

        // Top Control Card
        JPanel topCard = UITheme.createCard("Billing & Deadline Lifecycle Operations", "Generate monthly invoices and evaluate automatic overdue surcharges (15%) & disconnections");
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        topRow.setOpaque(false);

        JLabel lblInst = new JLabel("Target Inst ID:");
        lblInst.setFont(UITheme.FONT_BOLD);
        JTextField txtBillInstId = new JTextField(6);
        UITheme.styleTextField(txtBillInstId);

        JButton btnGenerateBill = UITheme.createPrimaryButton("Generate Monthly Bill");
        JButton btnEval = UITheme.createButton("Evaluate Deadlines (Overdue / Disconnect)", UITheme.WARNING_AMBER, new Color(217, 119, 6));
        JButton btnReconnect = UITheme.createOutlineButton("Surcharge Reconnection (+KSh 1,000)");

        topRow.add(lblInst);
        topRow.add(txtBillInstId);
        topRow.add(btnGenerateBill);
        topRow.add(btnEval);
        topRow.add(btnReconnect);

        btnGenerateBill.addActionListener(e -> {
            try {
                int instId = Integer.parseInt(txtBillInstId.getText().trim());
                LocalDate now = LocalDate.now();
                Bill bill = billingService.generateMonthlyBill(instId, now.getMonthValue(), now.getYear());
                JOptionPane.showMessageDialog(this, "Bill Generated:\n" + bill, "Bill Created", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEval.addActionListener(e -> {
            try {
                billingService.evaluateBillsStatus(LocalDate.now());
                JOptionPane.showMessageDialog(this, "Evaluated billing statuses against deadlines.\nOverdue accounts surcharged 15% fine.\nUnpaid past 10th flagged for disconnection.", "Lifecycle Evaluated", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReconnect.addActionListener(e -> {
            int row = billsTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Please select a bill from the table first.", "Select Bill", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int billId = (Integer) billsModel.getValueAt(row, 0);
            try {
                billingService.applyReconnection(billId);
                JOptionPane.showMessageDialog(this, "Reconnection surcharge fee of KSh 1,000 applied to Bill #" + billId, "Reconnection Surcharged", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        topCard.add(topRow, BorderLayout.CENTER);
        view.add(topCard, BorderLayout.NORTH);

        // Center Table Card
        JPanel tableCard = UITheme.createCard("Monthly Billing Ledger", "Invoice records, fine surcharges, reconnection fees, and current status");
        billsModel = new DefaultTableModel(new String[]{"Bill ID", "Inst ID", "Month/Year", "Base Charge", "15% Overdue Fine", "Reconnect Fee", "Total Due", "Paid", "Due Date", "Disconn Date", "Status"}, 0);
        billsTable = new JTable(billsModel);
        UITheme.styleTable(billsTable);

        JScrollPane scroll = new JScrollPane(billsTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        tableCard.add(scroll, BorderLayout.CENTER);

        view.add(tableCard, BorderLayout.CENTER);
        return view;
    }

    // =========================================================================
    // VIEW 6: Analytics & Reports Viewer
    // =========================================================================
    private JPanel createReportsView() {
        JPanel view = new JPanel(new BorderLayout(12, 12));
        view.setOpaque(false);

        // Top Control Card
        JPanel topCard = UITheme.createCard("Executive Analytics & Report Center", "Instant financial breakdown, defaulters roster, and service revenue aggregates");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnRow.setOpaque(false);

        JButton btnRptInst = UITheme.createOutlineButton("Registered Institutions");
        JButton btnRptDef = UITheme.createOutlineButton("Defaulters Roster (15% Fine)");
        JButton btnRptDisc = UITheme.createOutlineButton("Disconnection Notice");
        JButton btnRptInfra = UITheme.createOutlineButton("Infrastructure Details");
        JButton btnRptMaster = UITheme.createPrimaryButton("★ Master Financial Computation");
        JButton btnExport = UITheme.createSuccessButton("Export Report (.txt)");
        JButton btnPrint = UITheme.createOutlineButton("Print");
        JButton btnClear = UITheme.createOutlineButton("Clear");

        btnRow.add(btnRptInst);
        btnRow.add(btnRptDef);
        btnRow.add(btnRptDisc);
        btnRow.add(btnRptInfra);
        btnRow.add(btnRptMaster);
        btnRow.add(btnExport);
        btnRow.add(btnPrint);
        btnRow.add(btnClear);

        topCard.add(btnRow, BorderLayout.CENTER);
        view.add(topCard, BorderLayout.NORTH);

        // Center Output Card
        JPanel viewerCard = UITheme.createCard("Report Document Viewer", "Formatted plain-text document ready for review or export");

        reportsTextArea = new JTextArea();
        reportsTextArea.setFont(UITheme.FONT_MONO);
        reportsTextArea.setForeground(UITheme.TEXT_PRIMARY);
        reportsTextArea.setBackground(new Color(250, 250, 252));
        reportsTextArea.setEditable(false);
        reportsTextArea.setMargin(new Insets(14, 16, 14, 16));

        JScrollPane scroll = new JScrollPane(reportsTextArea);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR, 1, true));
        viewerCard.add(scroll, BorderLayout.CENTER);

        // Wire Actions
        btnRptInst.addActionListener(e -> {
            try { reportsTextArea.setText(reportService.generateRegisteredInstitutionsReport()); }
            catch (Exception ex) { reportsTextArea.setText("Error: " + ex.getMessage()); }
        });
        btnRptDef.addActionListener(e -> {
            try { reportsTextArea.setText(reportService.generateDefaultersReport()); }
            catch (Exception ex) { reportsTextArea.setText("Error: " + ex.getMessage()); }
        });
        btnRptDisc.addActionListener(e -> {
            try { reportsTextArea.setText(reportService.generateDisconnectionReport()); }
            catch (Exception ex) { reportsTextArea.setText("Error: " + ex.getMessage()); }
        });
        btnRptInfra.addActionListener(e -> {
            try { reportsTextArea.setText(reportService.generateInfrastructureReport()); }
            catch (Exception ex) { reportsTextArea.setText("Error: " + ex.getMessage()); }
        });
        btnRptMaster.addActionListener(e -> {
            try { reportsTextArea.setText(reportService.generateMasterComputationReport()); }
            catch (Exception ex) { reportsTextArea.setText("Error: " + ex.getMessage()); }
        });

        btnExport.addActionListener(e -> {
            String text = reportsTextArea.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please generate a report first before exporting.", "Empty Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("Azani_ISP_Executive_Report.txt"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (java.io.PrintWriter pw = new java.io.PrintWriter(chooser.getSelectedFile())) {
                    pw.write(text);
                    JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + chooser.getSelectedFile().getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnPrint.addActionListener(e -> {
            try {
                if (reportsTextArea.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please generate a report first before printing.", "Empty Report", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean printed = reportsTextArea.print();
                if (printed) {
                    JOptionPane.showMessageDialog(this, "Report printed successfully.", "Print Completed", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Printing error: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnClear.addActionListener(e -> reportsTextArea.setText(""));

        view.add(viewerCard, BorderLayout.CENTER);
        return view;
    }

    // =========================================================================
    // Form Helper
    // =========================================================================
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy++;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    // =========================================================================
    // Data Refresh & KPI Metrics Synchronization
    // =========================================================================
    private void refreshAllData() {
        try {
            // 1. Refresh Institutions
            institutionsModel.setRowCount(0);
            List<Institution> insts = institutionService.getAllInstitutions();
            for (Institution i : insts) {
                ContactPerson cp = i.getContactPerson();
                institutionsModel.addRow(new Object[]{
                        i.getId(), i.getName(), i.getCategory().getDisplayName(),
                        cp != null ? cp.getFullName() : "",
                        cp != null ? cp.getPhone() : "",
                        cp != null ? cp.getEmail() : "",
                        i.getStatus()
                });
            }
            if (statInstitutionsVal != null) {
                statInstitutionsVal.setText(String.valueOf(insts.size()));
            }

            // 2. Refresh Infrastructure
            infraModel.setRowCount(0);
            List<Infrastructure> infras = infrastructureService.getAllInfrastructure();
            for (Infrastructure inf : infras) {
                infraModel.addRow(new Object[]{
                        inf.getInstitutionId(), inf.getNumUsers(), inf.isReady() ? "YES" : "NO",
                        inf.getPcsPurchased(), String.format("%,.2f", inf.getPcCost()),
                        inf.getLanNodes(), String.format("%,.2f", inf.getLanCost()),
                        String.format("%,.2f", inf.getTotalInstallationCost())
                });
            }

            // 3. Refresh Subscriptions
            subsModel.setRowCount(0);
            List<Subscription> subs = billingService.getAllSubscriptions();
            int activeSubCount = 0;
            for (Subscription s : subs) {
                if (s.isActive()) activeSubCount++;
                subsModel.addRow(new Object[]{
                        s.getInstitutionId(), s.getBandwidthPackage().getLabel(),
                        String.format("%,.2f", s.getBaseMonthlyCost()),
                        s.isUpgraded() ? "YES" : "NO",
                        String.format("%.1f%%", s.getDiscountPercent()),
                        String.format("%,.2f", s.getFinalMonthlyCost()),
                        s.isActive() ? "YES" : "NO"
                });
            }
            if (statActiveSubsVal != null) {
                statActiveSubsVal.setText(String.valueOf(activeSubCount));
            }

            // 4. Refresh Payments
            paymentsModel.setRowCount(0);
            List<Payment> payments = paymentService.getAllPayments();
            double totalCaptured = 0.0;
            for (Payment p : payments) {
                totalCaptured += p.getAmount();
                paymentsModel.addRow(new Object[]{
                        p.getId(), p.getInstitutionId(), p.getPaymentType().getDescription(),
                        String.format("%,.2f", p.getAmount()), p.getPaymentDate(),
                        p.getReferenceNo(), p.getNotes()
                });
            }
            if (statTotalRevenueVal != null) {
                statTotalRevenueVal.setText(String.format("KSh %,.2f", totalCaptured));
            }

            // 5. Refresh Bills & Defaulters
            billsModel.setRowCount(0);
            List<Bill> bills = billingService.getAllBills();
            int defaulterCount = 0;
            for (Bill b : bills) {
                if ("OVERDUE".equalsIgnoreCase(b.getStatus()) || "DISCONNECTED".equalsIgnoreCase(b.getStatus())) {
                    defaulterCount++;
                }
                billsModel.addRow(new Object[]{
                        b.getId(), b.getInstitutionId(), b.getBillingMonth() + "/" + b.getBillingYear(),
                        String.format("%,.2f", b.getBaseCharge()),
                        String.format("%,.2f", b.getOverdueFine()),
                        String.format("%,.2f", b.getReconnectionFee()),
                        String.format("%,.2f", b.getTotalDue()),
                        String.format("%,.2f", b.getAmountPaid()),
                        b.getDueDate(), b.getDisconnectionDate(), b.getStatus()
                });
            }
            if (statDefaultersVal != null) {
                statDefaultersVal.setText(String.valueOf(defaulterCount));
            }

        } catch (Exception e) {
            System.err.println("Error refreshing tables: " + e.getMessage());
        }
    }

    // =========================================================================
    // Database Config Dialog (Intact Non-destructive Database configuration)
    // =========================================================================
    private void showDatabaseConfigDialog() {
        JDialog dialog = new JDialog(this, "Database Configuration & Cloud Sync", true);
        dialog.setSize(540, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(UITheme.APP_BG);

        JPanel card = UITheme.createCard("Connection Parameters", "Select engine and configure credentials. Data remains completely intact.");

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] types = {"Supabase (Cloud PostgreSQL)", "MySQL (Local Server)", "Embedded H2 (Offline In-Memory)"};
        JComboBox<String> cmbType = new JComboBox<>(types);
        UITheme.styleComboBox(cmbType);

        String currentType = DatabaseConnection.getActiveDbType();
        if ("supabase".equalsIgnoreCase(currentType) || "postgresql".equalsIgnoreCase(currentType)) {
            cmbType.setSelectedIndex(0);
        } else if ("mysql".equalsIgnoreCase(currentType)) {
            cmbType.setSelectedIndex(1);
        } else {
            cmbType.setSelectedIndex(2);
        }

        java.util.Properties cfg = DatabaseConnection.getConfig();
        JTextField txtHost = new JTextField(cfg.getProperty("db.host", "db.YOUR_PROJECT_REF.supabase.co"), 20);
        JTextField txtPort = new JTextField(cfg.getProperty("db.port", "5432"), 20);
        JTextField txtDbName = new JTextField(cfg.getProperty("db.name", "postgres"), 20);
        JTextField txtUser = new JTextField(cfg.getProperty("db.user", "postgres"), 20);
        JPasswordField txtPass = new JPasswordField(cfg.getProperty("db.password", ""), 20);
        JTextField txtCustomUrl = new JTextField(cfg.getProperty("db.url", ""), 20);

        for (JTextField f : new JTextField[]{txtHost, txtPort, txtDbName, txtUser, txtPass, txtCustomUrl}) {
            UITheme.styleTextField(f);
        }

        cmbType.addActionListener(e -> {
            int sel = cmbType.getSelectedIndex();
            if (sel == 0) { // Supabase
                if (txtHost.getText().contains("localhost")) txtHost.setText("db.YOUR_PROJECT_REF.supabase.co");
                txtPort.setText("5432");
                txtDbName.setText("postgres");
                txtUser.setText("postgres");
            } else if (sel == 1) { // MySQL
                txtHost.setText("localhost");
                txtPort.setText("3306");
                txtDbName.setText("azani_isp_db");
                txtUser.setText("root");
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Database Type:"), gbc);
        gbc.gridx = 1; form.add(cmbType, gbc);

        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Host:"), gbc);
        gbc.gridx = 1; form.add(txtHost, gbc);

        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Port:"), gbc);
        gbc.gridx = 1; form.add(txtPort, gbc);

        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Database Name:"), gbc);
        gbc.gridx = 1; form.add(txtDbName, gbc);

        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; form.add(txtUser, gbc);

        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; form.add(txtPass, gbc);

        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Direct JDBC URL:"), gbc);
        gbc.gridx = 1; form.add(txtCustomUrl, gbc);

        card.add(form, BorderLayout.CENTER);
        dialog.add(card, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setOpaque(false);
        JButton btnConnect = UITheme.createPrimaryButton("Save & Connect");
        JButton btnCancel = UITheme.createOutlineButton("Cancel");

        btnConnect.addActionListener(e -> {
            try {
                int sel = cmbType.getSelectedIndex();
                String typeStr = (sel == 0) ? "supabase" : (sel == 1) ? "mysql" : "h2";
                String host = txtHost.getText().trim();
                String port = txtPort.getText().trim();
                String dbName = txtDbName.getText().trim();
                String user = txtUser.getText().trim();
                String pass = new String(txtPass.getPassword());
                String url = txtCustomUrl.getText().trim();

                DatabaseConnection.configureAndSaveCredentials(typeStr, host, port, dbName, user, pass, url);
                refreshAllData();
                JOptionPane.showMessageDialog(dialog, "Connected successfully to " + DatabaseConnection.getActiveDbType().toUpperCase() + "!\nSchema verified and ready.", "Connection Successful", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Connection Failed:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(btnCancel);
        btnPanel.add(btnConnect);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
