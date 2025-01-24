package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class ChessPieceMovementRule {
    // returns true if:
    //   a position is on the chess board AND
    //   the position is not occupied by a piece of the same color
    public static boolean positionIsNotBlocked(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        if (!position.isOnBoard()) {
            return false;
        }
        ChessPiece pieceAtPosition = board.getPiece(position);
        if (pieceAtPosition != null && pieceAtPosition.getTeamColor() == color) {
            return false;
        }
        return true;
    }

    // returns true if:
    //   a position is on the chess board AND
    //   the position is occupied by a piece of a different color
    public static boolean positionIsCapturable(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        if (!position.isOnBoard()) {
            return false;
        }
        ChessPiece pieceAtPosition = board.getPiece(position);
        // SHUT UP INTELLIJ
        if (pieceAtPosition != null && pieceAtPosition.getTeamColor() != color) {
            return true;
        }
        return false;
    }

    public static Collection<ChessMove> checkLine(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor color, int dc, int dr) {
        Collection<ChessMove> moves = new ArrayList<>();
        int dist = 1;
        ChessPosition p = new ChessPosition(
                myPosition.getRow() + dr * dist,
                myPosition.getColumn() + dc * dist);
        while(positionIsNotBlocked(board, p, color)) {
            moves.add(new ChessMove(myPosition, p, null));
            if(positionIsCapturable(board, p, color)) {
                break;
            }
            dist += 1;
            p = new ChessPosition(
                    myPosition.getRow() + dr * dist,
                    myPosition.getColumn() + dc * dist);
        }
        return moves;
    }
}
