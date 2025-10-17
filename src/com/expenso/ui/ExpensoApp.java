package com.expenso.ui;

import com.expenso.data.DataManager;
import com.expenso.model.Expense;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main application window for Expenso Finance Manager
 */
public class ExpensoApp extends JFrame {
    // Dark Modern Elegant Theme
    private static final Color PRIMARY = new Color(99, 102, 241);        // Elegant Indigo
    private static final Color SECONDARY = new Color(107, 114, 128);     // Sophisticated Gray
    private static final Color SUCCESS = new Color(16, 185, 129);        // Modern Teal
    private static final Color DANGER = new Color(239, 68, 68);          // Modern Red
    private static final Color WARNING = new Color(245, 158, 11);        // Amber Gold
    private static final Color BG_DARK = new Color(17, 24, 39);          // Deep Charcoal
    private static final Color BG_CARD = new Color(31, 41, 55);          // Dark Card
    private static final Color BG_SECONDARY = new Color(55, 65, 81);     // Medium Dark
    private static final Color TEXT_PRIMARY = new Color(249, 250, 251);  // Pure White Text
    private static final Color TEXT_SECONDARY = new Color(156, 163, 175);// Soft Gray Text
    
    /**
     * Custom rounded border class for modern UI components
     */
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        private final int thickness;
        
