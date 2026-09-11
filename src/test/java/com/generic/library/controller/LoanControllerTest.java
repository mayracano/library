package com.generic.library.controller;

import com.generic.library.config.JwtService;
import com.generic.library.dto.LoanRequest;
import com.generic.library.model.*;
import com.generic.library.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(LoanController.class)
public class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private LoanService loanService;
    @MockitoBean
    private JwtService jwtService;

    @Test
    public void createLoanSuccessfullyTest() throws Exception {
        LoanRequest loanRequest = new LoanRequest(1L, 1L);
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Title");
        book.setAvailableCopies(1);
        book.setAuthor("Author");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setRole(Role.MEMBER);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setLoanDate(LocalDate.now());

        when(loanService.createLoan(loanRequest.userId(), loanRequest.bookId())).thenReturn(loan);
        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.book.id").value(1L))
                .andExpect(jsonPath("$.book.author").value("Author"))
                .andExpect(jsonPath("$.book.title").value("Title"))
                .andExpect(jsonPath("$.book.availableCopies").value(1))
                .andExpect(jsonPath("$.status").value(LoanStatus.ACTIVE.toString()))
                .andExpect(jsonPath("$.loanDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.firstName").value("FirstName"))
                .andExpect(jsonPath("$.user.lastName").value("LastName"))
                .andExpect(jsonPath("$.user.email").value("test@email.com"));
    }

    @Test
    public void returnBookSuccessfullyTest() throws Exception {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setBook(new Book());
        loan.setUser(new User());
        loan.setStatus(LoanStatus.RETURNED);
        when(loanService.returnBook(1L)).thenReturn(loan);

        mockMvc.perform(put("/api/loans/1/return")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
