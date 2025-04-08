package service;

import dataaccess.DAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;

public class ServerGame {
    public GameData game;
    ArrayList<Session> connectedPlayers;
    Session host;
    int gameID;

    public ServerGame() throws Exception {
        connectedPlayers = new ArrayList<>();
        game = DAO.GAME_DAO.readGame(gameID);
    }

    public boolean sendHostMessage (String message) {
        if (host == null) {
            host = connectedPlayers.getFirst();
        }
        while (!connectedPlayers.isEmpty()) {
            if (sendMessage(host, message)) {
                return true;
            } else {
                connectedPlayers.remove(host);
            }
        }
        return false;
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
        for (var session: new ArrayList<>(connectedPlayers)) {
            try {
                session.getRemote().sendString(message);
            } catch (Exception e) {
                connectedPlayers.remove(session);
            }
        }
    }
}
