package dev.orisha.user_service.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.orisha.user_service.config.AppConfig;
import dev.orisha.user_service.dto.requests.LoginRequest;
import dev.orisha.user_service.dto.responses.ApiResponse;
import dev.orisha.user_service.dto.responses.errors.ApiErrorResponse;
import dev.orisha.user_service.dto.responses.LoginResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collection;

import static dev.orisha.user_service.controllers.constants.ApplicationUrls.BASE_AUTH_URL;
import static dev.orisha.user_service.controllers.constants.ApplicationUrls.LOGIN_URL;
import static dev.orisha.user_service.handlers.constants.ErrorConstants.AUTHENTICATION_ERROR_MESSAGE;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final AppConfig appConfig;

    @Autowired
    public CustomAuthenticationFilter(final ObjectMapper mapper,
                                      final AuthenticationManager authenticationManager,
                                      final AppConfig appConfig) {
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.appConfig = appConfig;
        super.setAuthenticationManager(authenticationManager);
        super.setFilterProcessesUrl("%s%s".formatted(BASE_AUTH_URL, LOGIN_URL));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
                                                                throws AuthenticationException {
        log.info("Starting user authentication");
        LoginRequest loginRequest;
        try(InputStream inputStream = request.getInputStream()) {
            loginRequest = mapper.readValue(inputStream, LoginRequest.class);
            if (loginRequest == null || loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                throw new AuthenticationCredentialsNotFoundException("Login details is null or empty: %s".formatted(loginRequest));
            }
        } catch (Exception e) {
            log.error(AUTHENTICATION_ERROR_MESSAGE, e);
            throw new AuthenticationCredentialsNotFoundException(AUTHENTICATION_ERROR_MESSAGE);
        }

        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authResult = authenticationManager.authenticate(authentication);
        log.info("Retrieved the authentication result from authentication manager");
        return authResult;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
                                                        throws IOException, ServletException {
        String token = generateAccessToken(authResult);
        Cookie cookie = createCookie(token);
        response.addCookie(cookie);

        SecurityContextHolder.getContext().setAuthentication(authResult);
        log.info("User '{}' authentication successful", authResult.getName());

        LoginResponse loginResponse = buildLoginResponse(token);
        ApiResponse<LoginResponse> apiResponse = new ApiResponse<>(now(), true, loginResponse);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getOutputStream().write(mapper.writeValueAsBytes(apiResponse));
        response.flushBuffer();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException exception) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        log.info("User authentication unsuccessful");

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .responseTime(now())
                .isSuccessful(false)
                .error("UnsuccessfulAuthentication")
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getOutputStream().write(mapper.writeValueAsBytes(errorResponse));
        response.getOutputStream().flush();
    }

    private String generateAccessToken(Authentication authResult) {
        Algorithm algorithm = Algorithm.HMAC512(appConfig.getSecretKey());
        Instant now = Instant.now();
        String principal = authResult.getName();
        return JWT.create()
                .withIssuer("orisha.dev")
                .withIssuedAt(now)
                .withExpiresAt(now.plus(24, HOURS))
                .withSubject(principal)
                .withArrayClaim("authorities", extractAuthorities(authResult.getAuthorities()))
                .withClaim("principal", principal)
                .sign(algorithm);
    }

    private String[] extractAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return authorities
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    private static LoginResponse buildLoginResponse(String token) {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setMessage("Login successful");
        return loginResponse;
    }

    private static Cookie createCookie(String token) {
        Cookie cookie = new Cookie("orisha.dev", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400);
        cookie.setSecure(true);
//        cookie.setDomain("http://localhost:3000");
        return cookie;
    }
}
