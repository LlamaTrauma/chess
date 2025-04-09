import chess.*;
import client.Client;
import client.WebsocketClient;
import server.Server;

import java.util.Scanner;

import static client.Client.HandleInputReturnFlag.*;

public class Main {
    private static final Client CLIENT = new Client();

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        Server myServer = new Server();
        myServer.run(8080);

        Client.HandleInputReturnFlag returnFlag = LOOP_PRE;
        while(returnFlag != QUIT) {
            if (returnFlag == LOOP_PRE) {
                do {
                    returnFlag = doPreLoginLoop();
                } while (returnFlag == CONTINUE);
            } else if (returnFlag == LOOP_POST) {
                do {
                    returnFlag = doPostLoginLoop();
                } while (returnFlag == CONTINUE);
            } else if (returnFlag == LOOP_GAME) {
                CLIENT.current_game = null;
                try {
                    WebsocketClient ws = new WebsocketClient(8080, CLIENT);
                    do {
                        returnFlag = doPlayingLoop(ws);
                    } while (returnFlag == CONTINUE);
                } catch (Exception e) {
                    System.out.println("unable to start websocket client");
                    returnFlag = LOOP_POST;
                }
            } else {
                returnFlag = QUIT;
            }
        }
    }

    private static Client.HandleInputReturnFlag doPreLoginLoop() {
        Scanner scanner = new Scanner(System.in);
        String input;
        Client.HandleInputReturnFlag returnFlag = Client.HandleInputReturnFlag.CONTINUE;
        while (returnFlag == Client.HandleInputReturnFlag.CONTINUE) {
            System.out.print("[LOGGED OUT] >>> ");
            input = scanner.nextLine();
            returnFlag = CLIENT.handlePreLoginInput(input);
        }
        return returnFlag;
    }

    private static Client.HandleInputReturnFlag doPostLoginLoop() {
        Scanner scanner = new Scanner(System.in);
        String input;
        Client.HandleInputReturnFlag returnFlag = Client.HandleInputReturnFlag.CONTINUE;
        while (returnFlag == Client.HandleInputReturnFlag.CONTINUE) {
            System.out.print("[" + CLIENT.getUsername() + "] >>> ");
            input = scanner.nextLine();
            returnFlag = CLIENT.handlePostLoginInput(input);
        }
        return returnFlag;
    }

    private static Client.HandleInputReturnFlag doPlayingLoop(WebsocketClient ws) {
        Scanner scanner = new Scanner(System.in);
        String input;
        Client.HandleInputReturnFlag returnFlag = Client.HandleInputReturnFlag.CONTINUE;
        while (returnFlag == Client.HandleInputReturnFlag.CONTINUE) {
            System.out.print("[" + CLIENT.getUsername() + "] >>> ");
            input = scanner.nextLine();
            returnFlag = CLIENT.handlePlayingInput(ws, input);
        }
        return returnFlag;
    }
}