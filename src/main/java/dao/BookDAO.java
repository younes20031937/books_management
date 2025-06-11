package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Book;
import util.DatabaseConnection;

/**
 * Data Access Object for Book operations
 */
public class BookDAO {
    
    // SQL Queries
    private static final String INSERT_BOOK = "INSERT INTO books (id, title, author, year, pages) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_BOOKS = "SELECT * FROM books ORDER BY id";
    private static final String SELECT_BOOK_BY_ID = "SELECT * FROM books WHERE id = ?";
    private static final String UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, year = ?, pages = ? WHERE id = ?";
    private static final String DELETE_BOOK = "DELETE FROM books WHERE id = ?";
    private static final String COUNT_BOOKS = "SELECT COUNT(*) FROM books";
    private static final String BOOK_EXISTS = "SELECT 1 FROM books WHERE id = ?";
    
    /**
     * Insert a new book into the database
     * @param book Book object to insert
     * @return true if insertion is successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean insertBook(Book book) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_BOOK)) {
            
            pstmt.setInt(1, book.getId());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setInt(4, book.getYear());
            pstmt.setInt(5, book.getPages());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Retrieve all books from the database
     * @return List of all books
     * @throws SQLException if database error occurs
     */
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_BOOKS)) {
            
            while (rs.next()) {
                Book book = extractBookFromResultSet(rs);
                books.add(book);
            }
        }
        
        return books;
    }
    
    /**
     * Retrieve a book by its ID
     * @param id Book ID
     * @return Book object if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public Book getBookById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BOOK_BY_ID)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractBookFromResultSet(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Update an existing book in the database
     * @param book Book object with updated information
     * @return true if update is successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean updateBook(Book book) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_BOOK)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setInt(3, book.getYear());
            pstmt.setInt(4, book.getPages());
            pstmt.setInt(5, book.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Delete a book from the database
     * @param id Book ID to delete
     * @return true if deletion is successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean deleteBook(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_BOOK)) {
            
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Get total number of books in the database
     * @return Total count of books
     * @throws SQLException if database error occurs
     */
    public int getTotalBooksCount() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(COUNT_BOOKS)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Check if a book exists with the given ID
     * @param id Book ID to check
     * @return true if book exists, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean bookExists(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(BOOK_EXISTS)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Search books by title (partial match)
     * @param title Title to search for
     * @return List of books matching the title
     * @throws SQLException if database error occurs
     */
    public List<Book> searchBooksByTitle(String title) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE title LIKE ? ORDER BY title";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + title + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = extractBookFromResultSet(rs);
                    books.add(book);
                }
            }
        }
        
        return books;
    }
    
    /**
     * Search books by author (partial match)
     * @param author Author to search for
     * @return List of books by the author
     * @throws SQLException if database error occurs
     */
    public List<Book> searchBooksByAuthor(String author) throws SQLException {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE author LIKE ? ORDER BY author, title";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + author + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = extractBookFromResultSet(rs);
                    books.add(book);
                }
            }
        }
        
        return books;
    }
    
    /**
     * Extract Book object from ResultSet
     * @param rs ResultSet containing book data
     * @return Book object
     * @throws SQLException if error reading from ResultSet
     */
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getInt("year"),
            rs.getInt("pages")
        );
    }
}