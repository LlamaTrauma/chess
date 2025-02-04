package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessBishopMovementRules extends ChessPieceMovementRule {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int dr = 1;
        int dc = 1;
        for (int i = 0; i < 4; i ++){
            dr -= dc;
            dc = dr + dc;
            dr = dr - dc;
            moves.addAll(checkLine(board, myPosition, color, dr, dc));
        }
        return moves;
    }
}
