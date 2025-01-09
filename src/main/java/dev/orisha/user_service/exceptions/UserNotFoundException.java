package dev.orisha.user_service.exceptions;

public class UserNotFoundException extends AppBaseException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
