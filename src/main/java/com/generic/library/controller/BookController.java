package com.generic.library.controller;

import com.generic.library.model.Book;
import com.generic.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Book>> getAllBooks(){
        log.info("Get All Books Requested");
        List<Book> books =  bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    };

    @PreAuthorize("hasRole('LIBRARIAN')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        log.info("Add Book Requested");
        Book createBook = bookService.saveBook(book);
        return new ResponseEntity<>(createBook, HttpStatus.CREATED);
    }
}
