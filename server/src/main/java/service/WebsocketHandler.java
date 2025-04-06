package service;

import spark.Session;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;

public class WebsocketHandler {
    public static void handleConnectGameCommand (Session session, ConnectCommand comm) {
        try {
            WebsocketService.connectGame(session, comm);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void handleMoveGameCommand (Session session, MoveCommand comm) {
    }

    public static void handleLeaveGameCommand (Session session, LeaveCommand comm) {
    }

    public static void handleResignGameCommand (Session session, ResignCommand comm) {
    }
}
