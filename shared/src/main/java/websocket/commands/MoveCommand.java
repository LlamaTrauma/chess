package websocket.commands;

import chess.ChessMove;
import chess.ChessPosition;

public class MoveCommand extends UserGameCommand {
    private final ChessMove move;
    public MoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.CONNECT, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}
