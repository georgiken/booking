package com.example.booking;

import com.example.booking.entity.User;
import com.example.booking.controller.UserController;
import com.example.booking.dto.RegisterRequest;
import com.example.booking.repository.UserRepository;
import com.example.booking.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

class UserControllerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");

        User user = new User();
        user.setName("testuser");
        user.setPassword("encodedpassword");
        user.setEmail("test@example.com");

        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("User registered successfully")));
    }

//    @Test
//    void testLoginSuccess() throws Exception {
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.setEmail("test@example.com");
//        loginRequest.setPassword("password");
//
//        User user = new User();
//        user.setEmail("test@example.com");
//        user.setPassword("encodedpassword");
//
//        when(userRepository.findByEmail(anyString())).thenReturn(user);
//        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
//        when(jwtTokenProvider.generateToken(anyString(), eq(3600L))).thenReturn("mockAccessToken");
//        when(jwtTokenProvider.generateToken(anyString(), eq(86400L))).thenReturn("mockRefreshToken");
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.accessToken", is("mockAccessToken")))
//                .andExpect(jsonPath("$.refreshToken", is("mockRefreshToken")));
//    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(401)))
                .andExpect(jsonPath("$.message", is("Unauthorized")))
                .andExpect(jsonPath("$.details", is("Invalid email or password")));
    }
}

