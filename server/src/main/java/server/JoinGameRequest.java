package server;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
