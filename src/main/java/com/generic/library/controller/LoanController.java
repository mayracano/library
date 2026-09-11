package com.generic.library.controller;

import com.generic.library.dto.LoanRequest;
import com.generic.library.model.Loan;
import com.generic.library.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Slf4j
public class LoanController {

    private final LoanService loanService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Loan> createLoan(@Valid @RequestBody LoanRequest loanRequest) {
        log.info("Creating loan for userId {} and bookId {}", loanRequest.userId(), loanRequest.bookId());
        Loan loan = loanService.createLoan(loanRequest.userId(), loanRequest.bookId());
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/{id}/return")
    public ResponseEntity<Loan> returnBook(@PathVariable Long id) {
        log.info("Returning loan for bookId {}", id);
        Loan loan = loanService.returnBook(id);
        return new ResponseEntity<>(loan, HttpStatus.OK);
    }
}
