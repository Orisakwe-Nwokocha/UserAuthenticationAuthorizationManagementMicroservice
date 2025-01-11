package dev.orisha.user_service.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.orisha.user_service.config.AppConfig;
import dev.orisha.user_service.dto.responses.errors.ErrorResponse;
import dev.orisha.user_service.security.services.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import static dev.orisha.user_service.handlers.constants.ErrorConstants.NO_STATIC_RESOURCE_PATH_FOUND;
import static dev.orisha.user_service.handlers.constants.ErrorConstants.NO_STATIC_RESOURCE_PATH_FOUND;
import static dev.orisha.user_service.security.utils.SecurityUtils.JWT_PREFIX;
import static dev.orisha.user_service.security.utils.SecurityUtils.PUBLIC_ENDPOINTS;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final AppConfig appConfig;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final ObjectMapper objectMapper;


    @Autowired
    public CustomAuthorizationFilter(final AuthService authService,
                                     final AppConfig appConfig,
                                     final RequestMappingHandlerMapping requestMappingHandlerMapping,
                                     final ObjectMapper objectMapper) {
        this.authService = authService;
        this.appConfig = appConfig;
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("Starting authorization");
        String requestPath = request.getRequestURI();
        boolean isRequestPathPublic = PUBLIC_ENDPOINTS.contains(requestPath);
        if (isRequestPathPublic) {
            log.info("Authorization not needed for public endpoint: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(JWT_PREFIX)) {
            String token = authorizationHeader.substring(JWT_PREFIX.length()).strip();
            if (isTokenInvalid(response, token)) {
                return;
            }
        } else {
            log.info("Authorization header not provided for request: {}", requestPath);
            validateRequestPath(requestPath, request.getMethod(), response);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isTokenInvalid(HttpServletResponse response, String token) throws IOException {
        if (isTokenBlacklisted(response, token)) {
            return true;
        }
        return isUnAuthorized(token, response);
    }

    private boolean isTokenBlacklisted(HttpServletResponse response, String token) throws IOException {
        if (authService.isTokenBlacklisted(token)) {
            log.warn("Token is blacklisted: {}", token);
            sendErrorResponse(response);
            return true;
        }
        return false;
    }

    private boolean isUnAuthorized(String token, HttpServletResponse response) throws IOException {
        log.info("Verifying JWT token");
        Algorithm algorithm = Algorithm.HMAC512(appConfig.getSecretKey());
        DecodedJWT decodedJWT;
        try {
            JWTVerifier jwtVerifier = JWT.require(algorithm)
                    .withIssuer("orisha.dev")
                    .withClaimPresence("principal")
                    .withClaimPresence("authorities")
                    .build();

            decodedJWT = jwtVerifier.verify(token);
        } catch (JWTVerificationException exception) {
            log.error("JWT verification failed: {}", exception.getMessage());
            sendErrorResponse(response);
            return true;
        }
        List<? extends GrantedAuthority> authorities = decodedJWT.getClaim("authorities").asList(SimpleGrantedAuthority.class);
        String principal = decodedJWT.getClaim("principal").asString();

        log.info("JWT token verified for: {}", principal);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User '{}' authorization succeeded", principal);
        return false;
    }

    private void sendErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"" + "Token is expired or invalid" + "\"}");
        response.getWriter().flush();
    }

    private void validateRequestPath(String requestPath, String method, HttpServletResponse response) throws IOException {
        RequestMethod requestMethod = RequestMethod.valueOf(method);
        log.info("Validating request path: '{}' with request method: '{}'", requestPath, requestMethod);
        RequestMappingInfo mappingInfo = RequestMappingInfo.paths(requestPath).methods(requestMethod).build();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        Set<RequestMappingInfo> requestMappingInfos = handlerMethods.keySet();

        boolean isRequestPathValid = requestMappingInfos.contains(mappingInfo);
        if (!isRequestPathValid) {
            String errorMessage = NO_STATIC_RESOURCE_PATH_FOUND.formatted(requestPath);
            log.info(errorMessage);

            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setTimestamp(now());
            errorResponse.setStatus(SC_NOT_FOUND);
            errorResponse.setError(NOT_FOUND.getReasonPhrase());
            errorResponse.setDetail(errorMessage);
            errorResponse.setPath(requestPath);

            response.setStatus(SC_NOT_FOUND);
            response.setContentType(APPLICATION_JSON_VALUE);
            response.getOutputStream().write(objectMapper.writeValueAsBytes(errorResponse));
        }
    }

}
