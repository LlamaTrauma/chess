package service;

import org.eclipse.jetty.websocket.api.*;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;

import java.util.ArrayList;

public class WebsocketService {
    ArrayList<Session> connectedPlayers;
    Session host;

    public WebsocketService () {
        connectedPlayers = new ArrayList<>();
    }

    public void connectGame (Session session, ConnectCommand comm) {
        connectedPlayers.add(session);
    }

    public void moveGame (Session session, MoveCommand comm) {
        connectedPlayers.add(session);
    }

    public void leaveGame (Session session, LeaveCommand comm) {
        connectedPlayers.add(session);
    }

    public void resignGame (Session session, ResignCommand comm) {
        connectedPlayers.add(session);
    }

    public boolean sendHostMessage (String message) {
        while (!connectedPlayers.isEmpty()) {
            if (host == null) {
                host = connectedPlayers.getFirst();
            }
            try {
                sendMessage(host, message);
            } catch (Exception e) {
                host = null;
            }
        }
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