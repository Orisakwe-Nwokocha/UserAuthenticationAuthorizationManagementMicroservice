package dev.orisha.user_service.security.services;

import dev.orisha.user_service.dto.requests.RegisterRequest;
import dev.orisha.user_service.dto.responses.ApiResponse;
import dev.orisha.user_service.dto.responses.RegisterResponse;

public interface AuthService {

    ApiResponse<RegisterResponse> register(RegisterRequest request);

    void blacklist(String token);

    boolean isTokenBlacklisted(String token);

}
