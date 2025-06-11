package service;

import java.sql.SQLException;
import java.util.List;

import dao.BookDAO;
import model.Book;

/**
 * Service layer for Book operations
 * Contains business logic and validation
 */
public class BookService {
    
    private final BookDAO bookDAO;
    
    public BookService() {
        this.bookDAO = new BookDAO();
    }
    
    /**
     * Add a new book with validation
     * @param book Book to add
     * @return ServiceResult containing success status and message
     */
    public ServiceResult addBook(Book book) {
        try {
            // Validate book data
            ServiceResult validationResult = validateBook(book);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }
            
            // Check if book with same ID already exists
            if (bookDAO.bookExists(book.getId())) {
                return new ServiceResult(false, "A book with ID " + book.getId() + " already exists!");
            }
            
            // Insert book
            boolean success = bookDAO.insertBook(book);
            if (success) {
                return new ServiceResult(true, "Book added successfully!");
            } else {
                return new ServiceResult(false, "Failed to add book to database!");
            }
            
        } catch (SQLException e) {
            return new ServiceResult(false, "Database error: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing book with validation
     * @param book Book with updated information
     * @return ServiceResult containing success status and message
     */
    public ServiceResult updateBook(Book book) {
        try {
            // Validate book data
            ServiceResult validationResult = validateBook(book);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }
            
            // Check if book exists
            if (!bookDAO.bookExists(book.getId())) {
                return new ServiceResult(false, "Book with ID " + book.getId() + " not found!");
            }
            
            // Update book
            boolean success = bookDAO.updateBook(book);
            if (success) {
                return new ServiceResult(true, "Book updated successfully!");
            } else {
                return new ServiceResult(false, "Failed to update book!");
            }
            
        } catch (SQLException e) {
            return new ServiceResult(false, "Database error: " + e.getMessage());
        }
    }
    
    /**
     * Delete a book by ID
     * @param id Book ID to delete
     * @return ServiceResult containing success status and message
     */
    public ServiceResult deleteBook(int id) {
        try {
            // Validate ID
            if (id <= 0) {
                return new ServiceResult(false, "Invalid book ID!");
            }
            
            // Check if book exists
            if (!bookDAO.bookExists(id)) {
                return new ServiceResult(false, "Book with ID " + id + " not found!");
            }
            
            // Delete book
            boolean success = bookDAO.deleteBook(id);
            if (success) {
                return new ServiceResult(true, "Book deleted successfully!");
            } else {
                return new ServiceResult(false, "Failed to delete book!");
            }
            
        } catch (SQLException e) {
            return new ServiceResult(false, "Database error: " + e.getMessage());
        }
    }
    
    /**
     * Get all books
     * @return ServiceResult containing list of books or error message
     */
    public ServiceResult<List<Book>> getAllBooks() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            return new ServiceResult<>(true, "Books retrieved successfully!", books);
        } catch (SQLException e) {
            return new ServiceResult<>(false, "Database error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Get a book by ID
     * @param id Book ID
     * @return ServiceResult containing book or error message
     */
    public ServiceResult<Book> getBookById(int id) {
        try {
            if (id <= 0) {
                return new ServiceResult<>(false, "Invalid book ID!", null);
            }
            
            Book book = bookDAO.getBookById(id);
            if (book != null) {
                return new ServiceResult<>(true, "Book found!", book);
            } else {
                return new ServiceResult<>(false, "Book with ID " + id + " not found!", null);
            }
            
        } catch (SQLException e) {
            return new ServiceResult<>(false, "Database error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Search books by title
     * @param title Title to search for
     * @return ServiceResult containing list of matching books
     */
    public ServiceResult<List<Book>> searchBooksByTitle(String title) {
        try {
            if (title == null || title.trim().isEmpty()) {
                return new ServiceResult<>(false, "Search title cannot be empty!", null);
            }
            
            List<Book> books = bookDAO.searchBooksByTitle(title.trim());
            String message = books.isEmpty() ? "No books found with title containing: " + title 
                                            : "Found " + books.size() + " book(s)";
            return new ServiceResult<>(true, message, books);
            
        } catch (SQLException e) {
            return new ServiceResult<>(false, "Database error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Search books by author
     * @param author Author to search for
     * @return ServiceResult containing list of matching books
     */
    public ServiceResult<List<Book>> searchBooksByAuthor(String author) {
        try {
            if (author == null || author.trim().isEmpty()) {
                return new ServiceResult<>(false, "Search author cannot be empty!", null);
            }
            
            List<Book> books = bookDAO.searchBooksByAuthor(author.trim());
            String message = books.isEmpty() ? "No books found by author: " + author 
                                            : "Found " + books.size() + " book(s)";
            return new ServiceResult<>(true, message, books);
            
        } catch (SQLException e) {
            return new ServiceResult<>(false, "Database error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Get total number of books
     * @return ServiceResult containing total count
     */
    public ServiceResult<Integer> getTotalBooksCount() {
        try {
            int count = bookDAO.getTotalBooksCount();
            return new ServiceResult<>(true, "Total books count retrieved!", count);
        } catch (SQLException e) {
            return new ServiceResult<>(false, "Database error: " + e.getMessage(), 0);
        }
    }
    
    /**
     * Validate book data
     * @param book Book to validate
     * @return ServiceResult with validation result
     */
    private ServiceResult validateBook(Book book) {
        if (book == null) {
            return new ServiceResult(false, "Book object cannot be null!");
        }
        
        if (book.getId() <= 0) {
            return new ServiceResult(false, "Book ID must be a positive number!");
        }
        
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return new ServiceResult(false, "Book title cannot be empty!");
        }
        
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return new ServiceResult(false, "Book author cannot be empty!");
        }
        
        if (book.getYear() < 1000 || book.getYear() > java.time.Year.now().getValue()) {
            return new ServiceResult(false, "Book year must be between 1000 and " + java.time.Year.now().getValue() + "!");
        }
        
        if (book.getPages() <= 0) {
            return new ServiceResult(false, "Book pages must be a positive number!");
        }
        
        if (book.getTitle().length() > 255) {
            return new ServiceResult(false, "Book title is too long (max 255 characters)!");
        }
        
        if (book.getAuthor().length() > 255) {
            return new ServiceResult(false, "Book author name is too long (max 255 characters)!");
        }
        
        return new ServiceResult(true, "Book data is valid!");
    }
    
    /**
     * Inner class to represent service operation results
     */
    public static class ServiceResult<T> {
        private final boolean success;
        private final String message;
        private final T data;
        
        public ServiceResult(boolean success, String message) {
            this(success, message, null);
        }
        
        public ServiceResult(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public T getData() {
            return data;
        }
    }
}