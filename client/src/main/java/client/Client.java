package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.ListGamesResult;
import model.LoginResult;
import ui.EscapeSequences;

import java.util.HashMap;
import java.util.Map;

public class Client {
    public static enum PreLoginPrompt {
        HELP,
        QUIT,
        LOGIN,
        REGISTER
    }

    public static enum PostLoginPrompt {
        HELP,
        LOGOUT,
        CREATE,
        LIST,
        PLAY,
        OBSERVE
    }

    public static enum HandleInputReturnFlag {
        CONTINUE,
        QUIT,
        LOOP_PRE,
        LOOP_POST
    }

    private final ServerFacade facade = new ServerFacade(8080);

    private String username;
    private String authToken;
    private final Map<Integer, Integer> listedIDs = new HashMap<>();

    public String getUsername() {
        return username;
    }

    public Client() {}

    public HandleInputReturnFlag handlePreLoginInput(String input) {
        PreLoginPrompt prompt;
        try {
            String command = input.split("\\s+")[0];
            prompt = PreLoginPrompt.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            System.out.println("Invalid input\n");
            return HandleInputReturnFlag.CONTINUE;
        }
        switch (prompt) {
            case HELP:
                handlePreLoginHelp();
                return HandleInputReturnFlag.CONTINUE;
            case QUIT:
                System.out.println("goodbye");
                return HandleInputReturnFlag.QUIT;
            case LOGIN:
                if (handleLogin(input)) {
                    return HandleInputReturnFlag.LOOP_POST;
                } else {
                    return HandleInputReturnFlag.CONTINUE;
                }
            case REGISTER:
                if (handleRegister(input)) {
                    return HandleInputReturnFlag.LOOP_POST;
                } else {
                    return HandleInputReturnFlag.CONTINUE;
                }
            default:
                return HandleInputReturnFlag.QUIT;
        }
    }

    public boolean handleLogin(String input) {
        String[] args = input.split("\\s+");
        if (args.length < 3) {
            System.out.println("Too few arguments provided\n");
            return false;
        } else if (args.length > 3) {
            System.out.println("Too many arguments provided\n");
            return false;
        }
        String username = args[1];
        String password = args[2];
        try {
            LoginResult result = facade.loginRequest(username, password);
            this.username = result.username();
            authToken = result.authToken();
        } catch (RuntimeException e) {
            System.out.println("error registering");
            return false;
        }
        return true;
    }

    public boolean handleRegister(String input) {
        String[] args = input.split("\\s+");
        if (args.length < 4) {
            System.out.println("Too few arguments provided\n");
            return false;
        } else if (args.length > 4) {
            System.out.println("Too many arguments provided\n");
            return false;
        }
        String username = args[1];
        String password = args[2];
        String email = args[3];
        try {
            facade.registerRequest(username, password, email);
            this.username = username;
        } catch (RuntimeException e) {
            System.out.println("error registering");
            return false;
        }
        return true;
    }

    public void handlePreLoginHelp() {
        System.out.println("register <username> <password> <email>\n");
        System.out.println("login <username> <password>\n");
        System.out.println("quit\n");
        System.out.println("help\n");
    }

    public HandleInputReturnFlag handlePostLoginInput(String input) {
        PostLoginPrompt prompt;
        try {
            String command = input.split("\\s+")[0];
            prompt = PostLoginPrompt.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            System.out.println("Invalid input\n");
            return HandleInputReturnFlag.CONTINUE;
        }
        switch (prompt) {
            case HELP:
                handlePostLoginHelp();
                return HandleInputReturnFlag.CONTINUE;
            case LOGOUT:
                handleLogout();
                return HandleInputReturnFlag.LOOP_PRE;
            case CREATE:
                handleCreate(input);
                return HandleInputReturnFlag.CONTINUE;
            case PLAY:
                handlePlay(input);
                return HandleInputReturnFlag.CONTINUE;
            case LIST:
                handleList();
                return HandleInputReturnFlag.CONTINUE;
            case OBSERVE:
                handleObserve(input);
                return HandleInputReturnFlag.CONTINUE;
            default:
                return HandleInputReturnFlag.QUIT;
        }
    }

