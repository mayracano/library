package com.generic.library.controller;

import com.generic.library.model.Role;
import com.generic.library.model.User;
import com.generic.library.repository.LoanRepository;
import com.generic.library.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserControlIerIT extends BaseIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoanRepository loanRepository;
    private String librarianToken;
    private String memberToken;

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
        userRepository.deleteAll();
    }

    @Test
    public void shouldReturnForbiddenWhenAddingUserWithoutToken() {
        User user = getUser(2);
        webTestClient.post()
                .uri("/api/users")
                .bodyValue(user)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void shouldReturnForbiddenWhenAddingUserAsMemberRole() {
        User user = getUser(2);

        webTestClient.post()
                .uri("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + memberToken)
                .bodyValue(user)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void createUserAsLibrarianSuccessfully() {
        User user = getUser(2);

        webTestClient.post()
                .uri("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + librarianToken)
                .bodyValue(user)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .consumeWith(response -> {
                    User userResponse = response.getResponseBody();
                    assertThat(userResponse).isNotNull();
                    assertThat(userResponse.getId()).isNotNull();
                    assertThat(userResponse.getFirstName()).isEqualTo("name 2");
                    assertThat(userResponse.getLastName()).isEqualTo("last name 2");
                    assertThat(userResponse.getEmail()).isEqualTo("test2@email.com");
                });

        assertThat(userRepository.count()).isEqualTo(3);
    }

    @Test
    public void getAllUsersSuccessfully(){
        createUsersForGetAllTest();

        webTestClient.get()
                .uri("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + librarianToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .hasSize(4)
                .consumeWith(response -> {
                    List<User> usersResponse = response.getResponseBody();
                    assert usersResponse != null;
                    assertThat(usersResponse).isNotNull();
                    assertThat(usersResponse.get(2).getFirstName()).isEqualTo("name 2");
                    assertThat(usersResponse.get(2).getLastName()).isEqualTo("last name 2");
                    assertThat(usersResponse.get(2).getEmail()).isEqualTo("test2@email.com");
                    assertThat(usersResponse.get(3).getFirstName()).isEqualTo("name 3");
                    assertThat(usersResponse.get(3).getLastName()).isEqualTo("last name 3");
                    assertThat(usersResponse.get(3).getEmail()).isEqualTo("test3@email.com");
                });
    }

    @Test
    public void shouldReturnForbiddenWhenGetAllBooksWithoutToken() {
        createUsersForGetAllTest();
        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isForbidden();
    }

    private void createUsersForGetAllTest() {
        User user2 = getUser(2);
        userRepository.save(user2);

        User user3 = getUser(3);
        userRepository.save(user3);
    }

    private User getUser(int number) {
        User user = new User();
        user.setFirstName(String.format("name %s", number));
        user.setLastName(String.format("last name %s", number));
        user.setEmail(String.format("test%s@email.com", number));
        user.setRole(Role.MEMBER);
        user.setPassword(String.format("password%s", number));
        return user;
    }
}