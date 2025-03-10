package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameMetadata;
import service.RequestException;
import service.TakenException;

import java.sql.DataTruncation;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class GameDAOSQL extends DAOSQL implements GameDAO {
    private static final String NAME = "games";
    private static final String[] CREATE_STATEMENT = {
            """
            CREATE TABLE IF NOT EXISTS `mydb`.`games` (
             `game_id` INT NOT NULL AUTO_INCREMENT,
             `white_username` VARCHAR(40) NULL DEFAULT NULL,
             `black_username` VARCHAR(40) NULL DEFAULT NULL,
             `name` VARCHAR(45) NOT NULL,
             `serialized_game` VARCHAR(1000) NOT NULL,
             PRIMARY KEY (`game_id`),
             UNIQUE INDEX `game_id_UNIQUE` (`game_id` ASC) VISIBLE)
           ENGINE = InnoDB
            """
    };

    GameDAOSQL () throws DataAccessException {
        super(NAME, CREATE_STATEMENT);
    }

    public int createGame(String name) throws DataAccessException, TakenException {
        ChessGame game = new ChessGame();
        String gameStr = new Gson().toJson(game, ChessGame.class);
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO games name, serialized_game VALUES (?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, name);
                ps.setString(2, gameStr);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    try (var rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            return rs.getInt("game_id");
                        } else {
                            throw new DataAccessException("Unable to create game");
                        }
                    }
                } else {
                    throw new DataAccessException("Unable to create game");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game: " + e.getMessage());
        }
    }

    public ArrayList<GameMetadata> listGames() throws DataAccessException {
        ArrayList<GameMetadata> metadataList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_id, white_username, black_username, name FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameMetadata metadata = new GameMetadata(
                                rs.getInt("game_id"),
                                rs.getString("white_username"),
                                rs.getString("black_username"),
                                rs.getString("name")
                        );
                        metadataList.add(metadata);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games");
        }
        return metadataList;
    }

    public void joinGame(String username, int gameID, ChessGame.TeamColor team) throws DataAccessException, RequestException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT game_id FROM games WHERE game_id = ?";
            // probably have some lines like this not in a try-with-resources
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try(var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new RequestException("Game id " + String.valueOf(gameID) + " does not exist");
                    }
                }
            }
            String col_to_update = team == ChessGame.TeamColor.BLACK ? "black_username" : "white_username";
            statement = "UPDATE games SET " + col_to_update + " = ? WHERE game_id = ? AND " + col_to_update + " IS NULL";
            try (var ps = conn.prepareStatement(statement)) {
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated > 0) {
                    throw new TakenException("Joining game id " + String.valueOf(gameID) + " failed");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("finding game failed");
        }

    }

    public void deleteGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM games";
            var ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Deleting games failed");
        }
    }
}
