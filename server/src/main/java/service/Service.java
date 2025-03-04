package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.UserData;
import requestmodel.*;

import static dataaccess.DAO.*;

public class Service {
    public static RegisterResult register(RegisterRequest request) throws TakenException, RequestException, DataAccessException {
        if (request.password() == null) {
            throw new RequestException("Register request made with null password");
        }
        UserData userData = new UserData(request.username(), request.password(), request.email());
        String authToken = USER_DAO.createUser(userData);
        return new RegisterResult(request.username(), authToken);
    }

    public static LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = USER_DAO.readUser(request.username());
        if (!user.password().equals(request.password())) {
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

        if (!req.playerColor().equals("WHITE") && !req.playerColor().equals("BLACK")) {
            throw new RequestException("Team color " + req.playerColor() + " is invalid");
        }

        String username = AUTH_DAO.validateAuth(authToken);

        GAME_DAO.joinGame(username, req.gameID(), req.playerColor().equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
        return new JoinGameResult();
    }

    public static LogoutResult delete() throws DataAccessException {
        AUTH_DAO.deleteAuths();
        USER_DAO.deleteUsers();
        GAME_DAO.deleteGames();
        return new LogoutResult();
    }
}
