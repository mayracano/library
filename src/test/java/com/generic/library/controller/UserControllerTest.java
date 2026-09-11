package com.generic.library.controller;

import com.generic.library.config.JwtService;
import com.generic.library.model.Role;
import com.generic.library.model.User;
import com.generic.library.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JwtService jwtService;

    @Test
    public  void getAllUsersSuccessfully() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPassword("1234dfsjkfd");
        user.setRole(Role.MEMBER);

        when(userService.getAllUsers()).thenReturn(List.of(user));
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@email.com"))
                .andExpect(jsonPath("$[0].firstName").value("First"))
                .andExpect(jsonPath("$[0].lastName").value("Last"));
    }

    @Test
    public void addUserSuccessfully() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPassword("1234dfsjkfd");
        user.setRole(Role.MEMBER);

        when(userService.saveUser(user)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("First"))
                .andExpect(jsonPath("$.lastName").value("Last"))
                .andExpect(jsonPath("$.email").value("test@email.com"));
    }
}
