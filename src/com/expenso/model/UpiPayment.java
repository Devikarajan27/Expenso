package com.expenso.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UPI Payment model class
 */
public class UpiPayment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long id;
    private String payeeUpiId;
    private String payeeName;
    private double amount;
    private String note;
    private LocalDateTime timestamp;
    private PaymentStatus status;
    
    public enum PaymentStatus {
        INITIATED("Initiated"),
        PENDING("Pending"),
        COMPLETED("Completed"),
        FAILED("Failed");
        
        private final String displayName;
        
        PaymentStatus(String displayName) {
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
    
    public UpiPayment() {
        this.id = System.currentTimeMillis();
        this.timestamp = LocalDateTime.now();
        this.status = PaymentStatus.INITIATED;
    }
    
    public UpiPayment(String payeeUpiId, String payeeName, double amount, String note) {
        this();
        this.payeeUpiId = payeeUpiId;
        this.payeeName = payeeName;
        this.amount = amount;
        this.note = note;
    }
    
    // Generate UPI deep link
    public String generateUpiLink() {
        StringBuilder upiLink = new StringBuilder("upi://pay?");
        upiLink.append("pa=").append(payeeUpiId);
        upiLink.append("&pn=").append(payeeName.replace(" ", "%20"));
        upiLink.append("&am=").append(String.format("%.2f", amount));
        upiLink.append("&cu=INR");
        if (note != null && !note.isEmpty()) {
            upiLink.append("&tn=").append(note.replace(" ", "%20"));
        }
        return upiLink.toString();
    }
    
    // Getters and Setters
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getPayeeUpiId() {
        return payeeUpiId;
    }
    
    public void setPayeeUpiId(String payeeUpiId) {
        this.payeeUpiId = payeeUpiId;
    }
    
    public String getPayeeName() {
        return payeeName;
    }
    
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
    
    public String getFormattedAmount() {
        return String.format("₹%.2f", amount);
    }
    
    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }
}
