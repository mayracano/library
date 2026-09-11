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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    @Test
    public void createLoanBookNoCopiesExceptionTest(){
        Book existingBook = new Book();
        existingBook.setAvailableCopies(0);

        User existingUser = new User();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        assertThrows(BookNoCopiesException.class, () -> loanService.createLoan(1L, 1L));
    }

    @Test
    public void createLoanNoResourceFoundBookExceptionTest(){
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> loanService.createLoan(1L, 1L));
    }

    @Test
    public void createLoanNoResourceFoundUserExceptionTest(){
        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> loanService.createLoan(1L, 1L));
    }

    @Test
    public void returnBookResourceNotFoundExceptionTest(){
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> loanService.returnBook(1L));
    }

    @Test
    public void returnBookLoanAlreadyReturned() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.RETURNED);
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        assertThrows(DuplicateResourceException.class, () -> loanService.returnBook(1L));
    }

    @Test
    public void createLoanSuccessfully(){
        Loan loan = new Loan();
        loan.setId(1L);
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");
        book.setAvailableCopies(2);
        User user = new User();
        user.setId(1L);
        loan.setBook(book);
        loan.setUser(user);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setLoanDate(LocalDate.now());

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan createdLoan = loanService.createLoan(1L, 1L);
        assertTrue(loan.getBook().getAvailableCopies() == 1);
        assertTrue(loan.getStatus().equals(LoanStatus.ACTIVE));
        assertTrue(createdLoan.getBook().getTitle().equals("Book 1"));
    }

    @Test
    public void returnLoanSuccessfully(){
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setStatus(LoanStatus.ACTIVE);
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");
        book.setAvailableCopies(2);
        loan.setBook(book);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan loanSaved = loanService.returnBook(1L);
        assertTrue(loanSaved.getBook().getAvailableCopies() == 3);
        assertTrue(loanSaved.getStatus().equals(LoanStatus.RETURNED));
    }
}
