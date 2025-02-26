package dataaccess;

import model.UserData;
import service.TakenException;

import java.util.*;

public class UserDAORAM implements UserDAO {
    Map<String, UserData> usersByUsername = new HashMap<String, UserData>();
    Map<String, String> authsByUsername = new HashMap<String, String>();

    public String createUser(UserData userData) throws DataAccessException, TakenException {
        if (usersByUsername.containsKey(userData.username())) {
            throw new TakenException("Username " + userData.username() + " is taken");
        }
        String authToken = UUID.randomUUID().toString();
        usersByUsername.put(userData.username(), userData);
        authsByUsername.put(userData.username(), authToken);
        return authToken;
    }

    public UserData readUser(String username) throws DataAccessException {
        UserData userData = usersByUsername.get(username);
        if (userData == null) {
            throw new DataAccessException("User " + username + " does not exist");
        }
        return userData;
    }
}
