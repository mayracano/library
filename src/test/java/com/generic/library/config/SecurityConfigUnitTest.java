package com.generic.library.config;

import com.generic.library.model.Role;
import com.generic.library.model.User;
import com.generic.library.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityConfigUnitTest {

    @Mock private UserRepository userRepository;
    @Mock private JwtAuthenticationFilter jwtAuthenticationFilter;

    @InjectMocks private SecurityConfig securityConfig;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        UserDetailsService userDetailsService = securityConfig.userDetailsService(userRepository);

        User mockUser = new User();
        mockUser.setEmail("admin@biblioteca.com");
        mockUser.setPassword("password_encripted");
        mockUser.setRole(Role.LIBRARIAN);

        when(userRepository.findByEmail("admin@biblioteca.com")).thenReturn(Optional.of(mockUser));
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin@biblioteca.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("admin@biblioteca.com");
        assertThat(userDetails.getPassword()).isEqualTo("password_encripted");
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_LIBRARIAN");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UserDetailsService userDetailsService = securityConfig.userDetailsService(userRepository);
        when(userRepository.findByEmail("notfound@biblioteca.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("notfound@biblioteca.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found in database: notfound@biblioteca.com");
    }
}
