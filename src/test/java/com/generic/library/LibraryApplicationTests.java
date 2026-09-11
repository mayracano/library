package com.generic.library;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class LibraryApplicationTests {

    @Test
    void mainStartsSpringApplication() {
        String[] args = {"--spring.main.web-application-type=none"};

        try (MockedStatic<org.springframework.boot.SpringApplication> mocked = mockStatic(org.springframework.boot.SpringApplication.class)) {
            LibraryApplication.main(args);

            mocked.verify(() -> org.springframework.boot.SpringApplication.run(LibraryApplication.class, args));
        }
    }
}
