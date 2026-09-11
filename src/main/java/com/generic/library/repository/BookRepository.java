package com.generic.library.repository;

import com.generic.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository  extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
}
