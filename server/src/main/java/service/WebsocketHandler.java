package service;

import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;

public class WebsocketHandler {
    private static final WebsocketService service = new WebsocketService();

    public static void handleConnectGameCommand (Session session, ConnectCommand comm) {
        try {
            service.connectGame(session, comm);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void handleMoveGameCommand (Session session, MoveCommand comm) {
        try {
            service.moveGame(session, comm);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void handleLeaveGameCommand (Session session, LeaveCommand comm) {
        try {
            service.leaveGame(session, comm);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void handleResignGameCommand (Session session, ResignCommand comm) {
        try {
            service.resignGame(session, comm);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void handleInvalidCommand (Session session, String message) {
    }

    public static void handleError (String error) {
    }
}
