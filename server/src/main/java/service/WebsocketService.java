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
            } else {
                GameData readGame = DAO.GAME_DAO.readGame(comm.getGameID());
                serverGame.game.metadata.whiteUsername = readGame.metadata.whiteUsername;
                serverGame.game.metadata.blackUsername = readGame.metadata.blackUsername;
            }
            games.put(serverGame.game.metadata.gameID, serverGame);
            serverGame.connectedSessions.add(session);
            sendLoadGameMessage(session, serverGame.game);
            serverGame.sendAllButMessage(session, username + " joined the game");
        } catch (InvalidUserCommandError e) {
            sendErrorMessage(session, e.getMessage());
        } catch (UnauthorizedException e) {
            sendErrorMessage(session, "unauthorized");
        } catch (Exception e) {
            sendErrorMessage(session, "error");
        }
    }

    public void moveGame (Session session, MoveCommand comm) {
        ServerGame serverGame = null;
        try {
            String username = DAO.AUTH_DAO.validateAuth(comm.getAuthToken());
            String extraInfo = "";
            serverGame = games.get(comm.getGameID());
            if (serverGame == null) {
                throw new InvalidUserCommandError("Game " + String.valueOf(comm.getGameID()) + " does not exist");
            }
            if (serverGame.userTeam(username) == null) {
                throw new InvalidUserCommandError("You are observing this game");
            }
            if (serverGame.game.game.getTeamTurn() != serverGame.userTeam(username)) {
                throw new InvalidUserCommandError("It is not your turn to move");
            }
            if (serverGame.over) {
                throw new InvalidUserCommandError("Game is over");
            }
            serverGame.game.game.makeMove(comm.getMove());
            if (serverGame.game.game.isInCheckmate(serverGame.game.game.getTeamTurn())) {
                extraInfo += (serverGame.game.game.getTeamTurn() == ChessGame.TeamColor.WHITE ?
                        "WHITE" : "BLACK") + " is in checkmate";
                serverGame.over = true;
            } else if (serverGame.game.game.isInStalemate(serverGame.game.game.getTeamTurn())) {
                extraInfo += "Game is a stalemate";
                serverGame.over = true;
            } else if (serverGame.game.game.isInCheck(serverGame.game.game.getTeamTurn())) {
                extraInfo += (serverGame.game.game.getTeamTurn() == ChessGame.TeamColor.WHITE ?
                        "WHITE" : "BLACK") + " is in check";
            }
            DAO.GAME_DAO.updateGame(serverGame.game);
            serverGame.sendAllButMessage(session, username + " made a move");
            if (!extraInfo.isEmpty()) {
                serverGame.sendAllMessage(extraInfo);
            }
            serverGame.sendAllUpdate();
        }
        catch (InvalidUserCommandError e) {
            sendErrorMessage(session, e.getMessage());
        } catch (InvalidMoveException e) {
            sendErrorMessage(session, "invalid move");
        } catch (UnauthorizedException e) {
            sendErrorMessage(session, "unauthorized");
        } catch (Exception e) {
            sendErrorMessage(session, "error");
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
            boolean doUpdate = false;
            if (username.equals(serverGame.game.metadata.whiteUsername)) {
                serverGame.game.metadata.whiteUsername = null;
                doUpdate = true;
            } else if (username.equals(serverGame.game.metadata.blackUsername)) {
                serverGame.game.metadata.blackUsername = null;
                doUpdate = true;
            }
            if (doUpdate) {
                DAO.GAME_DAO.updateGame(serverGame.game);
            }
            serverGame.connectedSessions.remove(session);
            serverGame.sendAllButMessage(session, username + " left");
        } catch (InvalidUserCommandError e) {
            sendErrorMessage(session, e.getMessage());
        } catch (UnauthorizedException e) {
            sendErrorMessage(session, "unauthorized");
        } catch (Exception e) {
            sendErrorMessage(session, "error");
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
            if (serverGame.over) {
                throw new InvalidUserCommandError("The game is already over");
            }
            DAO.GAME_DAO.updateGame(serverGame.game);
            serverGame.sendAllMessage(username + " resigned from the game");
            serverGame.over = true;
        } catch (InvalidUserCommandError e) {
            sendErrorMessage(session, e.getMessage());
        } catch (UnauthorizedException e) {
            sendErrorMessage(session, "unauthorized");
        } catch (Exception e) {
            sendErrorMessage(session, "error");
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