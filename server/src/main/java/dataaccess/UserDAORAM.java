package dataaccess;

import model.UserData;
import service.TakenException;
import service.UnauthorizedException;

import java.util.*;

public class UserDAORAM implements UserDAO {
    Map<String, UserData> usersByUsername = new HashMap<String, UserData>();

    public void createUser(UserData userData) throws DataAccessException, TakenException {
        if (usersByUsername.containsKey(userData.username())) {
            throw new TakenException("Username " + userData.username() + " is taken");
        }
        usersByUsername.put(userData.username(), userData);
    }

    public UserData readUser(String username) throws DataAccessException {
        UserData userData = usersByUsername.get(username);
        if (userData == null) {
            throw new UnauthorizedException("User " + username + " does not exist");
        }
        return userData;
    }

    public void deleteUsers() {
        usersByUsername.clear();
    }
}
