package dev.orisha.user_service.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.orisha.user_service.dto.responses.errors.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static dev.orisha.user_service.handlers.constants.ErrorConstants.*;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Autowired
    public CustomAccessDeniedHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        int status = response.getStatus();
        String contentType = response.getContentType();
        log.info("AccessDeniedHandler - Response status: {}, Content-Type: {}", status, contentType);

        boolean isResponseSet = status != SC_OK && contentType != null;
        if (isResponseSet) {
            log.info(HTTP_RESPONSE_ALREADY_SET);
            response.flushBuffer();
            return;
        }

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(SC_FORBIDDEN);

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setType(DEFAULT_TYPE);
        errorResponse.setTitle(FORBIDDEN.getReasonPhrase());
        errorResponse.setStatus(SC_FORBIDDEN);
        errorResponse.setDetail("%s: You do not have permission to access this resource".formatted(accessDeniedException.getMessage()));
        errorResponse.setPath(request.getRequestURI());
        errorResponse.setMessage(String.format(MESSAGE_KEY, SC_FORBIDDEN));

        log.error("Access denied error: {}", errorResponse);

        response.getOutputStream().write(objectMapper.writeValueAsBytes(errorResponse));
        response.getOutputStream().flush();
    }

}
