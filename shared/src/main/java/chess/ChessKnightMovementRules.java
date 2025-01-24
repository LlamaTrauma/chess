package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessKnightMovementRules extends ChessPieceMovementRule {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for(int d1 = -1; d1 <= 1; d1 += 2) {
            for(int d2 = -2; d2 <= 2; d2 += 4) {
                ChessPosition p1 = new ChessPosition(myPosition.getRow() + d1, myPosition.getColumn() + d2);
                if (positionIsNotBlocked(board, p1, color)) {
                    moves.add(new ChessMove(myPosition, p1, null));
                }
                ChessPosition p2 = new ChessPosition(myPosition.getRow() + d2, myPosition.getColumn() + d1);
                if (positionIsNotBlocked(board, p2, color)) {
                    moves.add(new ChessMove(myPosition, p2, null));
                }
            }
        }
        return moves;
    }
}
