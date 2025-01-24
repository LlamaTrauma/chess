package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessQueenMovementRules extends ChessPieceMovementRule {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int dr = 1;
        int dc = 0;
        for (char i = 0; i < 4; i ++){
            for(char j = 0; j < 2; j ++) {
                moves.addAll(checkLine(board, myPosition, color, dr, dc));
                dr = dr - dc;
                dc = dr + dc + dc;
            }
            dr >>= 1;
            dc >>= 1;
        }
        return moves;
    }
}
