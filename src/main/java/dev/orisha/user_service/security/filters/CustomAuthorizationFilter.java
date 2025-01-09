package dev.orisha.user_service.security.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.orisha.user_service.config.AppConfig;
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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static dev.orisha.user_service.security.utils.SecurityUtils.JWT_PREFIX;
import static dev.orisha.user_service.security.utils.SecurityUtils.PUBLIC_ENDPOINTS;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final AppConfig appConfig;

    @Autowired
    public CustomAuthorizationFilter(final AuthService authService, final AppConfig appConfig) {
        this.authService = authService;
        this.appConfig = appConfig;
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
            log.info("Authorization header not found");
//            sendErrorResponse(response);
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
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"" + "Token is expired or invalid" + "\"}");
        response.getWriter().flush();
    }

//    private boolean isUnAuthorized(String token, HttpServletResponse response) throws IOException {
//        Algorithm algorithm = Algorithm.RSA512(rsaKeys.publicKey(), rsaKeys.privateKey());
//        DecodedJWT decodedJWT;
//        try {
//            JWTVerifier jwtVerifier = JWT.require(algorithm)
//                    .withIssuer("orisha.dev")
//                    .withClaimPresence("roles")
//                    .withClaimPresence("principal")
//                    .withClaimPresence("credentials")
//                    .build();
//
//            decodedJWT = jwtVerifier.verify(token);
//        } catch (JWTVerificationException exception) {
//            log.error("JWT verification failed: {}", exception.getMessage());
//            sendErrorResponse(response);
//            return true;
//        }
//
//        List<? extends GrantedAuthority> authorities = decodedJWT.getClaim("roles")
//                .asList(SimpleGrantedAuthority.class);
//        String principal = decodedJWT.getClaim("principal").asString();
//        String credentials = decodedJWT.getClaim("credentials").asString();
//
////        UserDetails userDetails = userDetailsService.loadUserByUsername(principal);
////        Authentication authentication =
////                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        log.info("User '{}' authorization succeeded", principal);
//        return false;
//    }

}
