package com.expenso.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Expense model class representing a single expense entry
 */
public class Expense implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long id;
    private String name;
    private double amount;
    private Category category;
    private LocalDate date;
    
    public enum Category {
        FOOD("🍔 Food"),
        TRANSPORT("🚗 Transport"),
        SHOPPING("🛍️ Shopping"),
        ENTERTAINMENT("🎮 Entertainment"),
        BILLS("📄 Bills"),
        HEALTHCARE("🏥 Healthcare"),
        EDUCATION("📚 Education"),
        OTHER("📦 Other");
        
        private final String displayName;
        
        Category(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public Expense() {
        this.id = System.currentTimeMillis();
        this.date = LocalDate.now();
    }
    
    public Expense(String name, double amount, Category category, LocalDate date) {
        this.id = System.currentTimeMillis();
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    public String getFormattedAmount() {
        return String.format("₹%.2f", amount);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s - %s - %s", 
            name, getFormattedAmount(), category.getDisplayName(), getFormattedDate());
    }
}
