package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameMetadata;
import service.RequestException;
import service.TakenException;

import javax.xml.crypto.Data;
import java.sql.DataTruncation;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

public class GameDAOSQL extends DAOSQL implements GameDAO {
    private static final String NAME = "games";
    private static final String[] CREATE_STATEMENT = {
            """
            CREATE TABLE IF NOT EXISTS `games` (
             `game_id` INT NOT NULL AUTO_INCREMENT,
             `white_username` VARCHAR(40) NULL DEFAULT NULL,
             `black_username` VARCHAR(40) NULL DEFAULT NULL,
             `name` VARCHAR(45) NOT NULL,
             `serialized_game` VARCHAR(10000) NOT NULL,
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
            var statement = "INSERT INTO games (name, serialized_game) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, gameStr);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Unable to create game");
                }
                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    } else {
                        throw new DataAccessException("Unable to create game");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game: " + e.getMessage());
        }
    }

    public void updateGame(GameData data) throws DataAccessException, TakenException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE games SET white_username = ?, black_username = ?, serialized_game = ? WHERE game_id = ?";
            try (var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, data.metadata.whiteUsername);
                ps.setString(2, data.metadata.blackUsername);
                ps.setString(3, new Gson().toJson(data.game));
                ps.setInt(4, data.metadata.gameID);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Game does not exist");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to update game: " + e.getMessage());
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
            String colToUpdate = team == ChessGame.TeamColor.BLACK ? "black_username" : "white_username";
            statement = "UPDATE games SET " + colToUpdate + " = ? WHERE game_id = ? AND " + colToUpdate + " IS NULL";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, gameID);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new TakenException("Joining game id " + String.valueOf(gameID) + " failed");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

    public GameData readGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT white_username, black_username, name, serialized_game FROM games WHERE game_id = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        GameMetadata metadata = new GameMetadata(gameID,
                                rs.getString("white_username"),
                                rs.getString("black_username"),
                                rs.getString("name")
                        );
                        ChessGame game = new Gson().fromJson(rs.getString("serialized_game"), ChessGame.class);
                        return new GameData(metadata, game);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("reading game failed");
        }
    }
}
