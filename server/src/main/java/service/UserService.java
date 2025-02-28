package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.UserDAORAM;
import model.UserData;
import server.RegisterRequest;
import server.RegisterResult;

import static dataaccess.DAO.userDAO;

public class UserService {
    public static RegisterResult register(RegisterRequest request) throws TakenException, RequestException, DataAccessException {
        UserData userData = new UserData(request.username(), request.password(), request.email());
        String authToken = userDAO.createUser(userData);
        return new RegisterResult(request.username(), authToken);
    }
}
