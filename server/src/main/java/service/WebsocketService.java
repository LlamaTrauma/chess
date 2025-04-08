package service;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MoveCommand;
import websocket.commands.ResignCommand;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WebsocketService {

    private final HashMap<Integer, ServerGame> games;

    public WebsocketService () {
        games = new HashMap<Integer, ServerGame>();
    }

    public void connectGame (Session session, ConnectCommand comm) {
        ServerGame game = games.get(comm.getGameID());
        if (game == null) {
            throw new GameDoesNotExist("Game " + String.valueOf(comm.getGameID()) + " does not exist");
        }
    }

    public void moveGame (Session session, MoveCommand comm) {
    }

    public void leaveGame (Session session, LeaveCommand comm) {
    }

    public void resignGame (Session session, ResignCommand comm) {
    }

    public void sendNotificationMessage(String msg, Session session){
        try {
            session.getRemote().sendString(new Gson().toJson(new NotificationMessage(msg)));
        } catch (IOException e) {
        }
    }

    public void sendErrorMessage(String msg, Session session){
        try {
            session.getRemote().sendString(new Gson().toJson(new ErrorMessage(msg)));
        } catch (IOException e) {
        }
    }

    public void sendLoadGameMessage(ChessGame game, Session session) {
        try {
            session.getRemote().sendString(new Gson().toJson(new LoadGameMessage(game)));
        } catch (IOException e) {
        }
    }
}