package com.expenso.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Simple QR Code generator without external dependencies
 * Generates a basic QR-like pattern for UPI links
 */
public class QRCodeGenerator {
    
    /**
     * Generate a simple QR-like image for display
     * Note: This creates a visual representation. For actual scanning,
     * users should use the UPI link or a proper QR library.
     */
    public static BufferedImage generateQRImage(String data, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // White background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size, size);
        
        // Create a pattern based on the data hash
        g2d.setColor(Color.BLACK);
        int blockSize = size / 25;
        
        // Generate pseudo-random pattern based on data
        for (int i = 0; i < data.length(); i++) {
            int charCode = data.charAt(i);
            int x = (charCode * (i + 1)) % 23;
            int y = (charCode * (i + 2)) % 23;
            g2d.fillRect(x * blockSize + blockSize, y * blockSize + blockSize, blockSize, blockSize);
        }
        
        // Add corner markers (typical QR code feature)
        drawCornerMarker(g2d, blockSize, blockSize, blockSize * 3);
        drawCornerMarker(g2d, size - blockSize * 4, blockSize, blockSize * 3);
        drawCornerMarker(g2d, blockSize, size - blockSize * 4, blockSize * 3);
        
        // Add center marker
        int center = size / 2;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(center - blockSize * 2, center - blockSize * 2, blockSize * 4, blockSize * 4);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(center - blockSize, center - blockSize, blockSize * 2, blockSize * 2);
        
        // Add border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(0, 0, size - 1, size - 1);
        
        g2d.dispose();
        return image;
    }
    
    private static void drawCornerMarker(Graphics2D g2d, int x, int y, int size) {
        // Outer square
        g2d.setColor(Color.BLACK);
        g2d.fillRect(x, y, size, size);
        
        // Inner white square
        g2d.setColor(Color.WHITE);
        int innerSize = size / 3;
        g2d.fillRect(x + innerSize, y + innerSize, innerSize, innerSize);
        
        // Center dot
        g2d.setColor(Color.BLACK);
        int dotSize = innerSize / 2;
        g2d.fillRect(x + innerSize + dotSize / 2, y + innerSize + dotSize / 2, dotSize, dotSize);
    }
    
    /**
     * Generate a simple visual pattern for UPI
     */
    public static BufferedImage generateUpiVisual(String upiId, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(20, 184, 166),
            size, size, new Color(249, 115, 22)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, size, size);
        
        // UPI icon representation
        g2d.setColor(Color.WHITE);
        int iconSize = size / 3;
        int iconX = (size - iconSize) / 2;
        int iconY = size / 4;
        
        // Draw stylized UPI icon
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(iconX, iconY, iconSize, iconSize);
        g2d.fillOval(iconX + iconSize / 3, iconY + iconSize / 3, iconSize / 3, iconSize / 3);
        
        // Draw UPI text
        g2d.setFont(new Font("Arial", Font.BOLD, size / 15));
        String text = "UPI Payment";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2d.drawString(text, (size - textWidth) / 2, size - size / 4);
        
        // Draw UPI ID
        g2d.setFont(new Font("Arial", Font.PLAIN, size / 25));
        String displayId = upiId.length() > 20 ? upiId.substring(0, 17) + "..." : upiId;
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(displayId);
        g2d.drawString(displayId, (size - textWidth) / 2, size - size / 6);
        
        g2d.dispose();
        return image;
    }
}
