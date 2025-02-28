package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.TakenException;

import java.util.ArrayList;

public interface GameDAO {
    public int createGame(String name) throws DataAccessException, TakenException;
    public ArrayList<GameData> listGames(String auth) throws DataAccessException;
    public void joinGame(String username, int gameID, ChessGame.TeamColor team) throws DataAccessException;
    public void deleteGames();
}
