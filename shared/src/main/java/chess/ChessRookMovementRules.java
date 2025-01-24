package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessRookMovementRules extends ChessPieceMovementRule {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int dr = 1;
        int dc = 0;
        for (int i = 0; i < 4; i ++){
            dr = dr - dc;
            dc = dr + dc;
            dr = dr - dc;
            moves.addAll(checkLine(board, myPosition, color, dr, dc));
        }
        return moves;
    }
}
