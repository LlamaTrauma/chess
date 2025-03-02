package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.UserDAORAM;
import model.UserData;
import server.LoginRequest;
import server.LoginResult;
import server.LogoutRequest;
import server.LogoutResult;
import server.RegisterRequest;
import server.RegisterResult;

import javax.naming.AuthenticationException;

import static dataaccess.DAO.userDAO;
import static dataaccess.DAO.authDAO;

public class UserService {
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
}
