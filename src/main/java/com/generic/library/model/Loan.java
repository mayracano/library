package com.generic.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name="loans")
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name="user_id", nullable = false)
    @NotNull(message = "User is Required")
    private User user;

    @ManyToOne()
    @JoinColumn(name="book_id", nullable = false)
    @NotNull(message = "Book is Required")
    private Book book;

    @Column(name="loan_date", nullable = false)
    @NotNull(message="Loan Date is Required")
    private LocalDate loanDate;

    @Column(name="return_date")
    private LocalDate returnDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;
}
