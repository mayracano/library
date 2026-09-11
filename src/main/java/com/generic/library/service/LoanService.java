package com.generic.library.service;

import com.generic.library.exception.BookNoCopiesException;
import com.generic.library.exception.DuplicateResourceException;
import com.generic.library.exception.ResourceNotFoundException;
import com.generic.library.model.Book;
import com.generic.library.model.Loan;
import com.generic.library.model.LoanStatus;
import com.generic.library.model.User;
import com.generic.library.repository.BookRepository;
import com.generic.library.repository.LoanRepository;
import com.generic.library.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public Loan createLoan(Long userId, Long bookId) {
        log.info("Attempting to create a loan for user {} and book {}", userId, bookId);
        Book book =bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (book.getAvailableCopies() <= 0) {
            log.warn("Book has no available copies");
            throw new BookNoCopiesException("Book has not enough copies");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        log.info("Book stock updated. Remaining copies for ID {}: {}", book.getId(), book.getAvailableCopies());

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setReturnDate(null);
        loan.setLoanDate(LocalDate.now());
        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        log.info("Attempting to process return for loanId {}", loanId);

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> {
                    log.warn("Loan returned by ID {} not found", loanId);
                    return new ResourceNotFoundException("Book not found");
                });

        if (loan.getStatus() ==  LoanStatus.RETURNED) {
            log.warn("Return failed, the loan with ID {} was already returned", loanId);
            throw  new DuplicateResourceException("This loan was already returned");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        log.info("Loan ID {} successfully closed. set to RETURNED", loanId);
        return loanRepository.save(loan);
    }
}
