package com.generic.library.config;

import com.generic.library.controller.BookController;
import com.generic.library.model.Book;
import com.generic.library.repository.UserRepository;
import com.generic.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(SecurityConfig.class)
public class SecurityConfigIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUpMocks() {
        when(jwtService.extractUsername(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn("admin@biblioteca.com");

        when(jwtService.isTokenValid(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(true);

        when(jwtService.extractRole(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn("LIBRARIAN");
    }


    @Test
    void shouldAllowMemberAccessToGetBooks() throws Exception {
        mockMvc.perform(get("/api/books")
                        .with(user("admin").roles("MEMBER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAnonymousAccessToCreateBook() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"test\",\"author\":\"Author a\",\"isbn\":\"1234567\",\"availableCopies\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowLibrarianToCreateBook() throws Exception {
        Book mockSavedBook = new Book();
        mockSavedBook.setId(1L);
        mockSavedBook.setTitle("Clean Code");
        mockSavedBook.setAuthor("Bob");
        mockSavedBook.setIsbn("123");
        mockSavedBook.setAvailableCopies(1);

        org.mockito.Mockito.when(bookService.saveBook(org.mockito.ArgumentMatchers.any(Book.class)))
                .thenReturn(mockSavedBook);

        mockMvc.perform(post("/api/books")
                        .with(user("admin").roles("LIBRARIAN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Clean Code\",\"author\":\"Bob\",\"isbn\":\"123\",\"availableCopies\":1}"))
                .andExpect(status().isCreated()); // Responderá de forma exitosa en verde
    }


    @Test
    void shouldDenyNonLibrarianToCreateBook() throws Exception {
        mockMvc.perform(post("/api/books")
                        .with(user("member").roles("MEMBER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Clean Code\",\"author\":\"Bob\",\"isbn\":\"123\",\"availableCopies\":1}"))
                .andExpect(status().isForbidden());
    }
}
