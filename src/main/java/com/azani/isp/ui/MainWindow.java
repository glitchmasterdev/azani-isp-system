package com.azani.isp.ui;

import com.azani.isp.dao.DatabaseConnection;
import com.azani.isp.model.*;
import com.azani.isp.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Modern Java Swing Desktop GUI for Azani ISP Information System.
 */
public class MainWindow extends JFrame {
    private final InstitutionService institutionService;
    private final InfrastructureService infrastructureService;
    private final BillingService billingService;
    private final PaymentService paymentService;
    private final ComputationService computationService;
    private final ReportService reportService;

    // Swing Components
    private JTabbedPane tabbedPane;
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
        super("Azani Internet Service Provider Information System - SCO200");
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
        setSize(1180, 750);
        setLocationRelativeTo(null);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 43, 73));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel("AZANI INTERNET SERVICE PROVIDER - INFORMATION SYSTEM");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerButtons.setOpaque(false);

        JLabel dbStatusLabel = new JLabel("DB: " + DatabaseConnection.getActiveDbType().toUpperCase());
        dbStatusLabel.setForeground(new Color(175, 220, 255));
        dbStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerButtons.add(dbStatusLabel);

        JButton dbConfigBtn = new JButton("Configure Database (Supabase / MySQL)");
        dbConfigBtn.setFocusPainted(false);
        dbConfigBtn.addActionListener(e -> showDatabaseConfigDialog(dbStatusLabel));
        headerButtons.add(dbConfigBtn);

        JButton seedBtn = new JButton("Load Sample Data");
        seedBtn.setFocusPainted(false);
        seedBtn.addActionListener(e -> {
            DatabaseConnection.seedSampleData();
            refreshAllData();
            JOptionPane.showMessageDialog(this, "Sample records loaded successfully.", "Seed Data", JOptionPane.INFORMATION_MESSAGE);
        });
        headerButtons.add(seedBtn);

        headerPanel.add(headerButtons, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabbedPane.addTab("1. Institution Registration", createRegistrationPanel());
        tabbedPane.addTab("2. Infrastructure & Hardware", createInfrastructurePanel());
        tabbedPane.addTab("3. Bandwidth & Upgrades", createSubscriptionPanel());
        tabbedPane.addTab("4. Payment Capture", createPaymentPanel());
        tabbedPane.addTab("5. Billing & Defaulters", createBillingPanel());
        tabbedPane.addTab("6. Computations & Reports", createReportsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // --- Tab 1: Registration ---
    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form on West
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Register New Institution"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
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

        // Ensure all text fields have visible black text on white background
        for (JTextField f : new JTextField[]{txtName, txtAddress, txtContactName, txtDesignation, txtPhone, txtEmail, txtNationalId}) {
            f.setForeground(Color.BLACK);
            f.setBackground(Color.WHITE);
            f.setCaretColor(Color.BLACK);
        }

        form.add(new JLabel("Institution Name:"), gbc); gbc.gridx = 1; form.add(txtName, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Category:"), gbc); gbc.gridx = 1; form.add(cmbCategory, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Physical Address:"), gbc); gbc.gridx = 1; form.add(txtAddress, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JSeparator(), gbc); gbc.gridx = 1; form.add(new JLabel("--- Contact Person Details ---"), gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Contact Full Name:"), gbc); gbc.gridx = 1; form.add(txtContactName, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Designation:"), gbc); gbc.gridx = 1; form.add(txtDesignation, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Phone Number:"), gbc); gbc.gridx = 1; form.add(txtPhone, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Email Address:"), gbc); gbc.gridx = 1; form.add(txtEmail, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("National/Staff ID:"), gbc); gbc.gridx = 1; form.add(txtNationalId, gbc);


        JButton btnRegister = new JButton("Register Institution");
        btnRegister.setForeground(Color.BLACK);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        form.add(btnRegister, gbc);

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

        panel.add(form, BorderLayout.WEST);

        // Table on Center
        institutionsModel = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Contact Person", "Phone", "Email", "Status"}, 0);
        institutionsTable = new JTable(institutionsModel);
        panel.add(new JScrollPane(institutionsTable), BorderLayout.CENTER);

        return panel;
    }

    // --- Tab 2: Infrastructure & Site Assessment ---
    private JPanel createInfrastructurePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Site Readiness & Infrastructure Assessment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField txtInstId = new JTextField(12);
        JTextField txtUsers = new JTextField("50", 12);
        JCheckBox chkReady = new JCheckBox("Site Already Ready for Connectivity");
        JTextField txtPcs = new JTextField("0", 12);
        JTextField txtLanNodes = new JTextField("0", 12);
        JLabel lblCostPreview = new JLabel("Est. Total Installation: KSh 10,000.00");
        lblCostPreview.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Ensure visible black text on white background
        for (JTextField f : new JTextField[]{txtInstId, txtUsers, txtPcs, txtLanNodes}) {
            f.setForeground(Color.BLACK);
            f.setBackground(Color.WHITE);
            f.setCaretColor(Color.BLACK);
        }

        form.add(new JLabel("Institution ID:"), gbc); gbc.gridx = 1; form.add(txtInstId, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Number of Users:"), gbc); gbc.gridx = 1; form.add(txtUsers, gbc);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; form.add(chkReady, gbc); gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Personal Computers (KSh 40,000):"), gbc); gbc.gridx = 1; form.add(txtPcs, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("LAN Nodes (Table 2):"), gbc); gbc.gridx = 1; form.add(txtLanNodes, gbc);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; form.add(lblCostPreview, gbc);

        JButton btnSaveInfra = new JButton("Save Assessment & Calculate Costs");
        gbc.gridy++; form.add(btnSaveInfra, gbc);

        btnSaveInfra.addActionListener(e -> {
            try {
                int instId = Integer.parseInt(txtInstId.getText().trim());
                int users = Integer.parseInt(txtUsers.getText().trim());
                boolean ready = chkReady.isSelected();
                int pcs = Integer.parseInt(txtPcs.getText().trim());
                int nodes = Integer.parseInt(txtLanNodes.getText().trim());

                Infrastructure inf = infrastructureService.assessInfrastructure(instId, users, ready, pcs, nodes);
                lblCostPreview.setText(String.format("Est. Total Installation: KSh %,.2f", inf.getTotalInstallationCost()));
                JOptionPane.showMessageDialog(this, "Assessment saved!\nPC Cost: KSh " + String.format("%,.2f", inf.getPcCost()) +
                        "\nLAN Cost: KSh " + String.format("%,.2f", inf.getLanCost()) +
                        "\nTotal Installation: KSh " + String.format("%,.2f", inf.getTotalInstallationCost()), "Assessment Saved", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(form, BorderLayout.WEST);

        infraModel = new DefaultTableModel(new String[]{"Inst ID", "Users", "Ready?", "PCs Purchased", "PC Cost", "LAN Nodes", "LAN Cost", "Total Install Fee"}, 0);
        infraTable = new JTable(infraModel);
        panel.add(new JScrollPane(infraTable), BorderLayout.CENTER);

        return panel;
    }

    // --- Tab 3: Subscriptions & Upgrades ---
    private JPanel createSubscriptionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Bandwidth Subscription & Upgrades"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JTextField txtSubInstId = new JTextField(14);
        JComboBox<BandwidthPackage> cmbPackage = new JComboBox<>(BandwidthPackage.values());
        JCheckBox chkUpgrade = new JCheckBox("Upgrade to Higher Bandwidth (10% Discount Offered)");
        chkUpgrade.setForeground(new Color(0, 100, 0));

        txtSubInstId.setForeground(Color.BLACK);
        txtSubInstId.setBackground(Color.WHITE);
        txtSubInstId.setCaretColor(Color.BLACK);

        form.add(new JLabel("Institution ID:"), gbc); gbc.gridx = 1; form.add(txtSubInstId, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Bandwidth Package:"), gbc); gbc.gridx = 1; form.add(cmbPackage, gbc);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; form.add(chkUpgrade, gbc);

        JButton btnApplySub = new JButton("Apply Subscription / Upgrade");
        gbc.gridy++; form.add(btnApplySub, gbc);

        btnApplySub.addActionListener(e -> {
            try {
                int instId = Integer.parseInt(txtSubInstId.getText().trim());
                BandwidthPackage pkg = (BandwidthPackage) cmbPackage.getSelectedItem();
                boolean isUpgrade = chkUpgrade.isSelected();

                Subscription sub;
                if (isUpgrade) {
                    sub = billingService.upgradeSubscription(instId, pkg);
                    JOptionPane.showMessageDialog(this, "Upgrade applied with 10% discount!\nNew Monthly Charge: KSh " +
                            String.format("%,.2f", sub.getFinalMonthlyCost()), "Upgrade Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    sub = billingService.subscribe(instId, pkg, LocalDate.now());
                    JOptionPane.showMessageDialog(this, "Subscribed successfully!\nMonthly Charge: KSh " +
                            String.format("%,.2f", sub.getFinalMonthlyCost()), "Subscribed", JOptionPane.INFORMATION_MESSAGE);
                }
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(form, BorderLayout.WEST);

        subsModel = new DefaultTableModel(new String[]{"Inst ID", "Bandwidth", "Base Monthly (KSh)", "Upgraded?", "Discount (%)", "Final Monthly (KSh)", "Active"}, 0);
        subsTable = new JTable(subsModel);
        panel.add(new JScrollPane(subsTable), BorderLayout.CENTER);

        return panel;
    }

    // --- Tab 4: Payment Capture ---
    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Capture Institution Payments"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        JComboBox<PaymentType> cmbPayType = new JComboBox<>(PaymentType.values());
        JTextField txtPayInstId = new JTextField(14);
        JTextField txtAmount = new JTextField(14);
        JTextField txtRefNo = new JTextField(14);
        JTextField txtNotes = new JTextField(14);

        // Ensure visible black text on white background
        for (JTextField f : new JTextField[]{txtPayInstId, txtAmount, txtRefNo, txtNotes}) {
            f.setForeground(Color.BLACK);
            f.setBackground(Color.WHITE);
            f.setCaretColor(Color.BLACK);
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

        form.add(new JLabel("Payment Type:"), gbc); gbc.gridx = 1; form.add(cmbPayType, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Institution ID:"), gbc); gbc.gridx = 1; form.add(txtPayInstId, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Amount (KSh):"), gbc); gbc.gridx = 1; form.add(txtAmount, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Transaction Ref:"), gbc); gbc.gridx = 1; form.add(txtRefNo, gbc);
        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Notes:"), gbc); gbc.gridx = 1; form.add(txtNotes, gbc);

        JButton btnCapturePay = new JButton("Capture Payment");
        btnCapturePay.setForeground(Color.BLACK);
        btnCapturePay.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; form.add(btnCapturePay, gbc);

        btnCapturePay.addActionListener(e -> {
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

                JOptionPane.showMessageDialog(this, "Payment Captured Successfully!\nReceipt: " + payment.getReferenceNo() +
                        "\nAmount: KSh " + String.format("%,.2f", payment.getAmount()), "Receipt", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Payment Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(form, BorderLayout.WEST);

        paymentsModel = new DefaultTableModel(new String[]{"ID", "Inst ID", "Type", "Amount (KSh)", "Date", "Reference", "Notes"}, 0);
        paymentsTable = new JTable(paymentsModel);
        panel.add(new JScrollPane(paymentsTable), BorderLayout.CENTER);

        return panel;
    }

    // --- Tab 5: Billing & Defaulters ---
    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        topControl.setBorder(BorderFactory.createTitledBorder("Billing Operations"));

        JTextField txtBillInstId = new JTextField(6);
        JButton btnGenerateBill = new JButton("Generate Bill for Institution");
        JButton btnEvalDeadlines = new JButton("Evaluate Billing Deadlines (Defaulters & Disconnections)");
        JButton btnReconnect = new JButton("Surcharge Reconnection (KSh 1,000)");

        topControl.add(new JLabel("Inst ID:"));
        topControl.add(txtBillInstId);
        topControl.add(btnGenerateBill);
        topControl.add(btnEvalDeadlines);
        topControl.add(btnReconnect);

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

        btnEvalDeadlines.addActionListener(e -> {
            try {
                billingService.evaluateBillsStatus(LocalDate.now());
                JOptionPane.showMessageDialog(this, "Evaluated billing statuses against deadlines.\nDefaulters and disconnected institutions updated.", "Evaluation Completed", JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Reconnection surcharge fee of KSh 1,000 applied to Bill #" + billId, "Reconnection Applied", JOptionPane.INFORMATION_MESSAGE);
                refreshAllData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(topControl, BorderLayout.NORTH);

        billsModel = new DefaultTableModel(new String[]{"Bill ID", "Inst ID", "Month/Year", "Base Charge", "15% Overdue Fine", "Reconnect Fee", "Total Due", "Paid", "Due Date", "Disconn Date", "Status"}, 0);
        billsTable = new JTable(billsModel);
        panel.add(new JScrollPane(billsTable), BorderLayout.CENTER);

        return panel;
    }

    // --- Tab 6: Computations & Reports Viewer ---
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttons = new JPanel(new GridLayout(2, 4, 8, 8));
        JButton btnRptInstitutions = new JButton("3(a) Registered Institutions");
        JButton btnRptDefaulters = new JButton("3(b) Defaulters (15% Fine)");
        JButton btnRptDisconnected = new JButton("3(c) Disconnection Issues");
        JButton btnRptInfra = new JButton("3(d) Infrastructure Details");
        JButton btnRptMaster = new JButton("4(a-e) Master Computations");
        JButton btnSaveToFile = new JButton("Export / Save Report...");
        JButton btnPrint = new JButton("Print Report");
        JButton btnClear = new JButton("Clear Viewer");

        buttons.add(btnRptInstitutions);
        buttons.add(btnRptDefaulters);
        buttons.add(btnRptDisconnected);
        buttons.add(btnRptInfra);
        buttons.add(btnRptMaster);
        buttons.add(btnSaveToFile);
        buttons.add(btnPrint);
        buttons.add(btnClear);

        panel.add(buttons, BorderLayout.NORTH);

        reportsTextArea = new JTextArea();
        reportsTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        reportsTextArea.setEditable(false);
        panel.add(new JScrollPane(reportsTextArea), BorderLayout.CENTER);

        btnRptInstitutions.addActionListener(e -> {
            try { reportsTextArea.setText(reportService.generateRegisteredInstitutionsReport()); }
            catch (Exception ex) { reportsTextArea.setText("Error: " + ex.getMessage()); }
        });
        btnRptDefaulters.addActionListener(e -> {
            try { reportsTextArea.setText(reportService.generateDefaultersReport()); }
            catch (Exception ex) { reportsTextArea.setText("Error: " + ex.getMessage()); }
        });
        btnRptDisconnected.addActionListener(e -> {
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
        btnSaveToFile.addActionListener(e -> {
            String text = reportsTextArea.getText().trim();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please generate a report first before exporting.", "Empty Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("Azani_ISP_Report.txt"));
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

        return panel;
    }

    private void refreshAllData() {
        try {
            // Refresh Institutions
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

            // Refresh Infrastructure
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

            // Refresh Subscriptions
            subsModel.setRowCount(0);
            List<Subscription> subs = billingService.getAllSubscriptions();
            for (Subscription s : subs) {
                subsModel.addRow(new Object[]{
                        s.getInstitutionId(), s.getBandwidthPackage().getLabel(),
                        String.format("%,.2f", s.getBaseMonthlyCost()),
                        s.isUpgraded() ? "YES" : "NO",
                        String.format("%.1f%%", s.getDiscountPercent()),
                        String.format("%,.2f", s.getFinalMonthlyCost()),
                        s.isActive() ? "YES" : "NO"
                });
            }

            // Refresh Payments
            paymentsModel.setRowCount(0);
            List<Payment> payments = paymentService.getAllPayments();
            for (Payment p : payments) {
                paymentsModel.addRow(new Object[]{
                        p.getId(), p.getInstitutionId(), p.getPaymentType().getDescription(),
                        String.format("%,.2f", p.getAmount()), p.getPaymentDate(),
                        p.getReferenceNo(), p.getNotes()
                });
            }

            // Refresh Bills
            billsModel.setRowCount(0);
            List<Bill> bills = billingService.getAllBills();
            for (Bill b : bills) {
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
        } catch (Exception e) {
            System.err.println("Error refreshing tables: " + e.getMessage());
        }
    }

    private void showDatabaseConfigDialog(JLabel dbStatusLabel) {
        JDialog dialog = new JDialog(this, "Database Configuration (Supabase / MySQL / H2)", true);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] types = {"Supabase (Cloud PostgreSQL)", "MySQL (Local Server)", "Embedded H2 (Offline In-Memory)"};
        JComboBox<String> cmbType = new JComboBox<>(types);

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

        gbc.gridx = 0; gbc.gridy++; form.add(new JLabel("Direct JDBC URL (optional):"), gbc);
        gbc.gridx = 1; form.add(txtCustomUrl, gbc);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnConnect = new JButton("Save & Connect");
        btnConnect.setForeground(Color.BLACK);
        btnConnect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JButton btnCancel = new JButton("Cancel");

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
                dbStatusLabel.setText("DB: " + DatabaseConnection.getActiveDbType().toUpperCase());
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
