package com.generic.library.controller;

import com.generic.library.config.JwtService;
import com.generic.library.dto.LoanRequest;
import com.generic.library.model.*;
import com.generic.library.repository.BookRepository;
import com.generic.library.repository.LoanRepository;
import com.generic.library.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class LoanControllerIT extends BaseIntegrationTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;
    private String memberToken;

    @BeforeEach
    public void setup() {
        super.setUpBase();
        User user = new User();
        user.setFirstName("name 1");
        user.setLastName("last name 1");
        user.setEmail("test1@email.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.MEMBER);
        userRepository.save(user);
        memberToken = jwtService.generateToken(Map.of("role", "MEMBER"),"test1@email.com");
    }

    @AfterEach
    void tearDown() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateLoan() {
        Book book = new Book();
        book.setAuthor("Author A");
        book.setIsbn("2133124343");
        book.setAvailableCopies(2);
        book.setTitle("Book A");
        bookRepository.save(book);
        log.info("Book created with id {}", book.getId());

        User user = new User();
        user.setEmail("test@email.com");
        user.setLastName("last name");
        user.setFirstName("first name");
        user.setPassword("password");
        user.setRole(Role.MEMBER);
        userRepository.save(user);
        log.info("User created with id {}", user.getId());

        LoanRequest loanRequest = new LoanRequest(user.getId(), book.getId());

        webTestClient.post()
                .uri("/api/loans")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + memberToken)
                .bodyValue(loanRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Loan.class)
                .consumeWith(response -> {
                    Loan loanResponse = response.getResponseBody();
                    assertThat(loanResponse).isNotNull();
                    assertThat(loanResponse.getId()).isNotNull();
                    assertThat(loanResponse.getBook().getId()).isNotNull();
                    assertThat(loanResponse.getBook().getAuthor()).isEqualTo("Author A");
                    assertThat(loanResponse.getBook().getIsbn()).isEqualTo("2133124343");
                    assertThat(loanResponse.getBook().getAvailableCopies()).isEqualTo(1);
                    assertThat(loanResponse.getBook().getTitle()).isEqualTo("Book A");
                    assertThat(loanResponse.getUser().getEmail()).isEqualTo("test@email.com");
                    assertThat(loanResponse.getUser().getLastName()).isEqualTo("last name");
                    assertThat(loanResponse.getUser().getFirstName()).isEqualTo("first name");
                    assertThat(loanResponse.getLoanDate()).isEqualTo(LocalDate.now());
                });
        assertThat(loanRepository.count()).isEqualTo(1);
    }

    @Test
    public void returnBookSuccessfully() {
        Book book = new Book();
        book.setAuthor("Author A");
        book.setIsbn("2133124343");
        book.setAvailableCopies(1);
        book.setTitle("Book A");
        bookRepository.save(book);

        User user = new User();
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setEmail("myTest@email.com");
        user.setPassword("password");
        user.setRole(Role.MEMBER);
        userRepository.save(user);

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(LocalDate.now());
        loan.setReturnDate(null);
        loan.setStatus(LoanStatus.ACTIVE);
        loanRepository.save(loan);

        webTestClient.put()
                .uri("/api/loans/{id}/return", loan.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + memberToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Loan.class)
                .consumeWith(response -> {
                    Loan loanResponse = response.getResponseBody();
                    assertThat(loanResponse).isNotNull();
                    assertThat(loanResponse.getId()).isNotNull();
                    assertThat(loanResponse.getBook().getId()).isNotNull();
                    assertThat(loanResponse.getBook().getAuthor()).isEqualTo("Author A");
                    assertThat(loanResponse.getBook().getIsbn()).isEqualTo("2133124343");
                    assertThat(loanResponse.getBook().getAvailableCopies()).isEqualTo(2);
                    assertThat(loanResponse.getBook().getTitle()).isEqualTo("Book A");
                    assertThat(loanResponse.getUser().getFirstName()).isEqualTo("first name");
                    assertThat(loanResponse.getUser().getLastName()).isEqualTo("last name");
                    assertThat(loanResponse.getLoanDate()).isEqualTo(LocalDate.now());
                    assertThat(loanResponse.getReturnDate()).isEqualTo(LocalDate.now());
                    assertThat(loanResponse.getStatus()).isEqualTo(LoanStatus.RETURNED);
                });
    }
}

