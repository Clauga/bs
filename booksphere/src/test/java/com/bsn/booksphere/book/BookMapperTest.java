package com.bsn.booksphere.book;


import com.bsn.booksphere.file.FileUtils;
import com.bsn.booksphere.history.BookTransactionHistory;
import com.bsn.booksphere.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookMapperTest {
/*in this test we are testing the BookMapper class, it means all the methods in the class,
so we need to mock all the dependencies of the class */

    private BookMapper mapper;
    private Book book;
    private User user;
    @BeforeEach
    void setUp() {
        mapper = new BookMapper();
        book = mock(Book.class);
        user = mock(User.class);

        // Config the mock objects
        when(book.getId()).thenReturn(1);
        when(book.getTitle()).thenReturn("Test Title");
        when(book.getAuthorName()).thenReturn("Author Name");
        when(book.getIsbn()).thenReturn("1234567890");
        when(book.getSynopsis()).thenReturn("Test Synopsis");
        when(book.getRate()).thenReturn(4.5);
        when(book.getOwner()).thenReturn(user);
        when(user.fullName()).thenReturn("John Doe");
        when(book.isShareable()).thenReturn(true);
        when(book.isArchived()).thenReturn(false);
        when(book.getBookCover()).thenReturn("/path/to/cover");
    }

    @Test
    void shouldMapBook() {
        // Arrange
        BookRequest request = mock(BookRequest.class);
        when(request.id()).thenReturn(1);
        when(request.title()).thenReturn("Test Title");
        when(request.isbn()).thenReturn("1234567890");
        when(request.authorName()).thenReturn("Author Name");
        when(request.synopsis()).thenReturn("Test Synopsis");
        when(request.shareable()).thenReturn(true);


        // Assert
        assertNotNull(book);
        assertEquals(1, book.getId().longValue());
        assertEquals("Test Title", book.getTitle());
        assertEquals("1234567890", book.getIsbn());
        assertEquals("Author Name", book.getAuthorName());
        assertEquals("Test Synopsis", book.getSynopsis());
        assertFalse(book.isArchived());
        assertTrue(book.isShareable());
    }

    @Test
    void shouldMapBookResponse() {
        // Arrange
        when(book.getId()).thenReturn(1);
        when(book.getTitle()).thenReturn("Test Title");
        when(book.getAuthorName()).thenReturn("Author Name");
        when(book.getIsbn()).thenReturn("1234567890");
        when(book.getSynopsis()).thenReturn("Test Synopsis");
        when(book.getRate()).thenReturn(4.5);
        when(book.isArchived()).thenReturn(false);
        when(book.isShareable()).thenReturn(true);
        when(book.getOwner()).thenReturn(user);
        when(user.fullName()).thenReturn("John Doe");
        when(book.getBookCover()).thenReturn("/path/to/cover");

        // Act
        BookResponse response = mapper.toBookResponse(book);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Test Title", response.getTitle());
        assertEquals("Author Name", response.getAuthorName());
        assertEquals("1234567890", response.getIsbn());
        assertEquals("Test Synopsis", response.getSynopsis());
        assertEquals(4.5, response.getRate());
        assertFalse(response.isArchived());
        assertTrue(response.isShareable());
        assertEquals("John Doe", response.getOwner());
        assertEquals(FileUtils.readFileFromLocation("/path/to/cover"), response.getCover());
    }

    @Test
    void shouldMapBorrowedBookResponse() {
        // Arrange

        when(book.getId()).thenReturn(1);
        when(book.getTitle()).thenReturn("Test Title");
        when(book.getAuthorName()).thenReturn("Author Name");
        when(book.getIsbn()).thenReturn("1234567890");
        when(book.getRate()).thenReturn(4.5);

        BookTransactionHistory history = mock(BookTransactionHistory.class);
        when(history.getBook()).thenReturn(book);
        when(history.isReturned()).thenReturn(true);
        when(history.isReturnApproved()).thenReturn(false);

        // Act
        BorrowedBookResponse response = mapper.toBorrowedBookResponse(history);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Test Title", response.getTitle());
        assertEquals("Author Name", response.getAuthorName());
        assertEquals("1234567890", response.getIsbn());
        assertEquals(4.5, response.getRate());
        assertTrue(response.isReturned());
        assertFalse(response.isReturnApproved());
    }
}