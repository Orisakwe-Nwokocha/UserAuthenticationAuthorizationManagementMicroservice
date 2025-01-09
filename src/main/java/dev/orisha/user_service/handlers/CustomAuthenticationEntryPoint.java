package dev.orisha.user_service.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.orisha.user_service.dto.responses.errors.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static dev.orisha.user_service.handlers.constants.ErrorConstants.DEFAULT_TYPE;
import static dev.orisha.user_service.handlers.constants.ErrorConstants.MESSAGE_KEY;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Autowired
    public CustomAuthenticationEntryPoint(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(SC_UNAUTHORIZED);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setType(DEFAULT_TYPE);
        errorResponse.setTitle(UNAUTHORIZED.getReasonPhrase());
        errorResponse.setStatus(SC_UNAUTHORIZED);
        errorResponse.setDetail(authException.getMessage());
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setMessage(String.format(MESSAGE_KEY, SC_UNAUTHORIZED));

        log.error("Authentication error: {}", errorResponse);

        response.getOutputStream().write(objectMapper.writeValueAsBytes(errorResponse));
        response.getOutputStream().flush();
    }

}
