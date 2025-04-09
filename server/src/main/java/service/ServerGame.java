package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.ArrayList;

public class ServerGame {
    public GameData game;
    ArrayList<Session> connectedSessions;
    boolean over = false;

    public ServerGame(GameData data) throws Exception {
        connectedSessions = new ArrayList<>();
        game = data;
    }

    public void sendAllMessage(String message) {
        for (var session: new ArrayList<>(connectedSessions)) {
            try {
                session.getRemote().sendString(new Gson().toJson(new NotificationMessage(message)));
            } catch (Exception e) {
                connectedSessions.remove(session);
            }
        }
    }

    public void sendAllUpdate() {
        for (var session: new ArrayList<>(connectedSessions)) {
            try {
                session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(game.game, game.metadata.blackUsername)));
            } catch (Exception e) {
                connectedSessions.remove(session);
            }
        }
    }

    public ChessGame.TeamColor userTeam(String username) {
        if (username.equals(game.metadata.blackUsername)) {
            return ChessGame.TeamColor.BLACK;
        }
        if (username.equals(game.metadata.whiteUsername)) {
            return ChessGame.TeamColor.WHITE;
        }
        return null;
    }

}
