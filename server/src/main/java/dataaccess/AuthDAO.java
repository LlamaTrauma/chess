package dataaccess;

public interface AuthDAO {
    public String createAuth(String username) throws DataAccessException;
    public String validateAuth(String auth) throws DataAccessException;
    public void deleteAuth(String auth) throws DataAccessException;
    public void deleteAuths();
}
