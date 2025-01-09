package dev.orisha.user_service.exceptions;

public class EmailExistsException extends AppBaseException {
    public EmailExistsException(String message) {
        super(message);
    }
}
