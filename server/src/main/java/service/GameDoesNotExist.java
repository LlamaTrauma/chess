package service;

public class GameDoesNotExist extends RuntimeException {
    public GameDoesNotExist(String message) {
        super(message);
    }
}
