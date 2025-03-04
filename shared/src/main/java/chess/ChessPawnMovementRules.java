package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessPawnMovementRules extends ChessPieceMovementRule {
    // valid pawn promotions
    public static final List<ChessPiece.PieceType> PAWN_PROMOTIONS = new ArrayList<ChessPiece.PieceType>() {
        {
            add(ChessPiece.PieceType.QUEEN);
            add(ChessPiece.PieceType.BISHOP);
            add(ChessPiece.PieceType.KNIGHT);
            add(ChessPiece.PieceType.ROOK);
        }
    };

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        boolean isOnStartRow = myPosition.getRow() == (color == ChessGame.TeamColor.WHITE ? 2 : 7);
        boolean isBeforeEndRow = myPosition.getRow() == (color == ChessGame.TeamColor.WHITE ? 7 : 2);

        // simplest condition - check if moving one row forward is valid
        ChessPosition vertical = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        boolean canMoveVertical = positionIsNotBlocked(board, vertical, color) && !positionIsCapturable(board, vertical, color);

        if (canMoveVertical) {
            if (isBeforeEndRow) {
                PAWN_PROMOTIONS.forEach(promotion -> moves.add(new ChessMove(myPosition, vertical, promotion)));
            } else {
                moves.add(new ChessMove(myPosition, vertical, null));
            }
            // if can move a single row and hasn't moved yet, must check two row opener
            // oh shut up intellij, you police me enough you've got no right to spellcheck my thoughts
            if (isOnStartRow) {
                ChessPosition vertical2 = new ChessPosition(myPosition.getRow() + direction * 2, myPosition.getColumn());
                if (positionIsNotBlocked(board, vertical2, color) && !positionIsCapturable(board, vertical2, color)) {
                    moves.add(new ChessMove(myPosition, vertical2, null));
                }
            }
        }

        // check for diagonal captures
        for (int c = -1; c <= 1; c += 2) {
            ChessPosition capture = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + c);
            if (positionIsCapturable(board, capture, color)) {
                if (isBeforeEndRow) {
                    PAWN_PROMOTIONS.forEach(promotion -> moves.add(new ChessMove(myPosition, capture, promotion)));
                } else {
                    moves.add(new ChessMove(myPosition, capture, null));
                }
            }
        }
        return moves;
    }
}
