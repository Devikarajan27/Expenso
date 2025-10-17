package com.expenso.data;

import com.expenso.model.Expense;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages data persistence for expenses and budget
 */
public class DataManager {
    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + ".expenso";
    private static final String EXPENSES_FILE = DATA_DIR + File.separator + "expenses.dat";
    private static final String BUDGET_FILE = DATA_DIR + File.separator + "budget.dat";
    
    private List<Expense> expenses;
    private double budget;
    
    public DataManager() {
        createDataDirectory();
        loadData();
    }
    
    private void createDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public void loadData() {
        expenses = loadExpenses();
        budget = loadBudget();
    }
    
    @SuppressWarnings("unchecked")
    private List<Expense> loadExpenses() {
        File file = new File(EXPENSES_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Expense>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error loading expenses: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private double loadBudget() {
        File file = new File(BUDGET_FILE);
        if (!file.exists()) {
            return 0.0;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return ois.readDouble();
        } catch (Exception e) {
            System.err.println("Error loading budget: " + e.getMessage());
            return 0.0;
        }
    }
    
    public void saveExpenses() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EXPENSES_FILE))) {
            oos.writeObject(expenses);
        } catch (Exception e) {
            System.err.println("Error saving expenses: " + e.getMessage());
        }
    }
    
    public void saveBudget() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BUDGET_FILE))) {
            oos.writeDouble(budget);
        } catch (Exception e) {
            System.err.println("Error saving budget: " + e.getMessage());
        }
    }
    
    public List<Expense> getExpenses() {
        return expenses;
    }
    
    public void addExpense(Expense expense) {
        expenses.add(0, expense);
        saveExpenses();
    }
    
    public void deleteExpense(long id) {
        expenses.removeIf(e -> e.getId() == id);
        saveExpenses();
    }
    
    public void clearAllExpenses() {
        expenses.clear();
        saveExpenses();
    }
    
    public double getBudget() {
        return budget;
    }
    
    public void setBudget(double budget) {
        this.budget = budget;
        saveBudget();
    }
    
    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }
    
    public double getCurrentMonthExpenses() {
        int currentMonth = java.time.LocalDate.now().getMonthValue();
        int currentYear = java.time.LocalDate.now().getYear();
        
        return expenses.stream()
            .filter(e -> e.getDate().getMonthValue() == currentMonth 
                      && e.getDate().getYear() == currentYear)
            .mapToDouble(Expense::getAmount)
            .sum();
    }
}