    public void handleObserve(String input) {
        try {
            String[] args = input.split("\\s+");
            if (args.length < 2) {
                System.out.println("No game specified");
                return;
            }
            int gameNum = Integer.parseInt(args[1]);
            Integer id = listedIDs.get(gameNum);
            if (id == null) {
                System.out.println("Game does not exist");
                return;
            }
            displayChessGame(new ChessGame(), ChessGame.TeamColor.WHITE);
        } catch (Exception e) {
            System.out.println("Failed to get game");
        }
    }

    public void handleList() {
        try {
            ListGamesResult games = facade.listRequest(authToken);
            listedIDs.clear();
            int i = 1;
            for (var game : games.games()) {
                System.out.println(String.valueOf(i) + ": " + game.gameName);
                listedIDs.put(i, game.gameID);
                i += 1;
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
            System.out.println("Failed to retrieve games list");
        }
    }

    public void handlePlay(String input) {
        String[] args = input.split("\\s+");
        if (args.length < 3) {
            System.out.println("Too few inputs");
            return;
        }
        String number = args[1];
        String color = args[2];
        ChessGame.TeamColor team = null;
        if (color.equalsIgnoreCase("white")) {
            team = ChessGame.TeamColor.WHITE;
        } else if (color.equalsIgnoreCase("black")) {
            team = ChessGame.TeamColor.BLACK;
        } else {
            System.out.println("Color must be white or black");
            return;
        }
        Integer id = null;
        try {
            id = listedIDs.get(Integer.parseInt(number));
        } catch (Exception e) {
            System.out.println("Couldn't parse number " + number);
        }
        if (id == null) {
            System.out.println("Game " + String.valueOf(id) + " does not exist");
            return;
        }
        try {
            facade.playRequest(authToken, color, id);
            displayChessGame(new ChessGame(), team);
        } catch (RuntimeException e) {
            System.out.println("Joining game failed");
        }
    }


    public void handleCreate(String input) {
        String[] args = input.split("\\s+");
        if (args.length < 2) {
            System.out.println("Too few inputs");
            return;
        }
        String name = args[1];
        try {
            facade.createRequest(authToken, name);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
            System.out.println("Creating game failed");
        }
    }

    public void handleLogout() {
        try {
            facade.logoutRequest(authToken);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    public void handlePostLoginHelp() {
        System.out.println("create <game name>\n");
        System.out.println("play <game number> <color>\n");
        System.out.println("observe <game number>\n");
        System.out.println("list\n");
        System.out.println("logout\n");
        System.out.println("help\n");
    }

    private void displayChessGame(ChessGame game, ChessGame.TeamColor team) {
        ChessBoard board = game.getBoard();
        int row = team == ChessGame.TeamColor.BLACK ? 0 : 7;
        int rowDiff = team == ChessGame.TeamColor.BLACK ? 1 : -1;
        String row_s = team == ChessGame.TeamColor.WHITE ? "    a   b  c   d   e   f  g   h    " : "    h   g  f   e   d   c  b   a    ";
        row_s = EscapeSequences.SET_BG_COLOR_BLACK + row_s + EscapeSequences.RESET_BG_COLOR + "\n";
        String out = row_s;
        while (row >= 0 && row <= 7) {
            int col = team == ChessGame.TeamColor.BLACK ? 7 : 0;
            int colDiff = team == ChessGame.TeamColor.BLACK ? -1 : 1;
            out += EscapeSequences.SET_BG_COLOR_BLACK + " " + String.valueOf(row + 1) + " ";
            while (col >= 0 && col <= 7) {
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                out += ((row % 2) + col) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
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
        out += row_s;
        System.out.println(out);
    }
}
