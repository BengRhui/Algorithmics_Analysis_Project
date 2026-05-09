package Part_Two_Assignment;

import java.util.*;

/**
 * Represents a book in the library system.
 */
class Book {

    // Books have two attributes: Book
    private final String bookId;
    private String bookTitle;

    // Constructor to create a book
    public Book(String bookId, String bookTitle) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
    }

    // Getters and setters
    public String getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }}

/**
 * Represents the stock of a book in the library system.
 */
class BookStock {

    // Record the book and its quantity
    private final Book book;
    private int quantity;

    // Constructor
    public BookStock(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    // Getters and setters
    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }

    // Functions to increase and decrease the number of books in stock
    public void increaseQuantity() { quantity++; }
    public void decreaseQuantity() {
        if (quantity > 0) quantity--;
    }
}

/**
 * Represents a borrowing record for a student.
 */
class BorrowRecord {

    // Attributes of a borrowing record
    private final Book book;
    private final Date borrowDate;
    private Date returnDate;
    private boolean returned;

    // Constructor
    public BorrowRecord(SmartLibSystem system, String bookId) {
        this.book = system.retrieveBook(bookId);
        this.borrowDate = new Date();
        this.returned = false;
    }

    // Mark the book as returned
    public void markReturned() {
        this.returnDate = new Date();
        this.returned = true;
    }

    // Print the borrowing record
    @Override
    public String toString() {
        return "Book ID: " + book.getBookId() +
                ", Title: " + book.getBookTitle() +
                ", Borrow Date: " + borrowDate +
                ", Returned: " + returned +
                (returned ? ", Return Date: " + returnDate : "");
    }
}

/**
 * Represents a student in the library system.
 */
class Student {

    // Attributes of a student
    private final String studentId;
    private String studentName;
    private List<BorrowRecord> borrowingHistory;

    // Constructor
    public Student(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.borrowingHistory = new ArrayList<>();
    }

    // Getters and setters
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public List<BorrowRecord> getBorrowingHistory() { return borrowingHistory; }
    public void setBorrowingHistory(List<BorrowRecord> borrowingHistory) { this.borrowingHistory = borrowingHistory; }

    // Add borrowing record to student's borrowing history
    public void addBorrowRecord(BorrowRecord record) {
        borrowingHistory.add(record);
    }
}

/**
 * Smart Library System for managing student borrowing operations.
 */
class SmartLibSystem {

    // Inventory of books and database of students
    private final Map<String, BookStock> bookInventory;
    private final Map<String, Student> studentDatabase;

    // Constructor
    public SmartLibSystem() {
        bookInventory = new HashMap<>();
        studentDatabase = new HashMap<>();
    }

    // Add student to the system
    public void addStudent(Student student) {
        studentDatabase.put(student.getStudentId(), student);
    }

    // Operation 1: Retrieve the borrowing history for a student
    public void retrieveBorrowingHistory(String studentId) {

        // First, retrieve the student from the database (hashmap) - O(1)
        Student student = studentDatabase.get(studentId);

        // Check if the student exists - O(1)
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        // Print the details of the student
        System.out.println("\nBorrowing History for: " + student.getStudentName());
        System.out.println("Student ID: " + student.getStudentId());

        // Retrieve the borrowing history - O(1)
        List<BorrowRecord> history = student.getBorrowingHistory();

        // Check if the history is empty
        if (history.isEmpty()) {
            System.out.println("No borrowing records found.");
            return;
        }

        // Retrieve and print each borrowing record - O(k) where k = number of borrowed books
        for (BorrowRecord record : history) {
            System.out.println(record);
        }
    }

    // Operation 2: Record a new borrowing transaction
    public void recordNewBorrowingTransaction(String studentId, String bookId) {

        // Retrieve the student from the database - O(1)
        Student student = studentDatabase.get(studentId);

        // Check if the student exists - O(1)
        if (student == null) {
            System.out.println("Student not found. Transaction failed.");
            return;
        }

        // Retrieve the number of stocks available for the book - O(1)
        BookStock stock = bookInventory.get(bookId);

        // Check if the book is available - O(1)
        if (stock == null || stock.getQuantity() <= 0) {
            System.out.println("Book is currently not available. Transaction failed.");
            return;
        }

        // Create a new borrowing record that involves retrieving book using book ID, then decrease its quantity - O(1)
        BorrowRecord newRecord = new BorrowRecord(this, bookId);
        stock.decreaseQuantity();

        // Add the borrowing record to the student's borrowing history and print successful message - O(1)
        student.addBorrowRecord(newRecord);
        System.out.println("\nNew borrowing transaction recorded successfully.");
    }

    // Add a book to the inventory
    public void addBookToInventory(Book book) {

        // If book is already in inventory, just increment its quantity
        if (bookInventory.containsKey(book.getBookId())) {
            bookInventory.get(book.getBookId()).increaseQuantity();

        // If not, add the book to the inventory and set its quantity as 1
        } else {
            bookInventory.put(
                    book.getBookId(),
                    new BookStock(book, 1)
            );
        }
    }

    // Retrieve a book from the inventory by using book ID
    public Book retrieveBook(String bookId) {
        BookStock stock = bookInventory.get(bookId);
        return (stock != null) ? stock.getBook() : null;
    }
}

/**
 * The main class to demonstrate the Smart Library System.
 */
public class SmartLibBorrowingOperations {
    public static void main(String[] args) {

        // Create the system
        SmartLibSystem system = new SmartLibSystem();

        // Add books
        Book b1 = new Book("B001", "The Great Gatsby");
        Book b2 = new Book("B002", "The Catcher in the Rye");
        Book b3 = new Book("B003", "To Kill a Mockingbird");
        system.addBookToInventory(b1);
        system.addBookToInventory(b2);
        system.addBookToInventory(b3);

        // Add students
        Student s1 = new Student("S1001", "Alice Tan");
        Student s2 = new Student("S1002", "John Lee");
        system.addStudent(s1);
        system.addStudent(s2);

        // Perform borrowing operations
        system.recordNewBorrowingTransaction("S1001", "B001");
        system.recordNewBorrowingTransaction("S1001", "B002");
        system.recordNewBorrowingTransaction("S1002", "B003");

        // Retrieve their borrowing history
        system.retrieveBorrowingHistory("S1001");
        system.retrieveBorrowingHistory("S1002");
    }
}
