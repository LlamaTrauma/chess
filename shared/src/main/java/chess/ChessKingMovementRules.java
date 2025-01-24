package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessKingMovementRules extends ChessPieceMovementRule {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int dr = 1;
        int dc = 0;
        for (int i = 0; i < 4; i ++) {
            for (int j = 0; j < 2; j++) {
                ChessPosition p = new ChessPosition(
                        myPosition.getRow() + dr,
                        myPosition.getColumn() + dc);
                if (positionIsNotBlocked(board, p, color)) {
                    moves.add(new ChessMove(myPosition, p, null));
                }
                dr = dr - dc;
                dc = dr + dc + dc;
            }
            dr >>= 1;
            dc >>= 1;
        }
        return moves;
    }
}
