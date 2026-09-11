package com.generic.library.service;

import com.generic.library.exception.DuplicateResourceException;
import com.generic.library.model.Book;
import com.generic.library.repository.BookRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        log.info("Finding all books");
        return bookRepository.findAll();
    }

    @Transactional
    public Book saveBook(Book book) {
        bookRepository.findByIsbn(book.getIsbn()).ifPresent(existingBook -> {
            throw new DuplicateResourceException("Book with ISBN " + book.getIsbn() + " already exists");
        });
        Book savedBook = bookRepository.save(book);
        log.info("Book with isbn {} saved with ID  {}", savedBook.getIsbn(), savedBook.getId());
        return savedBook;
    }
}
