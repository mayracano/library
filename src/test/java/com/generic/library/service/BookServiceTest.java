package com.generic.library.service;

import com.generic.library.exception.DuplicateResourceException;
import com.generic.library.model.Book;
import com.generic.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    public void saveBookDuplicateResource() {
        String isbn = "123456789";
        Book existingBook = new Book();
        existingBook.setIsbn(isbn);

        Book newBook = new Book();
        newBook.setIsbn(isbn);

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(existingBook));
        assertThrows(DuplicateResourceException.class, () -> bookService.saveBook(newBook));
    }

    @Test
    public void getBookByIdSuccessfully() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");
        book.setAuthor("Author 1");
        book.setAvailableCopies(2);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setAvailableCopies(3);
    }

    @Test
    public void getAllBooksSuccessfully() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");
        book.setAuthor("Author 1");
        book.setAvailableCopies(2);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setAvailableCopies(3);

        when(bookRepository.findAll()).thenReturn(List.of(book, book2));
        List<Book> books = bookService.getAllBooks();
        assertTrue(books.size() == 2);
        assertTrue(books.get(0).getTitle().equals("Book 1"));
        assertTrue(books.get(1).getTitle().equals("Book 2"));
    }

    @Test
    public void saveBookSuccessfully() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");
        book.setAuthor("Author 1");
        book.setAvailableCopies(2);
        book.setIsbn("123456789");

        when(bookRepository.findByIsbn("123456789")).thenReturn(Optional.empty());
        when(bookRepository.save(book)).thenReturn(book);
        Book savedBook = bookService.saveBook(book);
        assertTrue(savedBook.getTitle().equals("Book 1"));
        assertTrue(savedBook.getAuthor().equals("Author 1"));
        assertTrue(savedBook.getAvailableCopies() == 2);
        assertTrue(savedBook.getIsbn().equals("123456789"));
    }
}
