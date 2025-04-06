package service;

import spark.Session;
import websocket.commands.ConnectCommand;

import java.util.ArrayList;

public class WebsocketService {
    ArrayList<Service> connectedPlayers;

    public WebsocketService () {
        connectedPlayers = new ArrayList<>();
    }

    public static void connectGame (Session session, ConnectCommand comm) {

    }
}
