package com.expenso.ui;

import com.expenso.model.UpiPayment;
import com.expenso.util.QRCodeGenerator;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.net.URI;

/**
 * Dialog for UPI Payment operations
 */
public class UpiPaymentDialog extends JDialog {
    private static final Color PRIMARY = new Color(20, 184, 166);
    private static final Color SECONDARY = new Color(249, 115, 22);
    private static final Color BG_DARK = new Color(17, 24, 39);
    private static final Color BG_CARD = new Color(31, 41, 55);
    private static final Color BG_SECONDARY = new Color(55, 65, 81);
    private static final Color TEXT_PRIMARY = new Color(249, 250, 251);
    private static final Color TEXT_SECONDARY = new Color(209, 213, 219);
    
    private JTextField upiIdField;
    private JTextField nameField;
    private JTextField amountField;
    private JTextField noteField;
    private UpiPayment payment;
    private boolean paymentInitiated = false;
    
    public UpiPaymentDialog(Frame parent) {
        super(parent, "💳 UPI Payment", true);
        initializeUI();
    }
    
    private void initializeUI() {
        setSize(500, 650);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        
        JLabel titleLabel = new JLabel("💳 UPI Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel subtitleLabel = new JLabel("Send money via UPI");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BG_CARD);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BG_SECONDARY),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // UPI ID Field
        formPanel.add(createLabel("UPI ID / Phone Number"));
        upiIdField = createTextField("example@upi or 9876543210");
        formPanel.add(upiIdField);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Name Field
        formPanel.add(createLabel("Recipient Name"));
        nameField = createTextField("Enter name");
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Amount Field
        formPanel.add(createLabel("Amount (₹)"));
        amountField = createTextField("0.00");
        formPanel.add(amountField);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Note Field
        formPanel.add(createLabel("Note (Optional)"));
        noteField = createTextField("Payment note");
        formPanel.add(noteField);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(20, 184, 166, 30));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        JLabel infoLabel = new JLabel("ℹ️ This will generate a UPI payment link");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(TEXT_SECONDARY);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(infoLabel);
        
        JLabel infoLabel2 = new JLabel("The link will open in your UPI app for payment");
        infoLabel2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel2.setForeground(TEXT_SECONDARY);
        infoLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(infoLabel2);
        
        formPanel.add(infoPanel);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBackground(BG_DARK);
        
        JButton generateButton = createStyledButton("Generate QR", PRIMARY);
        generateButton.addActionListener(e -> generateQRCode());
        
        JButton payButton = createStyledButton("Pay Now", SECONDARY);
        payButton.addActionListener(e -> initiatePayment());
        
        JButton copyButton = createStyledButton("Copy Link", BG_SECONDARY);
        copyButton.addActionListener(e -> copyUpiLink());
        
        JButton cancelButton = createStyledButton("Cancel", BG_SECONDARY);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(generateButton);
        buttonPanel.add(payButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JTextField createTextField(String placeholder) {
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
    
    private boolean validateInput() {
        if (upiIdField.getText().trim().isEmpty()) {
            showError("Please enter UPI ID or Phone Number");
            return false;
        }
        
        if (nameField.getText().trim().isEmpty()) {
            showError("Please enter recipient name");
            return false;
        }
        
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                showError("Please enter a valid amount");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount");
            return false;
        }
        
        return true;
    }
    
    private UpiPayment createPayment() {
        String upiId = upiIdField.getText().trim();
        
        // Format phone number to UPI ID if numeric
        if (upiId.matches("\\d+")) {
            upiId = upiId + "@paytm"; // Default UPI handle
        }
        
        return new UpiPayment(
            upiId,
            nameField.getText().trim(),
            Double.parseDouble(amountField.getText().trim()),
            noteField.getText().trim()
        );
    }
    
    private void generateQRCode() {
        if (!validateInput()) return;
        
        payment = createPayment();
        String upiLink = payment.generateUpiLink();
        
        // Create QR Code dialog
        JDialog qrDialog = new JDialog(this, "UPI QR Code", true);
        qrDialog.setSize(450, 550);
        qrDialog.setLocationRelativeTo(this);
        
        JPanel qrPanel = new JPanel(new BorderLayout(0, 20));
        qrPanel.setBackground(BG_DARK);
        qrPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("📱 Scan to Pay", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        qrPanel.add(titleLabel, BorderLayout.NORTH);
        
        // QR Code Image
        BufferedImage qrImage = QRCodeGenerator.generateQRImage(upiLink, 300);
        JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
        qrLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
        
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.setBackground(BG_DARK);
        imagePanel.add(qrLabel);
        qrPanel.add(imagePanel, BorderLayout.CENTER);
        
        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(BG_CARD);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel amountLabel = new JLabel("Amount: " + payment.getFormattedAmount());
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        amountLabel.setForeground(PRIMARY);
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel nameLabel = new JLabel("To: " + payment.getPayeeName());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(TEXT_SECONDARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        infoPanel.add(amountLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(nameLabel);
        
        qrPanel.add(infoPanel, BorderLayout.SOUTH);
        
        qrDialog.setContentPane(qrPanel);
        qrDialog.setVisible(true);
    }
    
    private void initiatePayment() {
        if (!validateInput()) return;
        
        payment = createPayment();
        String upiLink = payment.generateUpiLink();
        
        // Try to open UPI link
        try {
            // For Windows, try to open the UPI link in browser
            // The browser may prompt to open in a UPI app
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                
                // Create a choice dialog
                Object[] options = {"Open UPI Link", "Copy Link", "Show QR Code", "Cancel"};
                int choice = JOptionPane.showOptionDialog(this,
                    "How would you like to pay?\n\n" +
                    "Amount: " + payment.getFormattedAmount() + "\n" +
                    "To: " + payment.getPayeeName() + "\n" +
                    "UPI ID: " + payment.getPayeeUpiId(),
                    "Payment Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                if (choice == 0) {
                    // Try to open UPI link
                    try {
                        desktop.browse(new URI("https://pay.google.com/pay?link=" + java.net.URLEncoder.encode(upiLink, "UTF-8")));
                        paymentInitiated = true;
                        showSuccess("Opening payment in browser...\nComplete the payment in your UPI app.");
                        dispose();
                    } catch (Exception ex) {
                        // Fallback: copy link
                        copyToClipboard(upiLink);
                        showInfo("Link copied to clipboard!\nPaste it in your UPI app to pay.");
                    }
                } else if (choice == 1) {
                    copyToClipboard(upiLink);
                    showSuccess("UPI link copied to clipboard!");
                } else if (choice == 2) {
                    generateQRCode();
                }
            }
        } catch (Exception ex) {
            showError("Error opening payment: " + ex.getMessage());
        }
    }
    
    private void copyUpiLink() {
        if (!validateInput()) return;
        
        payment = createPayment();
        String upiLink = payment.generateUpiLink();
        
        copyToClipboard(upiLink);
        showSuccess("UPI link copied to clipboard!\n\nPaste in any UPI app to pay.");
    }
    
    private void copyToClipboard(String text) {
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public UpiPayment getPayment() {
        return payment;
    }
    
    public boolean isPaymentInitiated() {
        return paymentInitiated;
    }
}
