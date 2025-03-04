package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.UserData;
import server.*;

import static dataaccess.DAO.*;

public class Service {
    public static RegisterResult register(RegisterRequest request) throws TakenException, RequestException, DataAccessException {
        if (request.password() == null) {
            throw new RequestException("Register request made with null password");
        }
        UserData userData = new UserData(request.username(), request.password(), request.email());
        String authToken = userDAO.createUser(userData);
        return new RegisterResult(request.username(), authToken);
    }

    public static LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = userDAO.readUser(request.username());
        if (!user.password().equals(request.password())) {
            throw new UnauthorizedException("Incorrect password for user " + request.username());
        }
        String authToken = authDAO.createAuth(request.username());
        return new LoginResult(request.username(), authToken);
    }

    public static LogoutResult logout(LogoutRequest request) throws DataAccessException {
        authDAO.validateAuth(request.authToken());
        authDAO.deleteAuth(request.authToken());
        return new LogoutResult();
    }

    public static ListGamesResult listGames(ListGamesRequest req) throws DataAccessException {
        authDAO.validateAuth(req.authToken());
        return new ListGamesResult(gameDAO.listGames());
    }

    public static CreateGameResult createGame(String authToken, CreateGameRequest req) throws DataAccessException {
        if (req.gameName() == null) {
            throw new RequestException("Making game with null name");
        }
        authDAO.validateAuth(authToken);
        int id = gameDAO.createGame(req.gameName());
        return new CreateGameResult(id);
    }

    public static JoinGameResult joinGame(String authToken, JoinGameRequest req) throws DataAccessException, RequestException {
        if (req.playerColor() == null) {
            throw new RequestException("Team color is null");
        }

        if (!req.playerColor().equals("WHITE") && !req.playerColor().equals("BLACK")) {
            throw new RequestException("Team color " + req.playerColor() + " is invalid");
        }

        String username = authDAO.validateAuth(authToken);

        gameDAO.joinGame(username, req.gameID(), req.playerColor().equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
        return new JoinGameResult();
    }

    public static LogoutResult delete() throws DataAccessException {
        authDAO.deleteAuths();
        userDAO.deleteUsers();
        gameDAO.deleteGames();
        return new LogoutResult();
    }
}
