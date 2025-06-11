package gui;

import model.Book;
import service.BookService;
import service.BookService.ServiceResult;
import util.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Main GUI class for Library Management System
 */
public class LibraryManagementGUI extends JFrame {
    
    // Service layer
    private final BookService bookService;
    
    // GUI Components
    private JTextField idField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField yearField;
    private JTextField pagesField;
    private JTextField searchField;
    
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton refreshButton;
    private JButton searchTitleButton;
    private JButton searchAuthorButton;
    
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public LibraryManagementGUI() {
        this.bookService = new BookService();
        initializeGUI();
        checkDatabaseConnection();
        loadBooks();
    }
    
    /**
     * Initialize the GUI components
     */
    private void initializeGUI() {
        setTitle("Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panels
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        
        // Set window properties
        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
    }
    
    /**
     * Create the top panel with input fields and buttons
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Input panel
        JPanel inputPanel = createInputPanel();
        topPanel.add(inputPanel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        topPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        return topPanel;
    }
    
    /**
     * Create input fields panel
     */
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Book Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create input fields
        idField = new JTextField(10);
        titleField = new JTextField(20);
        authorField = new JTextField(20);
        yearField = new JTextField(10);
        pagesField = new JTextField(10);
        
        // Add components
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(idField, gbc);
        
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(authorField, gbc);
        
        gbc.gridx = 2; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 3; gbc.anchor = GridBagConstraints.WEST;
        panel.add(yearField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Pages:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(pagesField, gbc);
        
        return panel;
    }
    
    /**
     * Create buttons panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        // Create buttons
        addButton = new JButton("Add Book");
        updateButton = new JButton("Update Book");
        deleteButton = new JButton("Delete Book");
        clearButton = new JButton("Clear Fields");
        refreshButton = new JButton("Refresh");
        
        // Add action listeners
        addButton.addActionListener(e -> addBook());
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadBooks());
        
        // Add buttons to panel
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    /**
     * Create search panel
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Search"));
        
        searchField = new JTextField(20);
        searchTitleButton = new JButton("Search by Title");
        searchAuthorButton = new JButton("Search by Author");
        
        searchTitleButton.addActionListener(e -> searchByTitle());
        searchAuthorButton.addActionListener(e -> searchByAuthor());
        
        panel.add(new JLabel("Search:"));
        panel.add(searchField);
        panel.add(searchTitleButton);
        panel.add(searchAuthorButton);
        
        return panel;
    }
    
    /**
     * Create center panel with table
     */
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Books List"));
        
        // Create table
        String[] columnNames = {"ID", "Title", "Author", "Year", "Pages"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFieldsFromSelectedRow();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create bottom panel with status label
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(statusLabel);
        return panel;
    }
    
    /**
     * Check database connection on startup
     */
    private void checkDatabaseConnection() {
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this,
                "Cannot connect to database!\nPlease check your MySQL server and database configuration.",
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE);
            updateStatus("Database connection failed!");
        } else {
            updateStatus("Connected to database successfully!");
        }
    }
    
    /**
     * Load all books from database and populate table
     */
    private void loadBooks() {
        SwingUtilities.invokeLater(() -> {
            updateStatus("Loading books...");
            
            ServiceResult<List<Book>> result = bookService.getAllBooks();
            if (result.isSuccess()) {
                populateTable(result.getData());
                updateStatus("Loaded " + result.getData().size() + " books");
            } else {
                showErrorMessage("Load Books", result.getMessage());
                updateStatus("Failed to load books");
            }
        });
    }
    
    /**
     * Add a new book
     */
    private void addBook() {
        try {
            Book book = createBookFromFields();
            ServiceResult result = bookService.addBook(book);
            
            if (result.isSuccess()) {
                showSuccessMessage(result.getMessage());
                clearFields();
                loadBooks();
                updateStatus("Book added successfully");
            } else {
                showErrorMessage("Add Book", result.getMessage());
                updateStatus("Failed to add book");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Add Book", "Please enter valid numbers for ID, Year, and Pages!");
        } catch (Exception e) {
            showErrorMessage("Add Book", "Error creating book: " + e.getMessage());
        }
    }
    
    /**
     * Update selected book
     */
    private void updateBook() {
        try {
            Book book = createBookFromFields();
            ServiceResult result = bookService.updateBook(book);
            
            if (result.isSuccess()) {
                showSuccessMessage(result.getMessage());
                clearFields();
                loadBooks();
                updateStatus("Book updated successfully");
            } else {
                showErrorMessage("Update Book", result.getMessage());
                updateStatus("Failed to update book");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Update Book", "Please enter valid numbers for ID, Year, and Pages!");
        } catch (Exception e) {
            showErrorMessage("Update Book", "Error updating book: " + e.getMessage());
        }
    }
    
    /**
     * Delete selected book
     */
    private void deleteBook() {
        try {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                showErrorMessage("Delete Book", "Please enter a book ID to delete!");
                return;
            }
            
            int id = Integer.parseInt(idText);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the book with ID: " + id + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                ServiceResult result = bookService.deleteBook(id);
                
                if (result.isSuccess()) {
                    showSuccessMessage(result.getMessage());
                    clearFields();
                    loadBooks();
                    updateStatus("Book deleted successfully");
                } else {
                    showErrorMessage("Delete Book", result.getMessage());
                    updateStatus("Failed to delete book");
                }
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Delete Book", "Please enter a valid book ID!");
        }
    }
    
    /**
     * Search books by title
     */
    private void searchByTitle() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            showErrorMessage("Search", "Please enter a title to search for!");
            return;
        }
        
        updateStatus("Searching by title...");
        ServiceResult<List<Book>> result = bookService.searchBooksByTitle(searchText);
        
        if (result.isSuccess()) {
            populateTable(result.getData());
            updateStatus(result.getMessage());
        } else {
            showErrorMessage("Search by Title", result.getMessage());
            updateStatus("Search failed");
        }
    }
    
    /**
     * Search books by author
     */
    private void searchByAuthor() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            showErrorMessage("Search", "Please enter an author to search for!");
            return;
        }
        
        updateStatus("Searching by author...");
        ServiceResult<List<Book>> result = bookService.searchBooksByAuthor(searchText);
        
        if (result.isSuccess()) {
            populateTable(result.getData());
            updateStatus(result.getMessage());
        } else {
            showErrorMessage("Search by Author", result.getMessage());
            updateStatus("Search failed");
        }
    }
    
    /**
     * Clear all input fields
     */
    private void clearFields() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        yearField.setText("");
        pagesField.setText("");
        searchField.setText("");
        booksTable.clearSelection();
    }
    
    /**
     * Create Book object from input fields
     */
    private Book createBookFromFields() {
        int id = Integer.parseInt(idField.getText().trim());
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        int year = Integer.parseInt(yearField.getText().trim());
        int pages = Integer.parseInt(pagesField.getText().trim());
        
        return new Book(id, title, author, year, pages);
    }
    
    /**
     * Populate input fields from selected table row
     */
    private void populateFieldsFromSelectedRow() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow != -1) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            titleField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            authorField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            yearField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            pagesField.setText(tableModel.getValueAt(selectedRow, 4).toString());
        }
    }
    
    /**
     * Populate table with book data
     */
    private void populateTable(List<Book> books) {
        tableModel.setRowCount(0);
        
        for (Book book : books) {
            Object[] row = {
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getYear(),
                book.getPages()
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Show success message
     */
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Show error message
     */
    private void showErrorMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Update status label
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
}
            