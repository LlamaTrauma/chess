package service;

import chess.ChessGame;
import dataaccess.DAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;

public class ServerGame {
    public GameData game;
    ArrayList<Session> connectedSessions;

    public ServerGame(GameData data) throws Exception {
        connectedSessions = new ArrayList<>();
        game = data;
    }

    public boolean sendMessage(Session session, String message) {
        try {
            session.getRemote().sendString(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void sendAllMessage(String message) {
        for (var session: new ArrayList<>(connectedSessions)) {
            try {
                session.getRemote().sendString(message);
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
