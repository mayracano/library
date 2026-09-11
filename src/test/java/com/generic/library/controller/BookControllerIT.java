package com.generic.library.controller;

import com.generic.library.model.Book;
import com.generic.library.model.Role;
import com.generic.library.model.User;
import com.generic.library.repository.BookRepository;
import com.generic.library.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class BookControllerIT extends BaseIntegrationTest {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;
    private String librarianToken;


    @BeforeEach
    void setUp() {
        super.setUpBase();
        User adminTest = new User();
        adminTest.setFirstName("Admin");
        adminTest.setLastName("Test");
        adminTest.setEmail("admin@biblioteca.com");
        adminTest.setPassword(passwordEncoder.encode("password123"));
        adminTest.setRole(Role.LIBRARIAN);
        userRepository.save(adminTest);

        librarianToken = jwtService.generateToken(Map.of("role", "LIBRARIAN"),"admin@biblioteca.com");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    public void addBookWithLibrarianTokenSuccessfully() {
        Book book = new Book();
        book.setAuthor("John Doe");
        book.setIsbn("123456789");
        book.setTitle("My Book");
        book.setAvailableCopies(2);


        webTestClient.post()
                .uri("/api/books")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + librarianToken)
                .bodyValue(book)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class)
                .consumeWith(response -> {
                    Book responseBook = response.getResponseBody();
                    assertThat(responseBook).isNotNull();
                    assertThat(responseBook.getId()).isNotNull();
                    assertThat(responseBook.getTitle()).isEqualTo("My Book");
                    assertThat(responseBook.getIsbn()).isEqualTo("123456789");
                    assertThat(responseBook.getAvailableCopies()).isEqualTo(2);
                });

        assertThat(bookRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldReturnForbiddenWhenAddingBookWithoutToken() {
        Book book = new Book();
        book.setTitle("Securing DevOps");
        book.setAuthor("Julian Vehent");
        book.setIsbn("978-1617294136");
        book.setAvailableCopies(1);

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(book)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void getAllBooksSuccessfully() {
        saveBooksForTest();
        webTestClient.get()
                .uri("/api/books")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + librarianToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Book.class)
                .hasSize(2)
                .consumeWith(response -> {
                    List<Book> responseBooks = response.getResponseBody();
                    assertThat(responseBooks).isNotNull();
                    assertThat(responseBooks).isNotEmpty();
                    assertThat(responseBooks.get(0).getTitle()).isEqualTo("Book One");
                    assertThat(responseBooks.get(1).getTitle()).isEqualTo("Book Two");
                });
    }

    @Test
    public void shouldReturnForbiddenWhenGetAllBooksWithoutToken() {
        saveBooksForTest();
        webTestClient.get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isForbidden();
    }

    private void saveBooksForTest() {
        Book book1 = new Book();
        book1.setTitle("Book One");
        book1.setAuthor("Author A");
        book1.setIsbn("1111");
        book1.setAvailableCopies(1);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Book Two");
        book2.setAuthor("Author B");
        book2.setIsbn("2222");
        book2.setAvailableCopies(2);
        bookRepository.save(book2);
    }
}
