package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import requestmodel.ListGamesResult;
import requestmodel.LoginResult;

import java.util.Map;

public class Client {
    public static enum preLoginPrompt {
        HELP,
        QUIT,
        LOGIN,
        REGISTER
    }

    public static enum postLoginPrompt {
        HELP,
        LOGOUT,
        CREATE,
        LIST,
        PLAY,
        OBSERVE
    }

    public static enum handleInputReturnFlag {
        CONTINUE,
        QUIT,
        LOOP_PRE,
        LOOP_POST
    }

    private final ServerFacade facade = new ServerFacade();

    private String username;
    private String authToken;
    private Map<Integer, Integer> listedIDs;

    public String getUsername() {
        return username;
    }

    public Client() {}

    public handleInputReturnFlag handlePreLoginInput(String input) {
        preLoginPrompt prompt;
        try {
            String command = input.split("\\s+")[0];
            prompt = preLoginPrompt.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            System.out.println("Invalid input\n");
            return handleInputReturnFlag.CONTINUE;
        }
        switch (prompt) {
            case HELP:
                handlePreLoginHelp();
                return handleInputReturnFlag.CONTINUE;
            case QUIT:
                System.out.println("goodbye");
                return handleInputReturnFlag.QUIT;
            case LOGIN:
                if (handleLogin(input)) {
                    return handleInputReturnFlag.LOOP_POST;
                } else {
                    return handleInputReturnFlag.CONTINUE;
                }
            case REGISTER:
                if (handleRegister(input)) {
                    return handleInputReturnFlag.LOOP_POST;
                } else {
                    return handleInputReturnFlag.CONTINUE;
                }
            default:
                return handleInputReturnFlag.QUIT;
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
            username = result.username();
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

    public handleInputReturnFlag handlePostLoginInput(String input) {
        postLoginPrompt prompt;
        try {
            String command = input.split("\\s+")[0];
            prompt = postLoginPrompt.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            System.out.println("Invalid input\n");
            return handleInputReturnFlag.CONTINUE;
        }
        switch (prompt) {
            case HELP:
                handlePostLoginHelp();
                return handleInputReturnFlag.CONTINUE;
            case LOGOUT:
                handleLogout();
                return handleInputReturnFlag.LOOP_PRE;
            case CREATE:
                handleCreate(input);
                return handleInputReturnFlag.CONTINUE;
            case PLAY:
                handlePlay(input);
                return handleInputReturnFlag.CONTINUE;
            case LIST:
                handleList();
                return handleInputReturnFlag.CONTINUE;
            case OBSERVE:
                handleObserve(input);
                return handleInputReturnFlag.CONTINUE;
            default:
                return handleInputReturnFlag.QUIT;
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
            }
        } catch (RuntimeException e) {
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
        int row = team == ChessGame.TeamColor.WHITE ? 0 : 7;
        int rowDiff = team == ChessGame.TeamColor.WHITE ? 1 : -1;
        int col = team == ChessGame.TeamColor.BLACK ? 0 : 7;
        int colDiff = team == ChessGame.TeamColor.BLACK ? 1 : -1;
        String out = "";
        while (row >= 0 && row <= 7) {
            while (col >= 0 && col <= 7) {
                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                if (piece == null) {
                    out += " ";
                } else {
                    switch (piece.getPieceType()) {
                        case PAWN:
                            out += team == ChessGame.TeamColor.WHITE ? "P" : "p";
                            break;
                        case KNIGHT:
                            out += team == ChessGame.TeamColor.WHITE ? "N" : "n";
                            break;
                        case ROOK:
                            out += team == ChessGame.TeamColor.WHITE ? "R" : "r";
                            break;
                        case BISHOP:
                            out += team == ChessGame.TeamColor.WHITE ? "B" : "b";
                            break;
                        case KING:
                            out += team == ChessGame.TeamColor.WHITE ? "K" : "k";
                            break;
                        case QUEEN:
                            out += team == ChessGame.TeamColor.WHITE ? "Q" : "q";
                            break;
                    }
                }
                col += colDiff;
            }
            out += '\n';
            row += rowDiff;
        }
        System.out.println(out);
    }
}
