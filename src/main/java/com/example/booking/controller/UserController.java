package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.entity.User;
import com.example.booking.repository.UserRepository;
import com.example.booking.security.JwtTokenProvider;
import com.example.booking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная регистрация", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Пользователь с такой почтой уже существует", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        log.info("Register request received: {}", registerRequest);
        Optional<User> user = userService.getUserByEmail(registerRequest.getEmail());
        try {
            if (user.isPresent()) {
                log.warn("User already exists with email: {}", registerRequest.getEmail());
                return ResponseEntity.badRequest().body(new ErrorResponse(400, "Bad Request", "User already exists"));
            }

            User newUser = userService.createUser(registerRequest);
            log.info("User created: {}", newUser);
            String accessToken = jwtTokenProvider.generateToken(newUser.getName(), 3600);
            log.info("JWT token generated: {}", accessToken);

            TokenResponse token = new TokenResponse();
            token.setAccessToken(accessToken);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            log.error("Error during user registration", e);
            return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal Server Error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Вход в аккаунт и получение wt токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Неверная почта или пароль", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login request received: {}", loginRequest);
            Optional<User> user = userService.getUserByEmail(loginRequest.getEmail());

            if (user.isEmpty()) {
                log.warn("User does not exist with email: {}", loginRequest.getEmail());
                return ResponseEntity.status(401).body(new ErrorResponse(401, "Unauthorized", "Invalid email"));
            }
            log.info("User found: {}", user.get());
            User logUser = user.get();
            if (!passwordEncoder.matches(loginRequest.getPassword(), logUser.getPassword())) {
                log.warn("Password does not match");
                return ResponseEntity.status(401).body(new ErrorResponse(401, "Unauthorized", "Invalid password"));
            }
            log.info("Login successful");
            String accessToken = jwtTokenProvider.generateToken(logUser.getEmail(), 3600);

            TokenResponse token = new TokenResponse();
            token.setAccessToken(accessToken);

            return ResponseEntity.ok(token);
        } catch (Exception e) {
            log.error("Error during user login", e);
            return ResponseEntity.status(500).body(new ErrorResponse(500, "Internal Server Error", e.getMessage()));
        }
    }

}
