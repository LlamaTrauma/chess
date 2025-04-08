package service;

public class InvalidUserCommandError extends RuntimeException {
    public InvalidUserCommandError(String message) {
        super(message);
    }
}
