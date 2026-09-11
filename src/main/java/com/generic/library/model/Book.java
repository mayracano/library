package com.generic.library.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name="books")
@Data
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Author is required")
    @Column(nullable = false)
    private String author;

    @NotNull(message = "ISBN is required")
    @Column(nullable = false,  unique = true)
    private String isbn;

    @NotNull(message = "Available Copies quantity is required")
    @Min(value = 0, message = "Available Copies cannot be negative")
    @Column(name="available_copies", nullable = false)
    private Integer availableCopies;
}