        public RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + thickness/2, y + thickness/2, 
                width - thickness, height - thickness, radius, radius));
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }
    
    /**
     * Custom rounded panel with background painting
     */
    private static class RoundedPanel extends JPanel {
        private final int radius;
        private Color backgroundColor;
        
        public RoundedPanel(int radius, Color backgroundColor) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
        
        @Override
        public void setBackground(Color bg) {
            this.backgroundColor = bg;
            super.setBackground(bg);
        }
    }
    
    // Utility methods for creating rounded borders
    private static RoundedBorder createRoundedBorder(int radius, Color color, int thickness) {
        return new RoundedBorder(radius, color, thickness);
    }
    
    private static RoundedPanel createRoundedPanel(int radius, Color backgroundColor) {
        return new RoundedPanel(radius, backgroundColor);
    }
    
    /**
     * Custom rounded button with modern styling
     */
    private static class RoundedButton extends JButton {
        private final int radius;
        private Color backgroundColor;
        private Color hoverColor;
        
        public RoundedButton(String text, int radius, Color backgroundColor) {
            super(text);
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            this.hoverColor = backgroundColor.darker();
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color bgColor = getModel().isRollover() ? hoverColor : backgroundColor;
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            
            g2.dispose();
            super.paintComponent(g);
        }
        
        public void setHoverColor(Color hoverColor) {
            this.hoverColor = hoverColor;
        }
    }
    
    private DataManager dataManager;
    
    // UI Components
    private JLabel totalBalanceLabel;
    private JLabel totalExpensesLabel;
    private JTextField nameField;
    private JTextField amountField;
    private JComboBox<Expense.Category> categoryCombo;
    private JTextField dateField;
    private JTextField budgetField;
    private JProgressBar budgetProgressBar;
    private JLabel budgetInfoLabel;
    private JPanel alertPanel;
    private DefaultTableModel tableModel;
    private JTable expenseTable;
    private JComboBox<String> filterCombo;
    private ChartPanel chartPanel;
    
    public ExpensoApp() {
        dataManager = new DataManager();
        initializeUI();
        updateUI();
    }
    
    private void initializeUI() {
        setTitle("💰 Expenso - Finance Management");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set global ComboBox styling for black backgrounds
            UIManager.put("ComboBox.background", BG_SECONDARY);
            UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
            UIManager.put("ComboBox.selectionBackground", PRIMARY);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            UIManager.put("ComboBox.disabledBackground", BG_SECONDARY);
            UIManager.put("ComboBox.disabledForeground", TEXT_SECONDARY);
            
            // Additional dropdown list styling
            UIManager.put("List.background", Color.BLACK);
            UIManager.put("List.foreground", Color.WHITE);
            UIManager.put("List.selectionBackground", PRIMARY);
            UIManager.put("List.selectionForeground", Color.WHITE);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Main panel with dark background
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        
        // Alert Panel
        alertPanel = new JPanel();
        alertPanel.setLayout(new BoxLayout(alertPanel, BoxLayout.Y_AXIS));
        alertPanel.setBackground(BG_DARK);
        mainPanel.add(alertPanel, BorderLayout.CENTER);
        
        // Content Panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(BG_DARK);
        
        // Left Panel - Add Expense and Budget
        contentPanel.add(createLeftPanel());
        
        // Right Panel - Expenses List and Charts
        contentPanel.add(createRightPanel());
        
        // Add content panel to center
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(BG_DARK);
        centerWrapper.add(alertPanel, BorderLayout.NORTH);
        centerWrapper.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createHeader() {
        RoundedPanel header = createRoundedPanel(15, BG_CARD);
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Logo and Import Button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(BG_CARD);
        
        // Create enhanced logo with icon and text
        JPanel logoPanel = createEnhancedLogo();
        leftPanel.add(logoPanel);
        
        // Import Transactions Button
        JButton importButton = new JButton("📥 Import Transactions");
        importButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        importButton.setForeground(Color.WHITE);
        importButton.setBackground(PRIMARY);
        importButton.setFocusPainted(false);
        importButton.setBorderPainted(false);
        importButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        importButton.setPreferredSize(new Dimension(200, 40));
        importButton.addActionListener(e -> openImportDialog());
        importButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                importButton.setBackground(PRIMARY.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                importButton.setBackground(PRIMARY);
            }
        });
        leftPanel.add(importButton);
        
        header.add(leftPanel, BorderLayout.WEST);
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setBackground(BG_CARD);
        
        // Total Balance
        JPanel balanceCard = createStatCard("TOTAL BALANCE", "₹0.00", SUCCESS);
        totalBalanceLabel = (JLabel) ((JPanel) balanceCard.getComponent(1)).getComponent(0);
        statsPanel.add(balanceCard);
        
        // Total Expenses
        JPanel expenseCard = createStatCard("TOTAL EXPENSES", "₹0.00", DANGER);
        totalExpensesLabel = (JLabel) ((JPanel) expenseCard.getComponent(1)).getComponent(0);
        statsPanel.add(expenseCard);
        
        header.add(statsPanel, BorderLayout.EAST);
        
        return header;
    }
    
    /**
     * Creates an enhanced logo with styled icon and text
     */
    private JPanel createEnhancedLogo() {
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoPanel.setBackground(BG_CARD);
        logoPanel.setOpaque(false);
        
        // Create logo icon container with rounded background
        RoundedPanel iconContainer = createRoundedPanel(25, PRIMARY);
        iconContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconContainer.setPreferredSize(new Dimension(50, 50));
        iconContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Logo icon - using a combination of symbols for finance theme
        JLabel iconLabel = new JLabel("₹");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconContainer.add(iconLabel);
        
        // Application name with gradient effect simulation
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(BG_CARD);
        
        JLabel titleLabel = new JLabel("Expenso");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY);
        
        JLabel subtitleLabel = new JLabel("Finance Manager");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        logoPanel.add(iconContainer);
        logoPanel.add(textPanel);
        
        return logoPanel;
    }
    
    private JPanel createStatCard(String label, String value, Color valueColor) {
        RoundedPanel card = createRoundedPanel(12, BG_SECONDARY);
        card.setLayout(new BorderLayout(0, 5));
        card.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        labelComp.setForeground(TEXT_SECONDARY);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setBackground(BG_SECONDARY);
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueComp.setForeground(valueColor);
        valuePanel.add(valueComp);
        
        card.add(labelComp, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_DARK);
        
        // Add Expense Panel
        JPanel addExpensePanel = createStyledPanel("Add Expense");
        addExpensePanel.setLayout(new BoxLayout(addExpensePanel, BoxLayout.Y_AXIS));
        
        // Form fields
        addExpensePanel.add(createFormField("Description:", nameField = createTextField()));
        addExpensePanel.add(Box.createVerticalStrut(15));
        
        JPanel amountCategoryPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        amountCategoryPanel.setBackground(BG_CARD);
        amountCategoryPanel.add(createFormField("Amount (₹):", amountField = createTextField()));
        
        categoryCombo = new JComboBox<>(Expense.Category.values());
        styleComboBox(categoryCombo);
        amountCategoryPanel.add(createFormField("Category:", categoryCombo));
        addExpensePanel.add(amountCategoryPanel);
        addExpensePanel.add(Box.createVerticalStrut(15));
        
        dateField = createTextField();
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        addExpensePanel.add(createFormField("Date (dd/MM/yyyy):", dateField));
        addExpensePanel.add(Box.createVerticalStrut(20));
        
        JButton addButton = createStyledButton("ADD EXPENSE", PRIMARY);
        addButton.addActionListener(e -> addExpense());
        addExpensePanel.add(addButton);
        
        // Budget Panel
        addExpensePanel.add(Box.createVerticalStrut(30));
        addExpensePanel.add(createSeparator());
        addExpensePanel.add(Box.createVerticalStrut(20));
        
        JLabel budgetTitle = new JLabel("Budget Settings");
        budgetTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        budgetTitle.setForeground(TEXT_SECONDARY);
        budgetTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        addExpensePanel.add(budgetTitle);
        addExpensePanel.add(Box.createVerticalStrut(15));
        
        budgetField = createTextField();
        budgetField.setText(String.valueOf(dataManager.getBudget()));
        addExpensePanel.add(createFormField("Monthly Budget (₹):", budgetField));
        addExpensePanel.add(Box.createVerticalStrut(15));
        
        JButton setBudgetButton = createStyledButton("SET BUDGET", SECONDARY);
        setBudgetButton.addActionListener(e -> setBudget());
        addExpensePanel.add(setBudgetButton);
        addExpensePanel.add(Box.createVerticalStrut(15));
        
        // Budget Info
        RoundedPanel budgetInfoPanel = createRoundedPanel(10, BG_SECONDARY);
        budgetInfoPanel.setLayout(new BoxLayout(budgetInfoPanel, BoxLayout.Y_AXIS));
        budgetInfoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        budgetInfoLabel = new JLabel("Set your monthly budget to track spending");
        budgetInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        budgetInfoLabel.setForeground(TEXT_SECONDARY);
        budgetInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        budgetInfoPanel.add(budgetInfoLabel);
        budgetInfoPanel.add(Box.createVerticalStrut(10));
        
        budgetProgressBar = new JProgressBar(0, 100);
        budgetProgressBar.setStringPainted(true);
        budgetProgressBar.setPreferredSize(new Dimension(0, 25));
        budgetProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        budgetProgressBar.setForeground(SUCCESS);
        budgetProgressBar.setBackground(BG_SECONDARY);
        budgetProgressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        budgetInfoPanel.add(budgetProgressBar);
        
        addExpensePanel.add(budgetInfoPanel);
        
        leftPanel.add(addExpensePanel, BorderLayout.NORTH);
        
        return leftPanel;
    }
    
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setBackground(BG_DARK);
        
        // Expenses List Panel
        JPanel expensesPanel = createStyledPanel("Recent Expenses");
        expensesPanel.setLayout(new BorderLayout(0, 10));
        
        // Filter and controls
        JPanel controlPanel = new JPanel(new BorderLayout(10, 0));
        controlPanel.setBackground(BG_CARD);
        
        filterCombo = new JComboBox<>(new String[]{"All Categories", "Food", "Transport", 
            "Shopping", "Entertainment", "Bills", "Healthcare", "Education", "Other"});
        styleComboBox(filterCombo);
        filterCombo.addActionListener(e -> filterExpenses());
        controlPanel.add(filterCombo, BorderLayout.CENTER);
        
        JButton clearButton = createStyledButton("CLEAR ALL", DANGER);
        clearButton.setPreferredSize(new Dimension(120, 35));
        clearButton.addActionListener(e -> clearAllExpenses());
        controlPanel.add(clearButton, BorderLayout.EAST);
        
        expensesPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Description", "Amount", "Category", "Date", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only delete button is editable
            }
        };
        
        expenseTable = new JTable(tableModel);
        styleTable(expenseTable);
        
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setBorder(createRoundedBorder(10, new Color(75, 85, 99), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(true);
        expensesPanel.add(scrollPane, BorderLayout.CENTER);
        
        rightPanel.add(expensesPanel, BorderLayout.NORTH);
        
        // Charts Panel
        chartPanel = new ChartPanel(dataManager);
        rightPanel.add(chartPanel, BorderLayout.CENTER);
        
        return rightPanel;
    }
    
    private JPanel createStyledPanel(String title) {
        RoundedPanel panel = createRoundedPanel(15, BG_CARD);
        panel.setLayout(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createFormField(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(BG_CARD);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelComp.setForeground(TEXT_SECONDARY);
        panel.add(labelComp, BorderLayout.NORTH);
        panel.add(component, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(BG_SECONDARY);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(8, new Color(75, 85, 99), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setOpaque(false);
        return field;
    }
    
    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(BG_SECONDARY);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createCompoundBorder(
            createRoundedBorder(8, new Color(75, 85, 99), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        // Enhanced styling for better visibility
        combo.setOpaque(true);
        
        // Custom renderer for dropdown items with black background
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                // Set black background for the dropdown list
                list.setBackground(Color.BLACK);
                list.setSelectionBackground(PRIMARY);
                list.setSelectionForeground(Color.WHITE);
                
                if (isSelected) {
                    // Selected item styling
                    c.setBackground(PRIMARY);
                    c.setForeground(Color.WHITE);
                } else {
                    // Normal item styling - black background with white text
                    c.setBackground(Color.BLACK);
                    c.setForeground(Color.WHITE);
                }
                
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                setOpaque(true);
                
                return c;
            }
        });
        
        // Style the dropdown popup with black background
        try {
            Object comp = combo.getUI().getAccessibleChild(combo, 0);
            if (comp instanceof JPopupMenu) {
                JPopupMenu popup = (JPopupMenu) comp;
                popup.setBackground(Color.BLACK);
                popup.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            }
        } catch (Exception e) {
            // Ignore if unable to access popup
        }
        
        // Additional styling for better black background support
        combo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                // Ensure dropdown list has black background when opened
                for (int i = 0; i < combo.getComponentCount(); i++) {
                    Component comp = combo.getComponent(i);
                    if (comp instanceof JList) {
                        JList<?> list = (JList<?>) comp;
                        list.setBackground(Color.BLACK);
                        list.setSelectionBackground(PRIMARY);
                        list.setSelectionForeground(Color.WHITE);
                    }
                }
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            
            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
    }
    
    private JButton createStyledButton(String text, Color color) {
        RoundedButton button = new RoundedButton(text, 12, color);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(0, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHoverColor(color.darker());
        
        return button;
    }
    
    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BG_SECONDARY);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BG_SECONDARY);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(BG_SECONDARY);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        
        // Add delete button renderer
        table.getColumn("Action").setCellRenderer((tbl, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton("Delete");
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setForeground(Color.WHITE);
            btn.setBackground(DANGER);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            return btn;
        });
        
        table.getColumn("Action").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                JButton btn = new JButton("Delete");
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                btn.setForeground(Color.WHITE);
                btn.setBackground(DANGER);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.addActionListener(e -> deleteExpense(row));
                return btn;
            }
        });
    }
    
    // Action Methods
    private void addExpense() {
        try {
            String name = nameField.getText().trim();
            double amount = Double.parseDouble(amountField.getText().trim());
            Expense.Category category = (Expense.Category) categoryCombo.getSelectedItem();
            LocalDate date = LocalDate.parse(dateField.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            if (name.isEmpty()) {
                showError("Please enter a description");
                return;
            }
            
            Expense expense = new Expense(name, amount, category, date);
            dataManager.addExpense(expense);
            
            // Clear form
            nameField.setText("");
            amountField.setText("");
            dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            categoryCombo.setSelectedIndex(0);
            
            updateUI();
            showSuccess("Expense added successfully!");
            
        } catch (NumberFormatException e) {
            showError("Please enter a valid amount");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }
    
    private void deleteExpense(int row) {
        List<Expense> expenses = dataManager.getExpenses();
        if (row >= 0 && row < expenses.size()) {
            long id = expenses.get(row).getId();
            dataManager.deleteExpense(id);
            updateUI();
        }
    }
    
    private void setBudget() {
        try {
            double budget = Double.parseDouble(budgetField.getText().trim());
            if (budget <= 0) {
                showError("Please enter a valid budget amount");
                return;
            }
            dataManager.setBudget(budget);
            updateUI();
            showSuccess("Budget set successfully!");
        } catch (NumberFormatException e) {
            showError("Please enter a valid budget amount");
        }
    }
    
    private void filterExpenses() {
        String selected = (String) filterCombo.getSelectedItem();
        updateExpenseTable(selected);
    }
    
    private void clearAllExpenses() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to clear all expenses?",
            "Confirm Clear All",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dataManager.clearAllExpenses();
            updateUI();
        }
    }
    
    // UI Update Methods
    private void updateUI() {
        updateStats();
        updateExpenseTable("All Categories");
        updateBudgetInfo();
        updateAlerts();
        chartPanel.updateCharts();
    }
    
    private void updateStats() {
        double totalExpenses = dataManager.getTotalExpenses();
        double balance = dataManager.getBudget() - totalExpenses;
        
        totalExpensesLabel.setText(String.format("₹%.2f", totalExpenses));
        totalBalanceLabel.setText(String.format("₹%.2f", balance));
        
        if (balance < 0) {
            totalBalanceLabel.setForeground(DANGER);
        } else {
            totalBalanceLabel.setForeground(SUCCESS);
        }
    }
    
    private void updateExpenseTable(String filter) {
        tableModel.setRowCount(0);
        
        List<Expense> expenses = dataManager.getExpenses();
        if (!filter.equals("All Categories")) {
            expenses = expenses.stream()
                .filter(e -> e.getCategory().getDisplayName().contains(filter))
                .collect(Collectors.toList());
        }
        
        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{
                expense.getName(),
                expense.getFormattedAmount(),
                expense.getCategory().getDisplayName(),
                expense.getFormattedDate(),
                "Delete"
            });
        }
    }
    
    private void updateBudgetInfo() {
        double budget = dataManager.getBudget();
        if (budget > 0) {
            double currentMonthExpenses = dataManager.getCurrentMonthExpenses();
            double percentage = (currentMonthExpenses / budget) * 100;
            double remaining = budget - currentMonthExpenses;
            
            budgetProgressBar.setValue((int) Math.min(percentage, 100));
            budgetProgressBar.setString(String.format("%.1f%%", percentage));
            
            if (percentage >= 100) {
                budgetProgressBar.setForeground(DANGER);
            } else if (percentage >= 80) {
                budgetProgressBar.setForeground(WARNING);
            } else {
                budgetProgressBar.setForeground(SUCCESS);
            }
            
            budgetInfoLabel.setText(String.format(
                "<html>Spent: ₹%.2f | Budget: ₹%.2f<br>Remaining: ₹%.2f</html>",
                currentMonthExpenses, budget, remaining
            ));
        }
    }
    
    private void updateAlerts() {
        alertPanel.removeAll();
        
        double budget = dataManager.getBudget();
        if (budget <= 0) {
            alertPanel.revalidate();
            alertPanel.repaint();
            return;
        }
        
        double currentMonthExpenses = dataManager.getCurrentMonthExpenses();
        double percentage = (currentMonthExpenses / budget) * 100;
        
        if (percentage >= 100) {
            alertPanel.add(createAlert(DANGER, "🚨 Budget Exceeded!",
                String.format("You've exceeded your monthly budget by ₹%.2f", 
                    currentMonthExpenses - budget)));
        } else if (percentage >= 90) {
            alertPanel.add(createAlert(DANGER, "⚠️ Critical Budget Alert!",
                String.format("You've used %.1f%% of your monthly budget. Only ₹%.2f remaining.",
                    percentage, budget - currentMonthExpenses)));
        } else if (percentage >= 75) {
            alertPanel.add(createAlert(WARNING, "⚡ Budget Warning",
                String.format("You've used %.1f%% of your monthly budget. ₹%.2f remaining.",
                    percentage, budget - currentMonthExpenses)));
        }
        
        alertPanel.revalidate();
        alertPanel.repaint();
    }
    
    private JPanel createAlert(Color color, String title, String message) {
        RoundedPanel alert = createRoundedPanel(10, new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        alert.setLayout(new BorderLayout(10, 0));
        alert.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, color),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        alert.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(0, 0, 0, 0));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        contentPanel.add(titleLabel);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(TEXT_SECONDARY);
        contentPanel.add(messageLabel);
        
        alert.add(contentPanel, BorderLayout.CENTER);
        
        return alert;
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void openImportDialog() {
        ImportTransactionsDialog dialog = new ImportTransactionsDialog(this);
        dialog.setVisible(true);
        
        // Get imported expenses
        java.util.List<Expense> importedExpenses = dialog.getImportedExpenses();
        if (!importedExpenses.isEmpty()) {
            // Add all imported expenses
            for (Expense expense : importedExpenses) {
                dataManager.addExpense(expense);
            }
            updateUI();
            showSuccess(String.format("Successfully imported %d transactions!", importedExpenses.size()));
        }
    }
}
