package dataaccess;

import model.UserData;
import service.TakenException;
import service.UnauthorizedException;

import java.sql.SQLException;

public class UserDAOSQL extends DAOSQL implements UserDAO {
    private static final String NAME = "users";
    private static final String[] CREATE_STATEMENT = {
            """
            CREATE TABLE IF NOT EXISTS `mydb`.`users` (
              `user_id` INT NOT NULL AUTO_INCREMENT,
              `username` VARCHAR(40) NOT NULL,
              `password` VARCHAR(100) NOT NULL,
              `email` VARCHAR(255) NOT NULL,
              UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
              PRIMARY KEY (`user_id`, `username`),
              UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE)
            ENGINE = InnoDB
            """
    };

    UserDAOSQL () throws DataAccessException {
        super(NAME, CREATE_STATEMENT);
    }

    public void createUser(UserData userData) throws DataAccessException, TakenException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, userData.username());
                ps.setString(2, userData.password());
                ps.setString(3, userData.email());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                throw new TakenException("Username " + userData.username() + " is taken");
            } else {
                throw new DataAccessException("Creating user " + userData.username() + " failed");
            }
        }
    }

    public UserData readUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM users WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Reading user " + username + " failed");
        }
        throw new UnauthorizedException("User " + username + " does not exist");
    }

    public void deleteUsers() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM users";
            var ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Deleting users failed");
        }
    }
}
