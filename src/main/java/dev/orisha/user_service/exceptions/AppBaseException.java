package dev.orisha.user_service.exceptions;

public abstract class AppBaseException extends RuntimeException {
    public AppBaseException(String message){
        super(message);
    }
}
