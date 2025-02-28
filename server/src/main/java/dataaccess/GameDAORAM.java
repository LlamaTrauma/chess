package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.TakenException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameDAORAM implements GameDAO {
    Map<Integer, GameData> gamesByID = new HashMap<Integer, GameData>();
    private int topGameID = 0;

    public int createGame(String name) throws DataAccessException, TakenException {
        topGameID += 1;
        GameData newGame = new GameData(topGameID, null, null, name, new ChessGame());
        return topGameID;
    }

    public ArrayList<GameData> listGames(String auth) throws DataAccessException {
        ArrayList<GameData> gameList = new ArrayList<>(gamesByID.values());
        return gameList;
    }

    public void joinGame(String username, int gameID, ChessGame.TeamColor team) throws DataAccessException {
        GameData gameData = gamesByID.get(gameID);
        if (gameData == null) {
            throw new DataAccessException("Game ID " + String.valueOf(gameID) + " does not exist");
        }
        if (team == ChessGame.TeamColor.BLACK) {
            if (gameData.blackUsername != null) {
                throw new TakenException("Black is already taken for game id " + String.valueOf(gameID));
            } else {
                gameData.blackUsername = username;
            }
        } else if (team == ChessGame.TeamColor.WHITE) {
            if (gameData.whiteUsername != null) {
                throw new TakenException("White is already taken for game id " + String.valueOf(gameID));
            } else {
                gameData.whiteUsername = username;
            }
        }
    }

    public void deleteGames() {
        gamesByID.clear();
    }
}
