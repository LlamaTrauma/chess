package model;

import chess.ChessGame;

public class GameData {
    public GameMetadata metadata;
    public ChessGame game;

    public GameData(GameMetadata metadata, ChessGame game) {
        this.metadata = metadata;
        this.game = game;
    }
}
