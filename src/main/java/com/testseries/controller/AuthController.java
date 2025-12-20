package com.testseries.controller;

import com.testseries.dto.LoginRequest;
import com.testseries.dto.RegisterRequest;
import com.testseries.dto.UserResponse;
import com.testseries.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {
    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided credentials. Email must be unique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data or email already exists"),
            @ApiResponse(responseCode = "409", description = "User with this email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Parameter(description = "User registration details", required = true) @Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User login", description = "Authenticates a user with email and password credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @Parameter(description = "User login credentials", required = true) @Valid @RequestBody LoginRequest request) {
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
