package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DAO;
import model.GameData;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.HashMap;

public class WebsocketService {

    private final HashMap<Integer, ServerGame> games;

    public WebsocketService () {
        games = new HashMap<Integer, ServerGame>();
    }

    public void connectGame (Session session, ConnectCommand comm) throws InvalidUserCommandError {
        ServerGame serverGame = null;
        try {
            String username = DAO.AUTH_DAO.validateAuth(comm.getAuthToken());
            serverGame = games.get(comm.getGameID());
            if (serverGame == null) {
                GameData readGame = DAO.GAME_DAO.readGame(comm.getGameID());
                if (readGame == null) {
                    throw new InvalidUserCommandError("Game " + String.valueOf(comm.getGameID()) + " does not exist");
                }
                serverGame = new ServerGame(readGame);
            }
            games.put(serverGame.game.metadata.gameID, serverGame);
            serverGame.connectedSessions.add(session);
            serverGame.sendAllMessage(username + " joined the game");
            sendLoadGameMessage(session, serverGame.game);
        } catch (InvalidUserCommandError e) {
            sendErrorMessage(session, e.getMessage());
        } catch (Exception e) {
// adsf
        }
    }

    public void moveGame (Session session, MoveCommand comm) {
        ServerGame serverGame = null;
        try {
            String username = DAO.AUTH_DAO.validateAuth(comm.getAuthToken());
            serverGame = games.get(comm.getGameID());
            if (serverGame == null) {
                throw new InvalidUserCommandError("Game " + String.valueOf(comm.getGameID()) + " does not exist");
            }
            if (serverGame.game.game.getTeamTurn() != serverGame.userTeam(username)) {
                throw new InvalidUserCommandError("It is not your turn to move");
            }
            serverGame.game.game.makeMove(comm.getMove());
            DAO.GAME_DAO.updateGame(serverGame.game);
            serverGame.sendAllMessage(username + " made a move");
            serverGame.sendAllUpdate();
        } catch (InvalidUserCommandError e) {
            sendErrorMessage(session, e.getMessage());
        } catch (InvalidMoveException e) {
            sendErrorMessage(session, "invalid move");
        } catch (Exception e) {

        }
    }

    public void leaveGame (Session session, LeaveCommand comm) {
        ServerGame serverGame = null;
        try {
            String username = DAO.AUTH_DAO.validateAuth(comm.getAuthToken());
            serverGame = games.get(comm.getGameID());
            if (serverGame == null) {
                throw new InvalidUserCommandError("Game " + String.valueOf(comm.getGameID()) + " does not exist");
            }
            serverGame.connectedSessions.remove(session);
            serverGame.sendAllMessage(username + " left");
        } catch (InvalidUserCommandError e) {
            sendErrorMessage(session, e.getMessage());
        } catch (Exception e) {
//            sendErrorMessage(session, e.getMessage());
        }
    }

    public void resignGame (Session session, ResignCommand comm) {
        ServerGame serverGame = null;
        try {
            String username = DAO.AUTH_DAO.validateAuth(comm.getAuthToken());
            serverGame = games.get(comm.getGameID());
            if (serverGame == null) {
                throw new InvalidUserCommandError("Game " + String.valueOf(comm.getGameID()) + " does not exist");
            }
            if (serverGame.userTeam(username) == null) {
                throw new InvalidUserCommandError("You are only observing this game");
            }
            DAO.GAME_DAO.updateGame(serverGame.game);
            serverGame.connectedSessions.remove(session);
            serverGame.sendAllMessage(username + " resigned from the game");
        } catch (InvalidUserCommandError e) {
            sendErrorMessage(session, e.getMessage());
        } catch (Exception e) {
//            sendErrorMessage(session, e.getMessage());
        }
    }

    public void sendErrorMessage(Session session, String msg){
        try {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage(msg)));
        } catch (IOException ignored) {
        }
    }

    public void sendLoadGameMessage(Session session, GameData game) {
        try {
            session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(game.game, game.metadata.blackUsername)));
        } catch (IOException ignored) {
        }
    }
}