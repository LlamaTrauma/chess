package service;

import chess.ChessGame;
import dataaccess.DAO;
import dataaccess.DataAccessException;
import model.ListGamesResult;
import model.LoginResult;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requestmodel.*;

import static dataaccess.DAO.*;

public class Service {
    public static RegisterResult register(RegisterRequest request) throws TakenException, RequestException, DataAccessException {
        if (request.password() == null) {
            throw new RequestException("Register request made with null password");
        }
        UserData userData = new UserData(request.username(), BCrypt.hashpw(request.password(), BCrypt.gensalt()), request.email());
        USER_DAO.createUser(userData);
        String authToken = DAO.AUTH_DAO.createAuth(userData.username());
        return new RegisterResult(request.username(), authToken);
    }

    public static LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = USER_DAO.readUser(request.username());
        if (!BCrypt.checkpw(request.password(), user.password())) {
            throw new UnauthorizedException("Incorrect password for user " + request.username());
        }
        String authToken = AUTH_DAO.createAuth(request.username());
        return new LoginResult(request.username(), authToken);
    }

    public static LogoutResult logout(LogoutRequest request) throws DataAccessException {
        AUTH_DAO.validateAuth(request.authToken());
        AUTH_DAO.deleteAuth(request.authToken());
        return new LogoutResult();
    }

    public static ListGamesResult listGames(ListGamesRequest req) throws DataAccessException {
        AUTH_DAO.validateAuth(req.authToken());
        return new ListGamesResult(GAME_DAO.listGames());
    }

    public static CreateGameResult createGame(String authToken, CreateGameRequest req) throws DataAccessException {
        if (req.gameName() == null) {
            throw new RequestException("Making game with null name");
        }
        AUTH_DAO.validateAuth(authToken);
        int id = GAME_DAO.createGame(req.gameName());
        return new CreateGameResult(id);
    }

    public static JoinGameResult joinGame(String authToken, JoinGameRequest req) throws DataAccessException, RequestException {
        if (req.playerColor() == null) {
            throw new RequestException("Team color is null");
        }

        if (!req.playerColor().equalsIgnoreCase("WHITE") && !req.playerColor().equalsIgnoreCase("BLACK")) {
            throw new RequestException("Team color " + req.playerColor() + " is invalid");
        }

        String username = AUTH_DAO.validateAuth(authToken);

        GAME_DAO.joinGame(username, req.gameID(), req.playerColor().equalsIgnoreCase("WHITE") ? ChessGame.TeamColor.WHITE:ChessGame.TeamColor.BLACK);
        return new JoinGameResult();
    }

    public static LogoutResult delete() throws DataAccessException {
        AUTH_DAO.deleteAuths();
        USER_DAO.deleteUsers();
        GAME_DAO.deleteGames();
        return new LogoutResult();
    }
}
