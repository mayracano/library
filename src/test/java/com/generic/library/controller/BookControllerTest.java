package com.generic.library.controller;

import com.generic.library.config.JwtService;
import com.generic.library.model.Book;
import com.generic.library.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BookService bookService;
    @MockitoBean
    private JwtService jwtService;

    @Test
    public void getAllBooksSuccessfullyTest() throws Exception {

        Book book = new Book();
        book.setIsbn("123456789");
        book.setAuthor("Author");
        book.setTitle("Title");

        when(bookService.getAllBooks()).thenReturn(List.of(book));
        mockMvc.perform(get("/api/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("123456789"))
                .andExpect(jsonPath("$[0].author").value("Author"))
                .andExpect(jsonPath("$[0].title").value("Title"));

    }

    @Test
    public void addBookSuccessfullyTest() throws Exception {
        Book book = new Book();
        book.setIsbn("123456789");
        book.setAuthor("Author");
        book.setTitle("Title");
        book.setAvailableCopies(2);
        when(bookService.saveBook(book)).thenReturn(book);
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("123456789"))
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.title").value("Title"));
    }
}
