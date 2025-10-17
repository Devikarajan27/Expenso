package com.expenso.util;

import com.expenso.model.Transaction;
import com.expenso.model.Transaction.TransactionType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

/**
 * Parser for transaction emails from banks and UPI apps
 * Extracts transaction details from email text
 */
public class EmailTransactionParser {
    
    // Common patterns for Indian banks and UPI apps
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(?:Rs\\.?|INR|₹)\\s*([\\d,]+(?:\\.\\d{2})?)");
    private static final Pattern UPI_REF_PATTERN = Pattern.compile("UPI Ref No\\s*:?\\s*(\\d+)");
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})");
    
    /**
     * Parse email content for transactions
     */
    public static List<Transaction> parseEmailContent(String emailContent, String subject) {
        List<Transaction> transactions = new ArrayList<>();
        
        try {
            // Check if it's a transaction notification
            if (!isTransactionEmail(subject, emailContent)) {
                return transactions;
            }
            
            Transaction txn = new Transaction();
            
            // Extract amount
            double amount = extractAmount(emailContent);
            if (amount == 0) return transactions;
            txn.setAmount(amount);
            
            // Extract date
            LocalDate date = extractDate(emailContent);
            txn.setDate(date);
            
            // Determine transaction type
            TransactionType type = determineTypeFromEmail(subject, emailContent);
            txn.setType(type);
            
            // Extract description
            String description = extractDescription(emailContent, subject);
            txn.setDescription(description);
            
            // Extract reference number
            String refNo = extractReferenceNumber(emailContent);
            if (refNo != null) {
                txn.setReferenceNumber(refNo);
            }
            
            // Set source
            txn.setSource(detectSource(emailContent, subject));
            
            transactions.add(txn);
            
        } catch (Exception e) {
            System.err.println("Error parsing email: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Check if email is a transaction notification
     */
    private static boolean isTransactionEmail(String subject, String content) {
        String combined = (subject + " " + content).toLowerCase();
        
        return combined.contains("debited") || combined.contains("credited") ||
               combined.contains("transaction") || combined.contains("payment") ||
               combined.contains("upi") || combined.contains("transferred") ||
               combined.contains("sent money") || combined.contains("received money") ||
               combined.contains("spent") || combined.contains("withdrawn");
    }
    
    /**
     * Extract amount from email content
     */
    private static double extractAmount(String content) {
        Matcher matcher = AMOUNT_PATTERN.matcher(content);
        
        if (matcher.find()) {
            String amountStr = matcher.group(1).replaceAll(",", "");
            try {
                return Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        
        return 0.0;
    }
    
    /**
     * Extract date from email content
     */
    private static LocalDate extractDate(String content) {
        Matcher matcher = DATE_PATTERN.matcher(content);
        
        if (matcher.find()) {
            String dateStr = matcher.group(1);
            
            // Try different date formats
            DateTimeFormatter[] formats = {
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yy"),
                DateTimeFormatter.ofPattern("dd-MM-yy")
            };
            
            for (DateTimeFormatter formatter : formats) {
                try {
                    return LocalDate.parse(dateStr, formatter);
                } catch (Exception e) {
                    // Try next format
                }
            }
        }
        
        // Default to today
        return LocalDate.now();
    }
    
    /**
     * Determine transaction type from email
     */
    private static TransactionType determineTypeFromEmail(String subject, String content) {
        String combined = (subject + " " + content).toLowerCase();
        
        if (combined.contains("debited") || combined.contains("sent") || 
            combined.contains("paid") || combined.contains("payment successful")) {
            
            if (combined.contains("upi")) {
                return TransactionType.UPI_SENT;
            } else if (combined.contains("atm") || combined.contains("cash")) {
                return TransactionType.ATM_WITHDRAWAL;
            } else if (combined.contains("card") || combined.contains("pos")) {
                return TransactionType.CARD_PAYMENT;
            } else if (combined.contains("transfer")) {
                return TransactionType.BANK_TRANSFER;
            }
            return TransactionType.DEBIT;
        }
        
        if (combined.contains("credited") || combined.contains("received")) {
            if (combined.contains("upi")) {
                return TransactionType.UPI_RECEIVED;
            }
            return TransactionType.CREDIT;
        }
        
        return TransactionType.OTHER;
    }
    
    /**
     * Extract transaction description
     */
    private static String extractDescription(String content, String subject) {
        // Try to find merchant name or description
        String[] descPatterns = {
            "to\\s+([\\w\\s]+?)(?:on|for|UPI)",
            "from\\s+([\\w\\s]+?)(?:on|for|UPI)",
            "at\\s+([\\w\\s]+?)(?:on|for|UPI)",
            "sent to\\s+([\\w\\s]+)",
            "received from\\s+([\\w\\s]+)"
        };
        
        for (String patternStr : descPatterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String desc = matcher.group(1).trim();
                if (!desc.isEmpty() && desc.length() < 50) {
                    return desc;
                }
            }
        }
        
        // Fallback to subject line
        return subject.length() > 50 ? subject.substring(0, 50) : subject;
    }
    
    /**
     * Extract reference number
     */
    private static String extractReferenceNumber(String content) {
        Matcher matcher = UPI_REF_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Try generic reference pattern
        Pattern refPattern = Pattern.compile("Ref(?:erence)?\\s*(?:No\\.?|#)?\\s*:?\\s*([A-Z0-9]+)", Pattern.CASE_INSENSITIVE);
        matcher = refPattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Detect source (bank/app) from email
     */
    private static String detectSource(String content, String subject) {
        String combined = (subject + " " + content).toLowerCase();
        
        if (combined.contains("google pay") || combined.contains("googlepay") || combined.contains("gpay")) {
            return "Google Pay";
        } else if (combined.contains("phonepe")) {
            return "PhonePe";
        } else if (combined.contains("paytm")) {
            return "Paytm";
        } else if (combined.contains("amazon pay")) {
            return "Amazon Pay";
        } else if (combined.contains("sbi") || combined.contains("state bank")) {
            return "SBI";
        } else if (combined.contains("hdfc")) {
            return "HDFC Bank";
        } else if (combined.contains("icici")) {
            return "ICICI Bank";
        } else if (combined.contains("axis")) {
            return "Axis Bank";
        } else if (combined.contains("kotak")) {
            return "Kotak Bank";
        } else if (combined.contains("pnb")) {
            return "PNB";
        }
        
        return "Email Import";
    }
    
    /**
     * Parse multiple emails
     */
    public static List<Transaction> parseMultipleEmails(List<EmailData> emails) {
        List<Transaction> allTransactions = new ArrayList<>();
        
        for (EmailData email : emails) {
            List<Transaction> transactions = parseEmailContent(email.getContent(), email.getSubject());
            allTransactions.addAll(transactions);
        }
        
        return allTransactions;
    }
    
    /**
     * Simple email data holder
     */
    public static class EmailData {
        private String subject;
        private String content;
        private String sender;
        private LocalDate date;
        
        public EmailData(String subject, String content) {
            this.subject = subject;
            this.content = content;
        }
        
        public EmailData(String subject, String content, String sender, LocalDate date) {
            this.subject = subject;
            this.content = content;
            this.sender = sender;
            this.date = date;
        }
        
        public String getSubject() {
            return subject;
        }
        
        public String getContent() {
            return content;
        }
        
        public String getSender() {
            return sender;
        }
        
        public LocalDate getDate() {
            return date;
        }
    }
}
