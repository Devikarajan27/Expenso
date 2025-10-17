package com.expenso.ui;

import com.expenso.data.DataManager;
import com.expenso.model.Expense;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for displaying expense charts and statistics
 */
public class ChartPanel extends JPanel {
    // Dark Modern Elegant Theme (matching ExpensoApp)
    private static final Color PRIMARY = new Color(99, 102, 241);        // Elegant Indigo
    private static final Color BG_CARD = new Color(31, 41, 55);          // Dark Card
    private static final Color BG_SECONDARY = new Color(55, 65, 81);     // Medium Dark
    private static final Color TEXT_PRIMARY = new Color(249, 250, 251);  // Pure White Text
    private static final Color TEXT_SECONDARY = new Color(156, 163, 175);// Soft Gray Text
    
    private DataManager dataManager;
    private JPanel categoryStatsPanel;
    private JPanel chartDisplayPanel;
    private JButton categoryBtn;
    private JButton trendBtn;
    
    // Dark Modern Elegant Color Palette for Charts
    private static final Color[] CHART_COLORS = {
        new Color(99, 102, 241),    // Elegant Indigo
        new Color(16, 185, 129),    // Modern Teal
        new Color(139, 92, 246),    // Rich Purple
        new Color(59, 130, 246),    // Electric Blue
        new Color(245, 158, 11),    // Amber Gold
        new Color(239, 68, 68),     // Modern Red
        new Color(236, 72, 153),    // Vivid Pink
        new Color(107, 114, 128)    // Sophisticated Gray
    };
    
