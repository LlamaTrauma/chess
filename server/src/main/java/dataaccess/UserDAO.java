package dataaccess;

import model.UserData;

public interface UserDAO {
    public String createUser(UserData userData) throws DataAccessException;
    public UserData readUser(String username) throws DataAccessException;
    public void deleteUsers();
}
