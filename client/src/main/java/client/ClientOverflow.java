package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

import java.util.ArrayList;

public class ClientOverflow {
    public static void displayChessGame(ChessGame game, ChessGame.TeamColor team, ArrayList<ChessPosition> highlights) {
        ChessBoard board = game.getBoard();
        int row = team == ChessGame.TeamColor.BLACK ? 0 : 7;
        int rowDiff = team == ChessGame.TeamColor.BLACK ? 1 : -1;
        String rowS = team == ChessGame.TeamColor.WHITE ? "    a   b  c   d   e   f  g   h    " : "    h   g  f   e   d   c  b   a    ";
        rowS = EscapeSequences.SET_BG_COLOR_BLACK + rowS + EscapeSequences.RESET_BG_COLOR + "\n";
        String out = rowS;
        while (row >= 0 && row <= 7) {
            int col = team == ChessGame.TeamColor.BLACK ? 7 : 0;
            int colDiff = team == ChessGame.TeamColor.BLACK ? -1 : 1;
            out += EscapeSequences.SET_BG_COLOR_BLACK + " " + String.valueOf(row + 1) + " ";
            while (col >= 0 && col <= 7) {
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                if (highlights.contains(new ChessPosition(row + 1, col + 1))) {
                    out += ((row % 2) + col) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_DARK_GREEN : EscapeSequences.SET_BG_COLOR_GREEN;
                } else {
                    out += ((row % 2) + col) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                }
                if (piece == null) {
                    out += EscapeSequences.EMPTY;
                } else {
                    switch (piece.getPieceType()) {
                        case PAWN:
                            out += piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                            break;
                        case KNIGHT:
                            out += piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                            break;
                        case ROOK:
                            out += piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                            break;
                        case BISHOP:
                            out += piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                            break;
                        case KING:
                            out += piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                            break;
                        case QUEEN:
                            out += piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                            break;
                    }
                }
                col += colDiff;
            }
            out += EscapeSequences.SET_BG_COLOR_BLACK + " " + String.valueOf(row + 1) + " ";
            out += EscapeSequences.RESET_BG_COLOR + " \n";
            row += rowDiff;
        }
        out += rowS;
        System.out.println(out);
    }

    public static void handlePostLoginHelp() {
        System.out.println("create game <game name>\n");
        System.out.println("play game <game number> <color>\n");
        System.out.println("observe game <game number>\n");
        System.out.println("list games\n");
        System.out.println("logout\n");
        System.out.println("help\n");
    }

    public static Client.PostLoginPrompt translateInputPostLogin(String input) {
        if (input.contains("help")) {
            return Client.PostLoginPrompt.HELP;
        }
        if (input.contains("list games")) {
            return Client.PostLoginPrompt.LIST;
        }
        if (input.contains("play game")) {
            return Client.PostLoginPrompt.PLAY;
        }
        if (input.contains("logout")) {
            return Client.PostLoginPrompt.LOGOUT;
        }
        if (input.contains("observe game")) {
            return Client.PostLoginPrompt.OBSERVE;
        }
        if (input.contains("create game")) {
            return Client.PostLoginPrompt.CREATE;
        }
        return null;
    }

    public static Client.PreLoginPrompt translateInputPreLogin(String input) {
        if (input.contains("help")) {
            return Client.PreLoginPrompt.HELP;
        }
        if (input.contains("quit")) {
            return Client.PreLoginPrompt.QUIT;
        }
        if (input.contains("login")) {
            return Client.PreLoginPrompt.LOGIN;
        }
        if (input.contains("register")) {
            return Client.PreLoginPrompt.REGISTER;
        }
        return null;
    }

    public static Client.PlayingPrompt translateInputPlaying(String input) {
        if (input.contains("help")) {
            return Client.PlayingPrompt.HELP;
        }
        if (input.contains("redraw chess board")) {
            return Client.PlayingPrompt.REDRAW;
        }
        if (input.contains("make move")) {
            return Client.PlayingPrompt.MOVE;
        }
        if (input.contains("resign")) {
            return Client.PlayingPrompt.RESIGN;
        }
        if (input.contains("highlight legal moves")) {
            return Client.PlayingPrompt.HIGHLIGHT;
        }
        if (input.contains("leave")) {
            return Client.PlayingPrompt.LEAVE;
        }
        return null;
    }
}
