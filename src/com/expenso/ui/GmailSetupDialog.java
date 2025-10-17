package com.expenso.ui;

import com.expenso.util.GmailConnector;
import com.expenso.model.Transaction;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Dialog for Gmail setup and automatic email fetching
 */
public class GmailSetupDialog extends JDialog {
    private static final Color PRIMARY = new Color(20, 184, 166);
    private static final Color SECONDARY = new Color(249, 115, 22);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER = new Color(244, 63, 94);
    private static final Color BG_DARK = new Color(17, 24, 39);
    private static final Color BG_CARD = new Color(31, 41, 55);
    private static final Color BG_SECONDARY = new Color(55, 65, 81);
    private static final Color TEXT_PRIMARY = new Color(249, 250, 251);
    private static final Color TEXT_SECONDARY = new Color(209, 213, 219);
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private JSpinner daysSpinner;
    private Preferences prefs;
    private List<Transaction> fetchedTransactions;
    private GmailConnector connector;
    
    public GmailSetupDialog(Frame parent) {
        super(parent, "📧 Gmail Auto-Import Setup", true);
        prefs = Preferences.userNodeForPackage(GmailSetupDialog.class);
        initializeUI();
        loadSavedCredentials();
    }
    
    private void initializeUI() {
        setSize(650, 750);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        
        JLabel titleLabel = new JLabel("📧 Gmail Auto-Import");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel subtitleLabel = new JLabel("Automatically fetch and import transaction emails");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_CARD);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BG_SECONDARY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Instructions
        JPanel instructionsPanel = createInstructionsPanel();
        contentPanel.add(instructionsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Email field
        contentPanel.add(createLabel("Gmail Address:"));
        emailField = createTextField();
        emailField.setText(prefs.get("gmail_email", ""));
        contentPanel.add(emailField);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // App Password field
        contentPanel.add(createLabel("Gmail App Password:"));
        passwordField = new JPasswordField();
        stylePasswordField(passwordField);
        contentPanel.add(passwordField);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Help link
        JLabel helpLabel = new JLabel("<html><u>How to generate App Password?</u></html>");
        helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        helpLabel.setForeground(PRIMARY);
        helpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        helpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showAppPasswordHelp();
            }
        });
        contentPanel.add(helpLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Days selector
        JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        daysPanel.setBackground(BG_CARD);
        daysPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        daysPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel daysLabel = createLabel("Fetch emails from last:");
        daysPanel.add(daysLabel);
        daysPanel.add(Box.createHorizontalStrut(10));
        
        daysSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 90, 1));
        daysSpinner.setPreferredSize(new Dimension(70, 35));
        daysSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) daysSpinner.getEditor()).getTextField().setBackground(BG_SECONDARY);
        ((JSpinner.DefaultEditor) daysSpinner.getEditor()).getTextField().setForeground(TEXT_PRIMARY);
        daysPanel.add(daysSpinner);
        daysPanel.add(Box.createHorizontalStrut(10));
        
        JLabel daysLabel2 = createLabel("days");
        daysPanel.add(daysLabel2);
        
        contentPanel.add(daysPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBackground(BG_CARD);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JButton testButton = createStyledButton("🔍 Test Connection", SECONDARY);
        testButton.addActionListener(e -> testConnection());
        buttonPanel.add(testButton);
        
        JButton fetchButton = createStyledButton("📥 Fetch Emails", PRIMARY);
        fetchButton.addActionListener(e -> fetchEmails());
        buttonPanel.add(fetchButton);
        
        JButton saveButton = createStyledButton("💾 Save Settings", SUCCESS);
        saveButton.addActionListener(e -> saveSettings());
        buttonPanel.add(saveButton);
        
        JButton cancelButton = createStyledButton("Cancel", BG_SECONDARY);
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setForeground(TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusLabel);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Security note
        JPanel notePanel = new JPanel();
        notePanel.setLayout(new BoxLayout(notePanel, BoxLayout.Y_AXIS));
        notePanel.setBackground(new Color(249, 115, 22, 20));
        notePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel noteTitle = new JLabel("🔒 Privacy & Security:");
        noteTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        noteTitle.setForeground(TEXT_PRIMARY);
        noteTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        notePanel.add(noteTitle);
        
        String[] notes = {
            "• Credentials stored securely on your computer",
            "• App passwords are safer than regular passwords",
            "• Only transaction emails are accessed",
            "• No data sent to external servers"
        };
        
        for (String note : notes) {
            JLabel label = new JLabel(note);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            label.setForeground(TEXT_SECONDARY);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            notePanel.add(label);
        }
        
        mainPanel.add(notePanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createInstructionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 184, 166, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel title = new JLabel("📋 Setup Instructions:");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        
        String[] steps = {
            "1. Open Gmail → Settings → See all settings",
            "2. Go to 'Forwarding and POP/IMAP' tab",
            "3. Enable 'IMAP access'",
            "4. Go to myaccount.google.com/apppasswords",
            "5. Generate an 'App Password' for 'Mail'",
            "6. Enter your Gmail and App Password below",
            "7. Click 'Fetch Emails' to import transactions"
        };
        
        for (String step : steps) {
            JLabel label = new JLabel(step);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            label.setForeground(TEXT_SECONDARY);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
        }
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(BG_SECONDARY);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BG_SECONDARY),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }
    
    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(BG_SECONDARY);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BG_SECONDARY),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
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
    
    private void testConnection() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("❌ Please enter email and app password");
            statusLabel.setForeground(DANGER);
            return;
        }
        
        statusLabel.setText("🔄 Testing connection...");
        statusLabel.setForeground(PRIMARY);
        
        // Test in background thread
        new Thread(() -> {
            connector = new GmailConnector(email, password);
            boolean connected = connector.testConnection();
            
            SwingUtilities.invokeLater(() -> {
                if (connected) {
                    statusLabel.setText("✅ Connection successful!");
                    statusLabel.setForeground(SUCCESS);
                } else {
                    statusLabel.setText("❌ Connection failed. Check credentials and IMAP access.");
                    statusLabel.setForeground(DANGER);
                }
            });
        }).start();
    }
    
    private void fetchEmails() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        int days = (Integer) daysSpinner.getValue();
        
        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("❌ Please enter email and app password");
            statusLabel.setForeground(DANGER);
            return;
        }
        
        statusLabel.setText("🔄 Fetching transaction emails...");
        statusLabel.setForeground(PRIMARY);
        
        // Fetch in background thread
        new Thread(() -> {
            try {
                connector = new GmailConnector(email, password);
                fetchedTransactions = connector.fetchTransactionEmails(days);
                connector.disconnect();
                
                SwingUtilities.invokeLater(() -> {
                    if (fetchedTransactions != null && !fetchedTransactions.isEmpty()) {
                        statusLabel.setText(String.format("✅ Found %d transactions!", fetchedTransactions.size()));
                        statusLabel.setForeground(SUCCESS);
                        dispose(); // Close dialog and return to import preview
                    } else {
                        statusLabel.setText("⚠️ No transaction emails found in the last " + days + " days");
                        statusLabel.setForeground(DANGER);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("❌ Error: " + e.getMessage());
                    statusLabel.setForeground(DANGER);
                });
            }
        }).start();
    }
    
    private void saveSettings() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            statusLabel.setText("❌ Please enter email address");
            statusLabel.setForeground(DANGER);
            return;
        }
        
        prefs.put("gmail_email", email);
        // Note: We don't save password for security reasons
        
        statusLabel.setText("✅ Settings saved!");
        statusLabel.setForeground(SUCCESS);
    }
    
    private void loadSavedCredentials() {
        String savedEmail = prefs.get("gmail_email", "");
        if (!savedEmail.isEmpty()) {
            emailField.setText(savedEmail);
        }
    }
    
    private void showAppPasswordHelp() {
        String message = "How to Generate Gmail App Password:\n\n" +
                        "1. Go to: myaccount.google.com/apppasswords\n" +
                        "2. Sign in to your Google Account\n" +
                        "3. Select 'Mail' and 'Windows Computer'\n" +
                        "4. Click 'Generate'\n" +
                        "5. Copy the 16-character password\n" +
                        "6. Paste it in the App Password field\n\n" +
                        "Note: 2-Step Verification must be enabled.";
        
        JOptionPane.showMessageDialog(this, message, 
            "App Password Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public List<Transaction> getFetchedTransactions() {
        return fetchedTransactions;
    }
}
