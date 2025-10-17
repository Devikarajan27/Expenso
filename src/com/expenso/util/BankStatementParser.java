package com.expenso.util;

import com.expenso.model.Transaction;
import com.expenso.model.Transaction.TransactionType;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Parser for bank statement files (CSV, Excel-exported CSV)
 * Supports multiple bank formats
 */
public class BankStatementParser {
    
    private static final DateTimeFormatter[] DATE_FORMATS = {
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd MMM yyyy"),
        DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd")
    };
    
    /**
     * Parse CSV file from bank statement
     */
    public static List<Transaction> parseCSV(File file) throws Exception {
        List<Transaction> transactions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            int dateCol = -1, descCol = -1, debitCol = -1, creditCol = -1, balanceCol = -1;
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;
                
                String[] columns = parseCSVLine(line);
                
                // First line - detect column indices
                if (isFirstLine) {
                    isFirstLine = false;
                    for (int i = 0; i < columns.length; i++) {
                        String col = columns[i].toLowerCase().trim();
                        
                        if (col.contains("date") || col.contains("txn date") || col.contains("transaction date")) {
                            dateCol = i;
                        } else if (col.contains("description") || col.contains("narration") || 
                                   col.contains("particulars") || col.contains("remarks")) {
                            descCol = i;
                        } else if (col.contains("debit") || col.contains("withdrawal") || 
                                   col.contains("paid") || col.contains("amount debited")) {
                            debitCol = i;
                        } else if (col.contains("credit") || col.contains("deposit") || 
                                   col.contains("received") || col.contains("amount credited")) {
                            creditCol = i;
                        } else if (col.contains("balance") || col.contains("closing")) {
                            balanceCol = i;
                        }
                    }
                    continue;
                }
                
                // Parse transaction data
                try {
                    if (dateCol >= 0 && descCol >= 0 && columns.length > Math.max(dateCol, descCol)) {
                        LocalDate date = parseDate(columns[dateCol]);
                        String description = columns[descCol].trim();
                        
                        // Skip if description is empty or balance row
                        if (description.isEmpty() || description.toLowerCase().contains("opening balance") ||
                            description.toLowerCase().contains("closing balance")) {
                            continue;
                        }
                        
                        double debitAmount = 0.0;
                        double creditAmount = 0.0;
                        
                        // Parse debit amount
                        if (debitCol >= 0 && debitCol < columns.length) {
                            debitAmount = parseAmount(columns[debitCol]);
                        }
                        
                        // Parse credit amount
                        if (creditCol >= 0 && creditCol < columns.length) {
                            creditAmount = parseAmount(columns[creditCol]);
                        }
                        
                        // Create transaction
                        if (debitAmount > 0) {
                            Transaction txn = new Transaction();
                            txn.setDate(date);
                            txn.setDescription(description);
                            txn.setAmount(debitAmount);
                            txn.setType(detectTransactionType(description, true));
                            txn.setSource("Bank Statement");
                            transactions.add(txn);
                        } else if (creditAmount > 0) {
                            Transaction txn = new Transaction();
                            txn.setDate(date);
                            txn.setDescription(description);
                            txn.setAmount(creditAmount);
                            txn.setType(TransactionType.CREDIT);
                            txn.setSource("Bank Statement");
                            transactions.add(txn);
                        }
                    }
                } catch (Exception e) {
                    // Skip malformed lines
                    System.err.println("Skipping line due to parse error: " + line);
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * Parse single CSV line handling quoted fields
     */
    private static String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
    
    /**
     * Parse date from string trying multiple formats
     */
    private static LocalDate parseDate(String dateStr) {
        dateStr = dateStr.trim();
        
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception e) {
                // Try next format
            }
        }
        
        // If all fails, return today
        return LocalDate.now();
    }
    
    /**
     * Parse amount from string
     */
    private static double parseAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return 0.0;
        }
        
        // Remove currency symbols, commas, and spaces
        amountStr = amountStr.replaceAll("[₹$,\\s]", "").trim();
        
        // Handle empty or non-numeric values
        if (amountStr.isEmpty() || amountStr.equals("-")) {
            return 0.0;
        }
        
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Detect transaction type from description
     */
    private static TransactionType detectTransactionType(String description, boolean isDebit) {
        String desc = description.toLowerCase();
        
        if (desc.contains("upi") || desc.contains("paytm") || desc.contains("phonepe") || 
            desc.contains("googlepay") || desc.contains("gpay")) {
            return isDebit ? TransactionType.UPI_SENT : TransactionType.UPI_RECEIVED;
        }
        
        if (desc.contains("atm") || desc.contains("cash withdrawal")) {
            return TransactionType.ATM_WITHDRAWAL;
        }
        
        if (desc.contains("card") || desc.contains("pos") || desc.contains("swipe")) {
            return TransactionType.CARD_PAYMENT;
        }
        
        if (desc.contains("transfer") || desc.contains("neft") || desc.contains("rtgs") || 
            desc.contains("imps")) {
            return TransactionType.BANK_TRANSFER;
        }
        
        return isDebit ? TransactionType.DEBIT : TransactionType.CREDIT;
    }
    
    /**
     * Get supported file extensions
     */
    public static String[] getSupportedExtensions() {
        return new String[]{"csv", "txt"};
    }
    
    /**
     * Check if file is supported
     */
    public static boolean isSupportedFile(File file) {
        String name = file.getName().toLowerCase();
        for (String ext : getSupportedExtensions()) {
            if (name.endsWith("." + ext)) {
                return true;
            }
        }
        return false;
    }
}
