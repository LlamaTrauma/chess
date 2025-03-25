import chess.*;
import client.Client;
import server.Server;

import java.util.Scanner;

import static client.Client.handleInputReturnFlag.*;

public class Main {
    private static final Client client = new Client();

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        Server myServer = new Server();
        myServer.run(8080);

        Client.handleInputReturnFlag returnFlag = LOOP_PRE;
        while(returnFlag != QUIT) {
            if (returnFlag == LOOP_PRE) {
                do {
                    returnFlag = doPreLoginLoop();
                } while (returnFlag == CONTINUE);
            } else if (returnFlag == LOOP_POST) {
                do {
                    returnFlag = doPostLoginLoop();
                } while (returnFlag == CONTINUE);
            } else {
                returnFlag = QUIT;
            }
        }
    }

    private static Client.handleInputReturnFlag doPreLoginLoop() {
        Scanner scanner = new Scanner(System.in);
        String input;
        Client.handleInputReturnFlag returnFlag = Client.handleInputReturnFlag.CONTINUE;
        while (returnFlag == Client.handleInputReturnFlag.CONTINUE) {
            System.out.print("[LOGGED OUT] >>> ");
            input = scanner.nextLine();
            returnFlag = client.handlePreLoginInput(input);
        }
        return returnFlag;
    }

    private static Client.handleInputReturnFlag doPostLoginLoop() {
        Scanner scanner = new Scanner(System.in);
        String input;
        Client.handleInputReturnFlag returnFlag = Client.handleInputReturnFlag.CONTINUE;
        while (returnFlag == Client.handleInputReturnFlag.CONTINUE) {
            System.out.println("[" + client.getUsername() + "] >>> ");
            input = scanner.nextLine();
            returnFlag = client.handlePostLoginInput(input);
        }
        return returnFlag;
    }
}