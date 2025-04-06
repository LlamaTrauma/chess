package websocket.commands;

import chess.ChessPosition;

public class MoveCommand extends UserGameCommand {
    private final ChessPosition start;
    private final ChessPosition end;
    public MoveCommand(String authToken, Integer gameID, ChessPosition start, ChessPosition end) {
        super(CommandType.CONNECT, authToken, gameID);
        this.start = start;
        this.end = end;
    }

    public ChessPosition getStart() {
        return start;
    }

    public ChessPosition getEnd() {
        return end;
    }
}
