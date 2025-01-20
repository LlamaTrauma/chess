package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private int row;
    private int col;

    public final int MIN_ROW = 1;
    public final int MAX_ROW = 8;

    public final int MIN_COL = 1;
    public final int MAX_COL = 8;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean isOnBoard() {
        return row >= MIN_ROW && row <= MAX_ROW && col >= MIN_COL && col <= MAX_COL;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
