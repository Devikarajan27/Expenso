package com.expenso.ui;

import com.expenso.model.Transaction;
import com.expenso.model.Expense;
import com.expenso.util.BankStatementParser;
import com.expenso.util.EmailTransactionParser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Dialog for importing transactions from various sources
 */
public class ImportTransactionsDialog extends JDialog {
    private static final Color PRIMARY = new Color(20, 184, 166);
    private static final Color SECONDARY = new Color(249, 115, 22);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color BG_DARK = new Color(17, 24, 39);
    private static final Color BG_CARD = new Color(31, 41, 55);
    private static final Color BG_SECONDARY = new Color(55, 65, 81);
    private static final Color TEXT_PRIMARY = new Color(249, 250, 251);
    private static final Color TEXT_SECONDARY = new Color(209, 213, 219);
    
    private JTabbedPane tabbedPane;
    private DefaultTableModel previewTableModel;
    private JTable previewTable;
    private List<Transaction> parsedTransactions;
    private List<Expense> selectedExpenses;
    private JLabel statusLabel;
    
    public ImportTransactionsDialog(Frame parent) {
        super(parent, "📥 Import Transactions", true);
        parsedTransactions = new ArrayList<>();
        selectedExpenses = new ArrayList<>();
        initializeUI();
    }
    
