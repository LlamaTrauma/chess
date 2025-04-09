package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    public final ChessGame game;
    public final String blackUsername;

    public LoadGameMessage(ChessGame game, String blackUsername) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.blackUsername = blackUsername;
    }
}
