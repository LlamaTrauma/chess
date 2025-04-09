package client;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ClientWebsocketHandler {
    public static void handleMessage(Client client, String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                displayGame(client, new Gson().fromJson(message, LoadGameMessage.class));
                break;
            case ERROR:
                ErrorMessage err = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println("ERROR: " + err.errorMessage);
                break;
            case NOTIFICATION:
                NotificationMessage note = new Gson().fromJson(message, NotificationMessage.class);
                System.out.println(note.message);
                break;
        }
    }

    public static void displayGame(Client client, LoadGameMessage msg) {
        client.displayChessGame(msg.game, client.getUsername().equals(msg.blackUsername) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);
    }
}