    private void initializeUI() {
        setSize(900, 700);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        
        JLabel titleLabel = new JLabel("📥 Import Transactions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel subtitleLabel = new JLabel("Automatically import transactions from bank statements or emails");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(BG_CARD);
        tabbedPane.setForeground(TEXT_PRIMARY);
        
        // Tab 1: Bank Statement Import
        tabbedPane.addTab("📄 Bank Statement", createBankStatementPanel());
        
        // Tab 2: Email Import
        tabbedPane.addTab("📧 Email Import", createEmailImportPanel());
        
        // Tab 3: Preview & Confirm
        tabbedPane.addTab("👁️ Preview", createPreviewPanel());
        tabbedPane.setEnabledAt(2, false); // Disabled until data is imported
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Status Bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BG_CARD);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BG_SECONDARY),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        statusLabel = new JLabel("Ready to import transactions");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        JButton closeButton = createStyledButton("Close", BG_SECONDARY);
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());
        statusPanel.add(closeButton, BorderLayout.EAST);
        
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createBankStatementPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(20, 184, 166, 30));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel infoTitle = new JLabel("📄 How to Import Bank Statements:");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoTitle.setForeground(TEXT_PRIMARY);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(10));
        
        String[] instructions = {
            "1. Download your bank statement as CSV from net banking",
            "2. Supported banks: SBI, HDFC, ICICI, Axis, Kotak, and more",
            "3. Click 'Select File' below and choose your CSV file",
            "4. Review transactions in the preview tab",
            "5. Click 'Import' to add to your expenses"
        };
        
        for (String instruction : instructions) {
            JLabel label = new JLabel(instruction);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(TEXT_SECONDARY);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(label);
            infoPanel.add(Box.createVerticalStrut(5));
        }
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // File Selection Panel
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 50));
        filePanel.setBackground(BG_CARD);
        
        JButton selectFileButton = createStyledButton("📁 Select Bank Statement (CSV)", PRIMARY);
        selectFileButton.setPreferredSize(new Dimension(300, 50));
        selectFileButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectFileButton.addActionListener(e -> selectBankStatement());
        
        filePanel.add(selectFileButton);
        
        panel.add(filePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEmailImportPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(249, 115, 22, 30));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel infoTitle = new JLabel("📧 How to Import from Email:");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoTitle.setForeground(TEXT_PRIMARY);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(10));
        
        String[] instructions = {
            "1. Open your email app (Gmail, Outlook, etc.)",
            "2. Search for transaction notifications from your bank/UPI app",
            "3. Copy the email content (subject + body)",
            "4. Paste it in the text area below",
            "5. Click 'Parse Email' to extract transaction",
            "6. Review and import to expenses"
        };
        
        for (String instruction : instructions) {
            JLabel label = new JLabel(instruction);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(TEXT_SECONDARY);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            infoPanel.add(label);
            infoPanel.add(Box.createVerticalStrut(5));
        }
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Email Input Panel
        JPanel inputPanel = new JPanel(new BorderLayout(0, 10));
        inputPanel.setBackground(BG_CARD);
        
        JLabel inputLabel = new JLabel("Paste Email Content:");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        inputLabel.setForeground(TEXT_SECONDARY);
        inputPanel.add(inputLabel, BorderLayout.NORTH);
        
        JTextArea emailTextArea = new JTextArea();
        emailTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        emailTextArea.setBackground(BG_SECONDARY);
        emailTextArea.setForeground(TEXT_PRIMARY);
        emailTextArea.setCaretColor(TEXT_PRIMARY);
        emailTextArea.setLineWrap(true);
        emailTextArea.setWrapStyleWord(true);
        emailTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(emailTextArea);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        inputPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel emailButtonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        emailButtonPanel.setBackground(BG_CARD);
        
        JButton parseButton = createStyledButton("🔍 Parse Email", SECONDARY);
        parseButton.addActionListener(e -> parseEmail(emailTextArea.getText()));
        emailButtonPanel.add(parseButton);
        
        JButton autoFetchButton = createStyledButton("🤖 Auto-Fetch from Gmail", PRIMARY);
        autoFetchButton.addActionListener(e -> autoFetchFromGmail());
        emailButtonPanel.add(autoFetchButton);
        
        inputPanel.add(emailButtonPanel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Review Transactions Before Import");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(TEXT_PRIMARY);
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Select", "Date", "Description", "Amount", "Type", "Category", "Source"};
        previewTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Only checkbox is editable
            }
        };
        
        previewTable = new JTable(previewTableModel);
        styleTable(previewTable);
        
        JScrollPane scrollPane = new JScrollPane(previewTable);
        scrollPane.getViewport().setBackground(BG_CARD);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BG_CARD);
        
        JButton selectAllButton = createStyledButton("Select All", BG_SECONDARY);
        selectAllButton.addActionListener(e -> selectAllTransactions(true));
        buttonPanel.add(selectAllButton);
        
        JButton deselectAllButton = createStyledButton("Deselect All", BG_SECONDARY);
        deselectAllButton.addActionListener(e -> selectAllTransactions(false));
        buttonPanel.add(deselectAllButton);
        
        JButton importButton = createStyledButton("✅ Import Selected", SUCCESS);
        importButton.setPreferredSize(new Dimension(150, 40));
        importButton.addActionListener(e -> importSelectedTransactions());
        buttonPanel.add(importButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void selectBankStatement() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || BankStatementParser.isSupportedFile(f);
            }
            public String getDescription() {
                return "CSV Files (*.csv, *.txt)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            parseBankStatement(selectedFile);
        }
    }
    
    private void parseBankStatement(File file) {
        try {
            statusLabel.setText("Parsing bank statement...");
            statusLabel.setForeground(PRIMARY);
            
            parsedTransactions = BankStatementParser.parseCSV(file);
            
            if (parsedTransactions.isEmpty()) {
                showError("No transactions found in the file.\nPlease check the file format.");
                statusLabel.setText("No transactions found");
                return;
            }
            
            updatePreviewTable();
            tabbedPane.setEnabledAt(2, true);
            tabbedPane.setSelectedIndex(2);
            
            statusLabel.setText(String.format("Loaded %d transactions from %s", 
                parsedTransactions.size(), file.getName()));
            statusLabel.setForeground(SUCCESS);
            
        } catch (Exception e) {
            showError("Error parsing bank statement:\n" + e.getMessage());
            statusLabel.setText("Error parsing file");
            statusLabel.setForeground(Color.RED);
        }
    }
    
    private void autoFetchFromGmail() {
        // TODO: Gmail functionality temporarily disabled due to missing dependencies
        showError("Gmail auto-fetch is currently not available.\nPlease use manual import methods.");
        /*
        GmailSetupDialog gmailDialog = new GmailSetupDialog((Frame) getParent());
        gmailDialog.setVisible(true);
        
        // Get fetched transactions
        java.util.List<Transaction> transactions = gmailDialog.getFetchedTransactions();
        if (transactions != null && !transactions.isEmpty()) {
            parsedTransactions.addAll(transactions);
            updatePreviewTable();
            tabbedPane.setEnabledAt(2, true);
            tabbedPane.setSelectedIndex(2);
            
            statusLabel.setText(String.format("Auto-fetched %d transactions from Gmail!", transactions.size()));
            statusLabel.setForeground(SUCCESS);
        }
        */
    }
    
    private void parseEmail(String emailContent) {
        if (emailContent.trim().isEmpty()) {
            showError("Please paste email content first");
            return;
        }
        
        try {
            statusLabel.setText("Parsing email transaction...");
            statusLabel.setForeground(PRIMARY);
            
            // Extract subject from first line if present
            String[] lines = emailContent.split("\n", 2);
            String subject = lines[0];
            String content = lines.length > 1 ? lines[1] : emailContent;
            
            List<Transaction> transactions = EmailTransactionParser.parseEmailContent(content, subject);
            
            if (transactions.isEmpty()) {
                showError("No transaction found in email.\nMake sure it's a transaction notification email.");
                statusLabel.setText("No transaction detected");
                return;
            }
            
            parsedTransactions.addAll(transactions);
            updatePreviewTable();
            tabbedPane.setEnabledAt(2, true);
            tabbedPane.setSelectedIndex(2);
            
            statusLabel.setText(String.format("Parsed %d transaction from email", transactions.size()));
            statusLabel.setForeground(SUCCESS);
            
        } catch (Exception e) {
            showError("Error parsing email:\n" + e.getMessage());
            statusLabel.setText("Error parsing email");
        }
    }
    
    private void updatePreviewTable() {
        previewTableModel.setRowCount(0);
        
        for (Transaction txn : parsedTransactions) {
            Expense expense = txn.toExpense();
            if (expense != null) {
                previewTableModel.addRow(new Object[]{
                    true, // Selected by default
                    txn.getFormattedDate(),
                    txn.getDescription(),
                    txn.getFormattedAmount(),
                    txn.getType().getDisplayName(),
                    expense.getCategory().getDisplayName(),
                    txn.getSource()
                });
            }
        }
    }
    
    private void selectAllTransactions(boolean select) {
        for (int i = 0; i < previewTableModel.getRowCount(); i++) {
            previewTableModel.setValueAt(select, i, 0);
        }
    }
    
    private void importSelectedTransactions() {
        selectedExpenses.clear();
        
        for (int i = 0; i < previewTableModel.getRowCount(); i++) {
            Boolean selected = (Boolean) previewTableModel.getValueAt(i, 0);
            if (selected != null && selected) {
                Transaction txn = parsedTransactions.get(i);
                Expense expense = txn.toExpense();
                if (expense != null) {
                    selectedExpenses.add(expense);
                }
            }
        }
        
        if (selectedExpenses.isEmpty()) {
            showError("Please select at least one transaction to import");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Import %d selected transactions?", selectedExpenses.size()),
            "Confirm Import",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            showSuccess(String.format("Successfully imported %d transactions!", selectedExpenses.size()));
            dispose();
        }
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(35);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BG_SECONDARY);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(BG_SECONDARY);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public List<Expense> getImportedExpenses() {
        return selectedExpenses;
    }
}
