package com.expenso.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Transaction model for imported bank/UPI transactions
 */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long id;
    private String description;
    private double amount;
    private TransactionType type;
    private LocalDate date;
    private LocalDateTime timestamp;
    private String referenceNumber;
    private String account;
    private String source; // Bank name, UPI app, etc.
    private boolean imported;
    
    public enum TransactionType {
        DEBIT("Debit", "Expense"),
        CREDIT("Credit", "Income"),
        UPI_SENT("UPI Sent", "Expense"),
        UPI_RECEIVED("UPI Received", "Income"),
        CARD_PAYMENT("Card Payment", "Expense"),
        ATM_WITHDRAWAL("ATM Withdrawal", "Expense"),
        BANK_TRANSFER("Bank Transfer", "Expense"),
        OTHER("Other", "Other");
        
        private final String displayName;
        private final String category;
        
        TransactionType(String displayName, String category) {
            this.displayName = displayName;
            this.category = category;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getCategory() {
            return category;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    public Transaction() {
        this.id = System.currentTimeMillis();
        this.timestamp = LocalDateTime.now();
        this.imported = true;
    }
    
    public Transaction(String description, double amount, TransactionType type, LocalDate date) {
        this();
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }
    
    // Convert to Expense object
    public Expense toExpense() {
        // Only convert debit/expense transactions
        if (type == TransactionType.CREDIT || type == TransactionType.UPI_RECEIVED) {
            return null;
        }
        
        Expense.Category category = categorizeTransaction();
        return new Expense(description, amount, category, date);
    }
    
    // Smart categorization based on description
    private Expense.Category categorizeTransaction() {
        String desc = description.toLowerCase();
        
        // Food keywords
        if (desc.contains("swiggy") || desc.contains("zomato") || desc.contains("restaurant") ||
            desc.contains("food") || desc.contains("cafe") || desc.contains("hotel") ||
            desc.contains("kitchen") || desc.contains("dominos") || desc.contains("mcdonald")) {
            return Expense.Category.FOOD;
        }
        
        // Transport keywords
        if (desc.contains("uber") || desc.contains("ola") || desc.contains("rapido") ||
            desc.contains("fuel") || desc.contains("petrol") || desc.contains("metro") ||
            desc.contains("bus") || desc.contains("taxi") || desc.contains("auto")) {
            return Expense.Category.TRANSPORT;
        }
        
        // Shopping keywords
        if (desc.contains("amazon") || desc.contains("flipkart") || desc.contains("myntra") ||
            desc.contains("shop") || desc.contains("mall") || desc.contains("store") ||
            desc.contains("retail") || desc.contains("purchase")) {
            return Expense.Category.SHOPPING;
        }
        
        // Entertainment keywords
        if (desc.contains("netflix") || desc.contains("prime") || desc.contains("hotstar") ||
            desc.contains("movie") || desc.contains("theater") || desc.contains("cinema") ||
            desc.contains("game") || desc.contains("spotify") || desc.contains("youtube")) {
            return Expense.Category.ENTERTAINMENT;
        }
        
        // Bills keywords
        if (desc.contains("bill") || desc.contains("electricity") || desc.contains("water") ||
            desc.contains("gas") || desc.contains("internet") || desc.contains("mobile") ||
            desc.contains("recharge") || desc.contains("subscription")) {
            return Expense.Category.BILLS;
        }
        
        // Healthcare keywords
        if (desc.contains("hospital") || desc.contains("doctor") || desc.contains("medical") ||
            desc.contains("pharmacy") || desc.contains("medicine") || desc.contains("clinic") ||
            desc.contains("health")) {
            return Expense.Category.HEALTHCARE;
        }
        
        // Education keywords
        if (desc.contains("school") || desc.contains("college") || desc.contains("course") ||
            desc.contains("education") || desc.contains("book") || desc.contains("tuition") ||
            desc.contains("udemy") || desc.contains("coursera")) {
            return Expense.Category.EDUCATION;
        }
        
        return Expense.Category.OTHER;
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public String getAccount() {
        return account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public boolean isImported() {
        return imported;
    }
    
    public void setImported(boolean imported) {
        this.imported = imported;
    }
    
    public String getFormattedAmount() {
        return String.format("₹%.2f", amount);
    }
    
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s - %s - %s",
            getFormattedDate(), description, getFormattedAmount(), type.getDisplayName());
    }
}
