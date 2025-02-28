package dataaccess;

import model.UserData;
import service.TakenException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthDAORAM implements AuthDAO {
    Map<String, String> usernamesByAuth = new HashMap<String, String>();

    public String createAuth(String username) throws DataAccessException, TakenException {
        String authToken = UUID.randomUUID().toString();
        usernamesByAuth.put(authToken, username);
        return authToken;
    }

    public String validateAuth(String auth) throws DataAccessException {
        String username = usernamesByAuth.get(auth);
        if (username == null) {
            throw new DataAccessException("Auth token " + auth + " does not exist");
        }
        return username;
    }

    public void deleteAuth(String auth) throws DataAccessException {
        if (usernamesByAuth.get(auth) != null) {
            usernamesByAuth.remove(auth);
        } else {
            throw new DataAccessException("Auth token " + auth + " does not exist valid");
        }
    }

    public void deleteAuths() {
        usernamesByAuth.clear();
    }
}
