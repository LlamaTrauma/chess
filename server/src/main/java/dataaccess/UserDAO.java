package dataaccess;

import model.UserData;

public interface UserDAO {
    public void createUser(UserData userData) throws DataAccessException;
    public UserData readUser(String username) throws DataAccessException;
}
