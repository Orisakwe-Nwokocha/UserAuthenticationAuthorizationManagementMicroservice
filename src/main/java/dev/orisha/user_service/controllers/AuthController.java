package dev.orisha.user_service.controllers;

import dev.orisha.user_service.dto.requests.RegisterRequest;
import dev.orisha.user_service.security.services.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static dev.orisha.user_service.controllers.constants.ApplicationUrls.*;
import static dev.orisha.user_service.security.utils.SecurityUtils.JWT_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_AUTH_URL)
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(final AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(REGISTER_URL)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("REST request to register user: {}", request);
        return ResponseEntity.status(CREATED).body(authService.register(request));
    }

    @DeleteMapping(LOGOUT_URL)
    public ResponseEntity<?> logout(@RequestHeader(AUTHORIZATION) String token) {
        log.info("Logout request received: {}", token);
        token = token.replace(JWT_PREFIX, "").strip();
        authService.blacklist(token);
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

}