    public ChartPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(75, 85, 99), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_CARD);
        
        JLabel title = new JLabel("Expense Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);
        headerPanel.add(title, BorderLayout.WEST);
        
        // Chart tabs
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tabPanel.setBackground(BG_CARD);
        
        categoryBtn = createTabButton("By Category", true);
        trendBtn = createTabButton("Trend", false);
        
        categoryBtn.addActionListener(e -> showCategoryChart());
        trendBtn.addActionListener(e -> showTrendChart());
        
        tabPanel.add(categoryBtn);
        tabPanel.add(trendBtn);
        
        headerPanel.add(tabPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Chart display area
        chartDisplayPanel = new JPanel(new BorderLayout());
        chartDisplayPanel.setBackground(BG_SECONDARY);
        chartDisplayPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        chartDisplayPanel.setPreferredSize(new Dimension(0, 250));
        add(chartDisplayPanel, BorderLayout.CENTER);
        
        // Category breakdown
        categoryStatsPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        categoryStatsPanel.setBackground(BG_CARD);
        add(categoryStatsPanel, BorderLayout.SOUTH);
        
        updateCharts();
    }
    
    private JButton createTabButton(String text, boolean active) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (active) {
            button.setForeground(Color.WHITE);
            button.setBackground(PRIMARY);
        } else {
            button.setForeground(TEXT_SECONDARY);
            button.setBackground(BG_SECONDARY);
        }
        
        return button;
    }
    
    private void showCategoryChart() {
        categoryBtn.setForeground(Color.WHITE);
        categoryBtn.setBackground(PRIMARY);
        trendBtn.setForeground(TEXT_SECONDARY);
        trendBtn.setBackground(BG_SECONDARY);
        updateCharts();
    }
    
    private void showTrendChart() {
        trendBtn.setForeground(Color.WHITE);
        trendBtn.setBackground(PRIMARY);
        categoryBtn.setForeground(TEXT_SECONDARY);
        categoryBtn.setBackground(BG_SECONDARY);
        updateCharts();
    }
    
    public void updateCharts() {
        if (categoryBtn.getBackground().equals(PRIMARY)) {
            updateCategoryChart();
        } else {
            updateTrendChart();
        }
        updateCategoryBreakdown();
    }
    
    private void updateCategoryChart() {
        chartDisplayPanel.removeAll();
        
        // Calculate category totals
        Map<Expense.Category, Double> categoryTotals = new HashMap<>();
        for (Expense expense : dataManager.getExpenses()) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
        }
        
        if (categoryTotals.isEmpty()) {
            JLabel emptyLabel = new JLabel("No expense data to display", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(TEXT_SECONDARY);
            chartDisplayPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            chartDisplayPanel.add(new SimplePieChart(categoryTotals), BorderLayout.CENTER);
        }
        
        chartDisplayPanel.revalidate();
        chartDisplayPanel.repaint();
    }
    
    private void updateTrendChart() {
        chartDisplayPanel.removeAll();
        
        // Get last 7 days expenses
        Map<String, Double> dailyTotals = getLast7DaysExpenses();
        
        if (dailyTotals.isEmpty() || dailyTotals.values().stream().allMatch(v -> v == 0.0)) {
            JLabel emptyLabel = new JLabel("No expense data for the last 7 days", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(TEXT_SECONDARY);
            chartDisplayPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            chartDisplayPanel.add(new SimpleBarChart(dailyTotals), BorderLayout.CENTER);
        }
        
        chartDisplayPanel.revalidate();
        chartDisplayPanel.repaint();
    }
    
    private Map<String, Double> getLast7DaysExpenses() {
        Map<String, Double> dailyTotals = new LinkedHashMap<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            String dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"));
            dailyTotals.put(dateStr, 0.0);
        }
        
        for (Expense expense : dataManager.getExpenses()) {
            if (expense.getDate().isAfter(today.minusDays(7)) && !expense.getDate().isAfter(today)) {
                String dateStr = expense.getDate().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd"));
                dailyTotals.merge(dateStr, expense.getAmount(), Double::sum);
            }
        }
        
        return dailyTotals;
    }
    
    private void updateCategoryBreakdown() {
        categoryStatsPanel.removeAll();
        
        Map<Expense.Category, Double> categoryTotals = new HashMap<>();
        double total = 0.0;
        
        for (Expense expense : dataManager.getExpenses()) {
            categoryTotals.merge(expense.getCategory(), expense.getAmount(), Double::sum);
            total += expense.getAmount();
        }
        
        if (categoryTotals.isEmpty()) {
            categoryStatsPanel.revalidate();
            categoryStatsPanel.repaint();
            return;
        }
        
        final double finalTotal = total;
        List<Map.Entry<Expense.Category, Double>> sortedEntries = categoryTotals.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
            .collect(Collectors.toList());
        
        int colorIndex = 0;
        for (Map.Entry<Expense.Category, Double> entry : sortedEntries) {
            double percentage = (entry.getValue() / finalTotal) * 100;
            categoryStatsPanel.add(createCategoryCard(
                entry.getKey().getDisplayName(),
                entry.getValue(),
                percentage,
                CHART_COLORS[colorIndex % CHART_COLORS.length]
            ));
            colorIndex++;
        }
        
        categoryStatsPanel.revalidate();
        categoryStatsPanel.repaint();
    }
    
    private JPanel createCategoryCard(String category, double amount, double percentage, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_SECONDARY);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, color),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel nameLabel = new JLabel(category);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(TEXT_SECONDARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(nameLabel);
        
        card.add(Box.createVerticalStrut(8));
        
        JLabel amountLabel = new JLabel(String.format("₹%.2f", amount));
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        amountLabel.setForeground(TEXT_PRIMARY);
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(amountLabel);
        
        card.add(Box.createVerticalStrut(5));
        
        JLabel percentLabel = new JLabel(String.format("%.1f%% of total", percentage));
        percentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        percentLabel.setForeground(TEXT_SECONDARY);
        percentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(percentLabel);
        
        return card;
    }
    
    // Simple Pie Chart implementation
    private class SimplePieChart extends JPanel {
        private Map<Expense.Category, Double> data;
        
        public SimplePieChart(Map<Expense.Category, Double> data) {
            this.data = data;
            setBackground(BG_SECONDARY);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int diameter = Math.min(width, height) - 100;
            int x = (width - diameter) / 2;
            int y = (height - diameter) / 2;
            
            double total = data.values().stream().mapToDouble(Double::doubleValue).sum();
            if (total == 0) return;
            
            int startAngle = 0;
            int colorIndex = 0;
            
            for (Map.Entry<Expense.Category, Double> entry : data.entrySet()) {
                int angle = (int) Math.round((entry.getValue() / total) * 360);
                
                g2.setColor(CHART_COLORS[colorIndex % CHART_COLORS.length]);
                g2.fillArc(x, y, diameter, diameter, startAngle, angle);
                
                startAngle += angle;
                colorIndex++;
            }
            
            // Draw legend
            int legendY = 20;
            int legendX = width - 180;
            colorIndex = 0;
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            for (Map.Entry<Expense.Category, Double> entry : data.entrySet()) {
                g2.setColor(CHART_COLORS[colorIndex % CHART_COLORS.length]);
                g2.fillRect(legendX, legendY, 15, 15);
                
                g2.setColor(TEXT_SECONDARY);
                g2.drawString(entry.getKey().getDisplayName(), legendX + 20, legendY + 12);
                
                legendY += 25;
                colorIndex++;
            }
        }
    }
    
    // Simple Bar Chart implementation
    private class SimpleBarChart extends JPanel {
        private Map<String, Double> data;
        
        public SimpleBarChart(Map<String, Double> data) {
            this.data = data;
            setBackground(BG_SECONDARY);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            int chartHeight = height - 2 * padding;
            int chartWidth = width - 2 * padding;
            
            double maxValue = data.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
            if (maxValue == 0) maxValue = 1.0;
            
            int barWidth = chartWidth / (data.size() * 2);
            int x = padding;
            
            g2.setColor(TEXT_SECONDARY);
            g2.drawLine(padding, height - padding, width - padding, height - padding);
            g2.drawLine(padding, padding, padding, height - padding);
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int barHeight = (int) ((entry.getValue() / maxValue) * chartHeight);
                int barY = height - padding - barHeight;
                
                g2.setColor(PRIMARY);
                g2.fillRoundRect(x, barY, barWidth, barHeight, 5, 5);
                
                g2.setColor(TEXT_SECONDARY);
                String label = entry.getKey();
                int labelWidth = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, x + (barWidth - labelWidth) / 2, height - padding + 20);
                
                if (entry.getValue() > 0) {
                    String value = String.format("₹%.0f", entry.getValue());
                    int valueWidth = g2.getFontMetrics().stringWidth(value);
                    g2.drawString(value, x + (barWidth - valueWidth) / 2, barY - 5);
                }
                
                x += barWidth * 2;
            }
        }
    }
}
