package com.expenso;

import com.expenso.ui.ExpensoApp;
import javax.swing.*;

/**
 * Main entry point for Expenso Finance Management Application
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
        
        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            ExpensoApp app = new ExpensoApp();
            app.setVisible(true);
        });
    }
}
