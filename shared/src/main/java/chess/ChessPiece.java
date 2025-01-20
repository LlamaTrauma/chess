package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor color;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    // valid pawn promotions
    public static final List<PieceType> pawnPromotions = new ArrayList<PieceType>() {
        {
            add(PieceType.QUEEN);
            add(PieceType.BISHOP);
            add(PieceType.KNIGHT);
            add(PieceType.ROOK);
        }
    };

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case PAWN:
                return pawnMoves(board, myPosition);
            case KNIGHT:
                return knightMoves(board, myPosition);
            case ROOK:
                return rookMoves(board, myPosition);
            case BISHOP:
                return bishopMoves(board, myPosition);
            case KING:
                return kingMoves(board, myPosition);
            case QUEEN:
                return queenMoves(board, myPosition);
            default:
                // this should not happen
                throw new RuntimeException("Attempting to calculate moves for invalid piece type: " + type);
        }
    }

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int direction = color == ChessGame.TeamColor.WHITE ? 1 : -1;
        boolean isOnStartRow = myPosition.getRow() == (color == ChessGame.TeamColor.WHITE ? 2 : 7);
        boolean isBeforeEndRow = myPosition.getRow() == (color == ChessGame.TeamColor.WHITE ? 7 : 2);

        // simplest condition - check if moving one row forward is valid
        ChessPosition vertical = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        boolean canMoveVertical = positionIsNotBlocked(board, vertical) && !positionIsCapturable(board, vertical);

        if (canMoveVertical) {
            if (isBeforeEndRow) {
                pawnPromotions.forEach(promotion -> moves.add(new ChessMove(myPosition, vertical, promotion)));
            } else {
                moves.add(new ChessMove(myPosition, vertical, null));
            }
            // if can move a single row and hasn't moved yet, must check two row opener
            if (isOnStartRow) {
                ChessPosition vertical2 = new ChessPosition(myPosition.getRow() + direction * 2, myPosition.getColumn());
                if (positionIsNotBlocked(board, vertical2) && !positionIsCapturable(board, vertical2)) {
                    moves.add(new ChessMove(myPosition, vertical2, null));
                }
            }
        }

        // check for diagonal captures
        for (int c = -1; c <= 1; c += 2) {
            ChessPosition capture = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + c);
            if (positionIsCapturable(board, capture)) {
                if (isBeforeEndRow) {
                    pawnPromotions.forEach(promotion -> moves.add(new ChessMove(myPosition, capture, promotion)));
                } else {
                    moves.add(new ChessMove(myPosition, capture, null));
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for(int d1 = -1; d1 <= 1; d1 += 2) {
            for(int d2 = -2; d2 <= 2; d2 += 4) {
                ChessPosition p1 = new ChessPosition(myPosition.getRow() + d1, myPosition.getColumn() + d2);
                if (positionIsNotBlocked(board, p1)) {
                    moves.add(new ChessMove(myPosition, p1, null));
                }
                ChessPosition p2 = new ChessPosition(myPosition.getRow() + d2, myPosition.getColumn() + d1);
                if (positionIsNotBlocked(board, p2)) {
                    moves.add(new ChessMove(myPosition, p2, null));
                }
            }
        }
        return moves;
    }

    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = 0; i < 4; i ++){
            int dr = i & 1;
            int dc = 1 - dr;
            int dir = ((i >> 1) > 0) ? 1 : -1;
            int dist = 1;
            ChessPosition p = new ChessPosition(
            myPosition.getRow() + dr * dir * dist,
            myPosition.getColumn() + dc * dir * dist);
            while(positionIsNotBlocked(board, p)) {
                moves.add(new ChessMove(myPosition, p, null));
                if (positionIsCapturable(board, p)) {
                    break;
                }
                dist += 1;
                p = new ChessPosition(
                myPosition.getRow() + dr * dist * dir,
                myPosition.getColumn() + dc * dist * dir);
            }
        }
        return moves;
    }

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = 0; i < 4; i ++){
            int dr = (i & 1) == 1 ? 1 : -1;
            int dc = (i & 2) == 2 ? 1 : -1;
            int dist = 1;
            ChessPosition p = new ChessPosition(
                    myPosition.getRow() + dr * dist,
                    myPosition.getColumn() + dc * dist);
            while(positionIsNotBlocked(board, p)) {
                moves.add(new ChessMove(myPosition, p, null));
                if(positionIsCapturable(board, p)) {
                    break;
                }
                dist += 1;
                p = new ChessPosition(
                        myPosition.getRow() + dr * dist,
                        myPosition.getColumn() + dc * dist);
            }
        }
        return moves;
    }

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = 0; i < 8; i ++){
            // gotta be a more elegant way to do this but this works?
            // could reduce it to boolean logic tables but shrug
            // honestly just a lookup table is probably faster, whatever
            int b1 = i & 1;
            int b2 = i & 2;
            boolean b3 = (i >> 2) > 0;
            int dr = b1 * (b2 - 1);
            int dc = (1 - b1) * (b2 - 1);
            if (b3) {
                dr = dr - dc;
                dc = dr + dc + dc;
            }
            ChessPosition p = new ChessPosition(
                    myPosition.getRow() + dr,
                    myPosition.getColumn() + dc);
            if(positionIsNotBlocked(board, p)) {
                moves.add(new ChessMove(myPosition, p, null));
            }
        }
        return moves;
    }

    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int i = 0; i < 8; i ++){
            // gotta be a more elegant way to do this but this works?
            // could reduce it to boolean logic tables but shrug
            // honestly just a lookup table is probably faster, whatever
            int b1 = i & 1;
            int b2 = i & 2;
            boolean b3 = (i >> 2) > 0;
            int dr = b1 * (b2 - 1);
            int dc = (1 - b1) * (b2 - 1);
            if (b3) {
                dr = dr - dc;
                dc = dr + dc + dc;
            }
            int dist = 1;
            ChessPosition p = new ChessPosition(
                    myPosition.getRow() + dr * dist,
                    myPosition.getColumn() + dc * dist);
            while(positionIsNotBlocked(board, p)) {
                moves.add(new ChessMove(myPosition, p, null));
                if(positionIsCapturable(board, p)) {
                    break;
                }
                dist += 1;
                p = new ChessPosition(
                        myPosition.getRow() + dr * dist,
                        myPosition.getColumn() + dc * dist);
            }
        }
        return moves;
    }

    // returns true if:
    //   a position is on the chess board AND
    //   the position is not occupied by a piece of the same color
    public boolean positionIsNotBlocked(ChessBoard board, ChessPosition position) {
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
    public boolean positionIsCapturable(ChessBoard board, ChessPosition position) {
        if (!position.isOnBoard()) {
            return false;
        }
        ChessPiece pieceAtPosition = board.getPiece(position);
        if (pieceAtPosition != null && pieceAtPosition.getTeamColor() != color) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
