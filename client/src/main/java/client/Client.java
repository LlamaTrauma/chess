package client;

import chess.*;
import com.google.gson.Gson;
import model.ListGamesResult;
import model.LoginResult;
import requestmodel.RegisterResult;
import ui.EscapeSequences;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;

import java.util.*;

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

    public static enum PlayingPrompt {
        HELP,
        REDRAW,
        LEAVE,
        MOVE,
        RESIGN,
        HIGHLIGHT
    }

    public static enum HandleInputReturnFlag {
        CONTINUE,
        QUIT,
        LOOP_PRE,
        LOOP_POST,
        LOOP_GAME
    }

    private final ServerFacade facade = new ServerFacade(8080);

    private String username;
    private String authToken;
    private final Map<Integer, Integer> listedIDs = new HashMap<>();

    private int gameID;
    public ChessGame currentGame;
    public ChessGame.TeamColor currentColor;

    public WebsocketClient ws;

    public String getUsername() {
        return username;
    }

    public Client() {
        ws = null;
    }

    private PlayingPrompt translateInputPlaying(String input) {
        return ClientOverflow.translateInputPlaying(input);
    }

    private PostLoginPrompt translateInputPostLogin(String input) {
        return ClientOverflow.translateInputPostLogin(input);
    }

    private PreLoginPrompt translateInputPreLogin(String input) {
        return ClientOverflow.translateInputPreLogin(input);
    }

    public HandleInputReturnFlag handlePreLoginInput(String input) {
        PreLoginPrompt prompt = translateInputPreLogin(input);
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
            case null:
                System.out.println("invalid input");
                return HandleInputReturnFlag.CONTINUE;
        }
    }

    public HandleInputReturnFlag handlePostLoginInput(String input) {
        PostLoginPrompt prompt = translateInputPostLogin(input);
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
                return handlePlay(input) ? HandleInputReturnFlag.LOOP_GAME : HandleInputReturnFlag.CONTINUE;
            case LIST:
                handleList();
                return HandleInputReturnFlag.CONTINUE;
            case OBSERVE:
                return handleObserve(input) ? HandleInputReturnFlag.LOOP_GAME : HandleInputReturnFlag.CONTINUE;
            case null:
                System.out.println("invalid input");
                return HandleInputReturnFlag.CONTINUE;
        }
    }

    public HandleInputReturnFlag handlePlayingInput(String input) {
        PlayingPrompt prompt = translateInputPlaying(input);
        switch (prompt) {
            case HELP:
                handlePlayingHelp();
                return HandleInputReturnFlag.CONTINUE;
            case REDRAW:
                handleRedraw();
                return HandleInputReturnFlag.CONTINUE;
            case MOVE:
                handleMove(input);
                return HandleInputReturnFlag.CONTINUE;
            case LEAVE:
                handleLeave();
                return HandleInputReturnFlag.LOOP_POST;
            case RESIGN:
                handleResign();
                return HandleInputReturnFlag.CONTINUE;
            case HIGHLIGHT:
                handleHighlight(input);
                return HandleInputReturnFlag.CONTINUE;
            case null:
                System.out.println("invalid input");
                return HandleInputReturnFlag.CONTINUE;
        }
    }

    public void createWebsocket() {
        try {
            ws = new WebsocketClient(8080, this);
            ws.send(new Gson().toJson(new ConnectCommand(authToken, gameID)));
        } catch (Exception e) {
            System.out.println("unable to start websocket client");
        }
    }

    public void handleLeave() {
        try {
            ws.send(new Gson().toJson(new LeaveCommand(authToken, gameID)));
        } catch (Exception e) {
            System.out.println("unable to connect to server");
        }
    }

    public void handleResign() {
        System.out.println("Are you sure you want to resign? (y/n): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (!input.isEmpty() && input.toLowerCase().charAt(0) == 'y') {
            try {
                ws.send(new Gson().toJson(new ResignCommand(authToken, gameID)));
            } catch (Exception e) {
                System.out.println("unable to connect to server");
            }
        }
    }

    public void handleHighlight(String input) {
        var inputs = input.split("\\s+");
        if (inputs.length < 4) {
            System.out.println("specify position");
            return;
        } else if (inputs.length > 4) {
            System.out.println("too many inputs");
            return;
        }
        ChessPosition highlightPos = inputToPosition(inputs[3]);
        if (highlightPos == null) {
            System.out.println("invalid position");
            return;
        }
        var legalMoves = currentGame.validMoves(highlightPos);
        ArrayList<ChessPosition> legalPositions = new ArrayList<>();
        for (var move : legalMoves) {
            legalPositions.add(move.getEndPosition());
        }
        displayChessGame(currentGame, currentColor, legalPositions);
    }

    public void handleRedraw() {
        if (currentGame == null) {
            System.out.println("Cannot redraw game");
        } else {
            displayChessGame(currentGame, currentColor);
        }
    }

    private ChessPosition inputToPosition(String input) {
        if (input.length() > 2) {
            return null;
        }
        int col = (int) input.charAt(0) - 'a' + 1;
        int row = (int) input.toLowerCase().charAt(1) - '0';
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return null;
        }
        return new ChessPosition(row, col);
    }

    public void handleMove(String input) {
        var inputs = input.split("\\s+");
        if (inputs.length < 4) {
            System.out.println("too few inputs");
            return;
        }
        ChessPosition start = inputToPosition(inputs[2]);
        ChessPosition end = inputToPosition(inputs[3]);
        if (start == null || end == null) {
            System.out.println("invalid positions");
            return;
        }
        ChessPiece.PieceType promotion = null;
        if (currentGame.getBoard().getPiece(start).getPieceType() == ChessPiece.PieceType.PAWN && (end.getRow() == 1 || end.getRow() == 8)) {
            if (inputs.length < 5) {
                System.out.println("specify promotion type");
                return;
            } else if (inputs.length > 5) {
                System.out.println("too many arguments");
                return;
            }
            switch (inputs[4].toLowerCase().charAt(0)) {
                case 'q':
                    promotion = ChessPiece.PieceType.QUEEN;
                    break;
                case 'b':
                    promotion = ChessPiece.PieceType.BISHOP;
                    break;
                case 'r':
                    promotion = ChessPiece.PieceType.ROOK;
                    break;
                case 'k':
                    promotion = ChessPiece.PieceType.KNIGHT;
                    break;
                default:
                    System.out.println("Invalid promotion");
                    return;
            }
        } else if (inputs.length > 4) {
            System.out.println("too many arguments");
            return;
        }
        ChessMove move = new ChessMove(start, end, promotion);
        try {
            ws.send(new Gson().toJson(new MoveCommand(authToken, gameID, move)));
        } catch (Exception e) {
            System.out.println("Failed to connect to server");
        }
    }

    public void handlePlayingHelp() {
        System.out.println("help");
        System.out.println("redraw chess board");
        System.out.println("leave");
        System.out.println("make move <piece> <end position> <promotion>");
        System.out.println("resign");
        System.out.println("highlight legal moves <piece>");
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
            System.out.println("error logging in");
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
            RegisterResult res = facade.registerRequest(username, password, email);
            this.username = res.username();
            this.authToken = res.authToken();
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

    public boolean handleObserve(String input) {
        try {
            String[] args = input.split("\\s+");
            if (args.length < 3) {
                System.out.println("No game specified");
                return false;
            }
            if (args.length > 3) {
                System.out.println("too many arguments");
                return false;
            }
            int gameNum = 0;
            try {
                gameNum = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.print("Couldn't parse game number");
                return false;
            }
            Integer id = listedIDs.get(gameNum);
            if (id == null) {
                System.out.println("Game does not exist");
                return false;
            }
            gameID = id;
            createWebsocket();
            return true;
        } catch (Exception e) {
            System.out.println("Failed to get game");
            return false;
        }
    }

    public void handleList() {
        try {
            ListGamesResult games = facade.listRequest(authToken);
            listedIDs.clear();
            int i = 1;
            for (var game : games.games()) {
                String gamestr = String.valueOf(i) + ": " + game.gameName;
                gamestr += "\n    white: ";
                gamestr += game.whiteUsername == null ? "<available>" : game.whiteUsername;
                gamestr += "\n    black: ";
                gamestr += game.blackUsername == null ? "<available>" : game.blackUsername;
                System.out.println(gamestr);
                listedIDs.put(i, game.gameID);
                i += 1;
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            e.printStackTrace(System.out);
            System.out.println("Failed to retrieve games list");
        }
    }

    public boolean handlePlay(String input) {
        String[] args = input.split("\\s+");
        if (args.length < 4) {
            System.out.println("Too few inputs");
            return false;
        }
        if (args.length > 4) {
            System.out.println("Too many inputs");
            return false;
        }
        String number = args[2];
        String color = args[3];
        ChessGame.TeamColor team = null;
        if (color.equalsIgnoreCase("white")) {
            team = ChessGame.TeamColor.WHITE;
        } else if (color.equalsIgnoreCase("black")) {
            team = ChessGame.TeamColor.BLACK;
        } else {
            System.out.println("Color must be white or black");
            return false;
        }
        Integer id = null;
        try {
            id = listedIDs.get(Integer.parseInt(number));
        } catch (Exception e) {
            System.out.println("Couldn't parse number " + number);
        }
        if (id == null) {
            System.out.println("Game " + String.valueOf(id) + " does not exist");
            return false;
        }
        try {
            facade.playRequest(authToken, color, id);
            gameID = id;
            createWebsocket();
            return true;
        } catch (RuntimeException e) {
            System.out.println("Joining game failed");
            return false;
        }
    }


    public void handleCreate(String input) {
        String[] args = input.split("\\s+");
        if (args.length < 3) {
            System.out.println("Too few inputs");
            return;
        }
        if (args.length > 3) {
            System.out.println("Too many inputs");
            return;
        }
        String name = args[2];
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
        ClientOverflow.handlePostLoginHelp();
    }

    public void displayChessGame(ChessGame game, ChessGame.TeamColor team) {
        displayChessGame(game, team, new ArrayList<>());
    }

    public void displayChessGame(ChessGame game, ChessGame.TeamColor team, ArrayList<ChessPosition> highlights) {
        ClientOverflow.displayChessGame(game, team, highlights);
    }
}