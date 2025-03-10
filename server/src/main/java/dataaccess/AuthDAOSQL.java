package dataaccess;

import service.TakenException;
import service.UnauthorizedException;

import javax.naming.AuthenticationException;
import java.sql.SQLException;
import java.util.UUID;

public class AuthDAOSQL extends DAOSQL implements AuthDAO {
    private static final String NAME = "auths";
    private static final String[] CREATE_STATEMENT = {
            """
            CREATE TABLE IF NOT EXISTS `mydb`.`auths` (
              `auth_id` INT NOT NULL AUTO_INCREMENT,
              `token` VARCHAR(100) NOT NULL,
              `username` VARCHAR(40) NOT NULL,
              PRIMARY KEY (`auth_id`),
              UNIQUE INDEX `auth_id_UNIQUE` (`auth_id` ASC) VISIBLE)
            ENGINE = InnoDB
            """
    };

    AuthDAOSQL () throws DataAccessException {
        super(NAME, CREATE_STATEMENT);
    }

    public String createAuth(String username) throws DataAccessException, TakenException {
        String authToken = UUID.randomUUID().toString();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auths (token, username) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.setString(2, username);
                ps.executeUpdate();
                return authToken;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create auth token for user " + username + ": " + e.getMessage());
        }
    }

    public String validateAuth(String auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM auths WHERE token = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                try(var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to validate auth token " + e.getMessage());
        }
        throw new UnauthorizedException("Unable to validate auth token " + auth);
    }

    public void deleteAuth(String auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths WHERE token = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth);
                int deletedRows = ps.executeUpdate();
                if (deletedRows == 0) {
                    throw new DataAccessException("Auth " + auth + " does not exist");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token " + auth);
        }
    }

    public void deleteAuths() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auths";
            var ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Deleting auths failed");
        }
    }
}
