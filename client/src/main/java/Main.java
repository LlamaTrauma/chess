import chess.*;
import client.Client;

import java.util.Scanner;

public class Main {
    private static final Client client = new Client();

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        Client.handleInputReturnFlag returnFlag = doPreLoginLoop();
        if (returnFlag == Client.handleInputReturnFlag.QUIT) {
            return;
        }

        Client.handleInputReturnFlag returnFlag = doPostLoginLoop();
        if (returnFlag == Client.handleInputReturnFlag.QUIT) {
            return;
        }
    }

    private static Client.handleInputReturnFlag doPreLoginLoop() {
        Scanner scanner = new Scanner(System.in);
        String input;
        Client.handleInputReturnFlag returnFlag = Client.handleInputReturnFlag.CONTINUE;
        while (returnFlag == Client.handleInputReturnFlag.CONTINUE) {
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
            input = scanner.nextLine();
            returnFlag = client.handlePostLoginInput(input);
        }
        return returnFlag;
    }
}