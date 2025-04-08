package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.GameMetadata;
import service.TakenException;

import java.util.ArrayList;

public interface GameDAO {
    public int createGame(String name) throws DataAccessException, TakenException;
    public ArrayList<GameMetadata> listGames() throws DataAccessException;
    public void joinGame(String username, int gameID, ChessGame.TeamColor team) throws DataAccessException;
    public void deleteGames() throws DataAccessException;
    public GameData readGame(int gameID) throws DataAccessException;
    public void updateGame(GameData data) throws DataAccessException, TakenException;
}
