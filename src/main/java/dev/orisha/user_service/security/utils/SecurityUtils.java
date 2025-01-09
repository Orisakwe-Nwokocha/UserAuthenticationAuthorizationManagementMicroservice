package dev.orisha.user_service.security.utils;

import java.util.List;

import static dev.orisha.user_service.controllers.constants.ApplicationUrls.*;

public class SecurityUtils {

    private SecurityUtils() {}

    public static final String JWT_PREFIX = "Bearer ";

    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            "%s%s".formatted(BASE_AUTH_URL, REGISTER_URL),
            "%s%s".formatted(BASE_AUTH_URL, LOGIN_URL)
//                "/users/api/v1/auth/register",
//                "/users/api/v1/auth/login"
    );

//    public static final String[] ADMIN_AUTH_ENDPOINTS = {
//
//    };
//
//    public static final String[] USER_AUTH_ENDPOINTS = {
//
//    };

}
