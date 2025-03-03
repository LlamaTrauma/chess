package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameMetadata;
import service.TakenException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameDAORAM implements GameDAO {
    Map<Integer, GameData> gamesByID = new HashMap<Integer, GameData>();
    private int topGameID = 0;

    public int createGame(String name) throws DataAccessException, TakenException {
        topGameID += 1;
        GameData newGame = new GameData(new GameMetadata(topGameID, null, null, name), new ChessGame());
        return topGameID;
    }

    public ArrayList<GameMetadata> listGames() throws DataAccessException {
        ArrayList<GameData> gameList = new ArrayList<>(gamesByID.values());
        ArrayList<GameMetadata> metadataList = new ArrayList<>();
        for (GameData gameData : gameList) {
            metadataList.add(gameData.metadata);
        }
        return metadataList;
    }

    public void joinGame(String username, int gameID, ChessGame.TeamColor team) throws DataAccessException {
        GameData gameData = gamesByID.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game ID " + String.valueOf(gameID) + " does not exist");
        }
        if (team == ChessGame.TeamColor.BLACK) {
            if (gameData.metadata.blackUsername != null) {
                throw new TakenException("Black is already taken for game id " + String.valueOf(gameID));
            } else {
                gameData.metadata.blackUsername = username;
            }
        } else if (team == ChessGame.TeamColor.WHITE) {
            if (gameData.metadata.whiteUsername != null) {
                throw new TakenException("White is already taken for game id " + String.valueOf(gameID));
            } else {
                gameData.metadata.whiteUsername = username;
            }
        }
    }

    public void deleteGames() {
        gamesByID.clear();
    }
}
