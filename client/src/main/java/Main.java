import chess.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        doPreLoginLoop();
    }

    private static void doPreLoginLoop() {
        Scanner scannner = new Scanner(System.in);
        String input;
        do {
            input = scannner.nextLine();
        } while ();
    }
}