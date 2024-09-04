package com.bsn.booksphere.book;


import com.bsn.booksphere.common.PageResponse;
import com.bsn.booksphere.file.FileStorageService;
import com.bsn.booksphere.history.BookTransactionHistory;
import com.bsn.booksphere.history.BookTransactionHistoryRepository;
import com.bsn.booksphere.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookServiceTest {
    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookTransactionHistoryRepository transactionHistoryRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private Authentication authentication;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(authentication.getPrincipal()).thenReturn(user);
    }


    @Test
    void save() {
        // Arrange
        BookRequest request = mock(BookRequest.class);
        when(request.id()).thenReturn(1);
        when(request.title()).thenReturn("Test Title");
        when(request.isbn()).thenReturn("1234567890");
        when(request.authorName()).thenReturn("Author Name");
        when(request.synopsis()).thenReturn("Test Synopsis");
        when(request.shareable()).thenReturn(true);

        Book book = mock(Book.class);
        when(bookMapper.toBook(request)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(book.getId()).thenReturn(1);

        // Act
        Integer bookId = bookService.save(request, authentication);

        // Assert
        assertNotNull(bookId);
        assertEquals(1, bookId);
        verify(bookRepository).save(book);
    }


    @Test
    void findById() {
        // Arrange
        Book book = mock(Book.class);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        BookResponse response = mock(BookResponse.class);
        when(bookMapper.toBookResponse(book)).thenReturn(response);

        // Act
        BookResponse result = bookService.findById(1);

        // Assert
        assertNotNull(result);
        assertEquals(response, result);
    }


    @Test
    void findAllBooks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        Page<Book> booksPage = mock(Page.class);
        when(booksPage.getNumber()).thenReturn(0);
        when(booksPage.getSize()).thenReturn(10);
        when(booksPage.getTotalElements()).thenReturn(1L);
        when(booksPage.getTotalPages()).thenReturn(1);
        when(booksPage.isFirst()).thenReturn(true);
        when(booksPage.isLast()).thenReturn(true);
        when(bookRepository.findAllDisplayableBooks(pageable, user.getId())).thenReturn(booksPage);

        Book book = mock(Book.class);
        BookResponse response = mock(BookResponse.class);
        when(bookMapper.toBookResponse(book)).thenReturn(response);
        when(booksPage.stream()).thenReturn(List.of(book).stream());

        // Act
        PageResponse<BookResponse> result = bookService.findAllBooks(0, 10, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        verify(bookRepository).findAllDisplayableBooks(pageable, user.getId());
    }

//    @Test
//    void findAllBooksByOwner() {
//        // Arrange
//        // Crear el mock del usuario
//        User user = mock(User.class);
//        when(authentication.getPrincipal()).thenReturn(user);
//        when(user.getId()).thenReturn(1);
//
//        // Crear libros mockeados
//        Book book1 = mock(Book.class);
//        Book book2 = mock(Book.class);
//
//        // Crear BookResponse mockeados
//        BookResponse response1 = new BookResponse();
//        BookResponse response2 = new BookResponse();
//
//        // Crear una página mockeada con libros
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
//        Page<Book> booksPage = mock(Page.class);
//        when(booksPage.getNumber()).thenReturn(0);
//        when(booksPage.getSize()).thenReturn(10);
//        when(booksPage.getTotalElements()).thenReturn(2L);
//        when(booksPage.getTotalPages()).thenReturn(1);
//        when(booksPage.isFirst()).thenReturn(true);
//        when(booksPage.isLast()).thenReturn(true);
//        when(booksPage.stream()).thenReturn(Stream.of(book1, book2));
//
//        // Configurar el mock del repositorio para que devuelva la página mockeada
//        when(bookRepository.findAll(withOwnerId(1), pageable)).thenReturn(booksPage);
//
//        // Configurar el mock del mapper para convertir libros a respuestas
//        when(bookMapper.toBookResponse(book1)).thenReturn(response1);
//        when(bookMapper.toBookResponse(book2)).thenReturn(response2);
//
//        // Act
//        PageResponse<BookResponse> result = bookService.findAllBooksByOwner(0, 10, authentication);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(2, result.getContent().size());
//        assertTrue(result.getContent().contains(response1));
//        assertTrue(result.getContent().contains(response2));
//        assertEquals(0, result.getNumber());
//        assertEquals(10, result.getSize());
//        assertEquals(2, result.getTotalElements());
//        assertEquals(1, result.getTotalPages());
//        assertTrue(result.isFirst());
//        assertTrue(result.isLast());
//
//        // Verificar que el repositorio fue llamado con los parámetros correctos
//        verify(bookRepository).findAll(withOwnerId(1), pageable);
//    }
//
//
//

    @Test
    void updateShareableStatus() {
        // Arrange
        Book book = mock(Book.class);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(book.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(1);
        when(book.isShareable()).thenReturn(true);

        // Act
        Integer bookId = bookService.updateShareableStatus(1, authentication);

        // Assert
        assertNotNull(bookId);
        assertEquals(1, bookId);
        verify(bookRepository).save(book);
        verify(book).setShareable(false);
    }

    @Test
    void updateArchivedStatus() {
        // Arrange
        Book book = mock(Book.class);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(book.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(1);
        when(book.isArchived()).thenReturn(false);

        // Act
        Integer bookId = bookService.updateArchivedStatus(1, authentication);

        // Assert
        assertNotNull(bookId);
        assertEquals(1, bookId);
        verify(bookRepository).save(book);
        verify(book).setArchived(true);
    }

    @Test
    void borrowBook() {
        // Arrange
        Book book = mock(Book.class);
        User borrower = mock(User.class);
        User bookOwner = mock(User.class);

        // Configura el libro y los usuarios en la prueba
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(book.isArchived()).thenReturn(false);
        when(book.isShareable()).thenReturn(true);
        when(book.getOwner()).thenReturn(bookOwner);
        when(bookOwner.getId()).thenReturn(2);

        when(authentication.getPrincipal()).thenReturn(borrower);
        when(borrower.getId()).thenReturn(1);

        when(transactionHistoryRepository.isAlreadyBorrowedByUser(1, 1)).thenReturn(false);
        when(transactionHistoryRepository.isAlreadyBorrowed(1)).thenReturn(false);
        BookTransactionHistory history = mock(BookTransactionHistory.class);
        when(transactionHistoryRepository.save(any(BookTransactionHistory.class))).thenReturn(history);
        when(history.getId()).thenReturn(1);

        // Act
        Integer transactionId = bookService.borrowBook(1, authentication);

        // Assert
        assertNotNull(transactionId);
        assertEquals(1, transactionId);
    }

    @Test
    void returnBorrowedBook() {
        // Arrange
        Book book = mock(Book.class);
        User user = mock(User.class); // Usuario que ha tomado el libro
        User bookOwner = mock(User.class); // Propietario del libro

        // Configura el libro y el usuario en la prueba
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(book.isArchived()).thenReturn(false);
        when(book.isShareable()).thenReturn(true);
        when(book.getOwner()).thenReturn(bookOwner);
        when(bookOwner.getId()).thenReturn(2); // ID del propietario del libro

        when(authentication.getPrincipal()).thenReturn(user);
        when(user.getId()).thenReturn(1); // ID del usuario que está intentando devolver el libro

        BookTransactionHistory history = mock(BookTransactionHistory.class);
        when(transactionHistoryRepository.findByBookIdAndUserId(1, 1)).thenReturn(Optional.of(history));
        when(transactionHistoryRepository.save(any(BookTransactionHistory.class))).thenReturn(history);
        when(history.getId()).thenReturn(1);

        // Act
        Integer transactionId = bookService.returnBorrowedBook(1, authentication);

        // Assert
        assertNotNull(transactionId);
        assertEquals(1, transactionId);
        verify(history).setReturned(true);
    }


    @Test
    void approveReturnBorrowedBook() {
        // Arrange
        Book book = mock(Book.class);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(book.isArchived()).thenReturn(false);
        when(book.isShareable()).thenReturn(true);
        when(book.getOwner()).thenReturn(user);
        when(user.getId()).thenReturn(2);

        BookTransactionHistory history = mock(BookTransactionHistory.class);
        when(transactionHistoryRepository.findByBookIdAndOwnerId(1, 2)).thenReturn(Optional.of(history));
        when(transactionHistoryRepository.save(any(BookTransactionHistory.class))).thenReturn(history);
        when(history.getId()).thenReturn(1);

        // Act
        Integer transactionId = bookService.approveReturnBorrowedBook(1, authentication);

        // Assert
        assertNotNull(transactionId);
        assertEquals(1, transactionId);
        verify(history).setReturnApproved(true);
    }

    @Test
    void uploadBookCoverPicture() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        Book book = mock(Book.class);
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(fileStorageService.saveFile(file, 1, user.getId())).thenReturn("/path/to/cover");

        // Act
        bookService.uploadBookCoverPicture(file, authentication, 1);

        // Assert
        verify(bookRepository).save(book);
        verify(book).setBookCover("/path/to/cover");
    }

    @Test
    void findAllBorrowedBooks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> historyPage = mock(Page.class);
        when(historyPage.getNumber()).thenReturn(0);
        when(historyPage.getSize()).thenReturn(10);
        when(historyPage.getTotalElements()).thenReturn(1L);
        when(historyPage.getTotalPages()).thenReturn(1);
        when(historyPage.isFirst()).thenReturn(true);
        when(historyPage.isLast()).thenReturn(true);
        when(transactionHistoryRepository.findAllBorrowedBooks(pageable, user.getId())).thenReturn(historyPage);

        BookTransactionHistory history = mock(BookTransactionHistory.class);
        BorrowedBookResponse response = mock(BorrowedBookResponse.class);
        when(bookMapper.toBorrowedBookResponse(history)).thenReturn(response);
        when(historyPage.stream()).thenReturn(List.of(history).stream());

        // Act
        PageResponse<BorrowedBookResponse> result = bookService.findAllBorrowedBooks(0, 10, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        verify(transactionHistoryRepository).findAllBorrowedBooks(pageable, user.getId());
    }


    @Test
    void findAllReturnedBooks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> historyPage = mock(Page.class);
        when(historyPage.getNumber()).thenReturn(0);
        when(historyPage.getSize()).thenReturn(10);
        when(historyPage.getTotalElements()).thenReturn(1L);
        when(historyPage.getTotalPages()).thenReturn(1);
        when(historyPage.isFirst()).thenReturn(true);
        when(historyPage.isLast()).thenReturn(true);
        when(transactionHistoryRepository.findAllReturnedBooks(pageable, user.getId())).thenReturn(historyPage);

        BookTransactionHistory history = mock(BookTransactionHistory.class);
        BorrowedBookResponse response = mock(BorrowedBookResponse.class);
        when(bookMapper.toBorrowedBookResponse(history)).thenReturn(response);
        when(historyPage.stream()).thenReturn(List.of(history).stream());

        // Act
        PageResponse<BorrowedBookResponse> result = bookService.findAllReturnedBooks(0, 10, authentication);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
        verify(transactionHistoryRepository).findAllReturnedBooks(pageable, user.getId());
    }
}