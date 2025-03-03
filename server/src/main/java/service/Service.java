package service;

import dataaccess.DataAccessException;
import model.UserData;
import server.*;

import javax.naming.AuthenticationException;

import static dataaccess.DAO.*;

public class Service {
    public static RegisterResult register(RegisterRequest request) throws TakenException, RequestException, DataAccessException {
        UserData userData = new UserData(request.username(), request.password(), request.email());
        String authToken = userDAO.createUser(userData);
        return new RegisterResult(request.username(), authToken);
    }

    public static LoginResult login(LoginRequest request) throws AuthenticationException, DataAccessException {
        UserData user = userDAO.readUser(request.username());
        if (!user.password().equals(request.password())) {
            throw new AuthenticationException("Incorrect password for user " + request.username());
        }
        String authToken = authDAO.createAuth(request.username());
        return new LoginResult(request.username(), authToken);
    }

    public static LogoutResult logout(LogoutRequest request) throws AuthenticationException, DataAccessException {
        authDAO.validateAuth(request.authToken());
        authDAO.deleteAuth(request.authToken());
        return new LogoutResult();
    }

    public static ListGamesResult listGames(ListGamesRequest req) throws AuthenticationException, DataAccessException {
        authDAO.validateAuth(req.authToken());
        return new ListGamesResult(gameDAO.listGames());
    }

    public static CreateGameResult createGame(CreateGameRequest req) throws AuthenticationException, DataAccessException {
        authDAO.validateAuth(req.authToken());
        gameDAO.createGame(req.gameName());
        return new CreateGameResult(req.gameName());
    }
}
