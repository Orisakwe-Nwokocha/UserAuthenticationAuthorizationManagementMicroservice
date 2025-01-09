package dev.orisha.user_service.handlers.constants;

public final class ErrorConstants {

    public static final String PROBLEM_BASE_URL = "https://www.orisha.dev/problem";
    public static final String DEFAULT_TYPE = String.format("%s%s", PROBLEM_BASE_URL, "/problem-with-message");
    public static final String CONSTRAINT_VIOLATION_TYPE = String.format("%s%s", PROBLEM_BASE_URL, "/constraint-violation");
    public static final String MESSAGE_KEY = "error.http.%s";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String METHOD_ARGUMENT_NOT_VALID = "Method argument not valid";
    public static final String ACCESS_DENIED = "Access is denied";
    public static final String AUTHENTICATION_ERROR_MESSAGE = "Invalid username or password";

//    public static final String METHOD_ARGUMENT_TYPE_MISMATCH = "Method argument type mismatch";
//        public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";

    private ErrorConstants() {}

}
