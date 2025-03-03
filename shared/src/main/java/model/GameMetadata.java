package model;

import chess.ChessGame;

public class GameMetadata {
    public int gameID;
    public String whiteUsername;
    public String blackUsername;
    public String gameName;

    public GameMetadata(int gameID, String whiteUsername, String blackUsername, String gameName) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }
}
