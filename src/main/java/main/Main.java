package main;

import gui.LibraryManagementGUI;
import util.DatabaseConnection;

import javax.swing.*;

/**
 * Main class - Entry point for Library Management System
 */
public class Main {
    
    public static void main(String[] args) {
        // Set system look and feel
        setLookAndFeel();
        
        // Print application info
        printApplicationInfo();
        
        // Test database connection before starting GUI
        if (!DatabaseConnection.testConnection()) {
            System.err.println("WARNING: Cannot connect to database!");
            System.err.println("Please ensure MySQL is running and the library_db database exists.");
            System.err.println("The application will still start, but database operations will fail.");
            System.err.println();
        }
        
        // Start GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                LibraryManagementGUI gui = new LibraryManagementGUI();
                gui.setVisible(true);
                System.out.println("Library Management System GUI started successfully!");
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
                
                // Show error dialog
                JOptionPane.showMessageDialog(null,
                    "Error starting application:\n" + e.getMessage(),
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE);
                
                System.exit(1);
            }
        });
    }
    
    /**
     * Set system look and feel for better appearance
     */
    private static void setLookAndFeel() {
        try {
            // Try to set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and Feel set to: " + UIManager.getLookAndFeel().getName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
            // Continue with default look and feel
        }
    }
    
    /**
     * Print application information to console
     */
    private static void printApplicationInfo() {
        System.out.println("==========================================");
        System.out.println("    Library Management System v1.0");
        System.out.println("==========================================");
        System.out.println("Features:");
        System.out.println("- Add, Update, Delete books");
        System.out.println("- Search by title or author");
        System.out.println("- View all books in table format");
        System.out.println("- Input validation and error handling");
        System.out.println("- MySQL database integration");
        System.out.println();
        System.out.println("Database Requirements:");
        System.out.println("- MySQL Server running on localhost:3306");
        System.out.println("- Database: library_db");
        System.out.println("- Table: books (id INT, title VARCHAR(255), author VARCHAR(255), year INT, pages INT)");
        System.out.println("- User: root (no password)");
        System.out.println();
        System.out.println("Starting application...");
        System.out.println();
    }
    
    /**
     * Create database table if it doesn't exist (optional helper method)
     * This method can be called to automatically create the books table
     */
    public static void createDatabaseTable() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS books (
                id INT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                author VARCHAR(255) NOT NULL,
                year INT NOT NULL,
                pages INT NOT NULL
            )
            """;
        
        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.createStatement()) {
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("Database table 'books' created or verified successfully!");
            
        } catch (Exception e) {
            System.err.println("Error creating database table: " + e.getMessage());
        }
    }
}