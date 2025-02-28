package dataaccess;

public class DAO {
    public static final UserDAO userDAO = new UserDAORAM();
    public static final AuthDAO authDAO = new AuthDAORAM();
    public static final GameDAO gameDAO = new GameDAORAM();
}
