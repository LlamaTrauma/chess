package dataaccess;

public class DAO {
    public static final UserDAO USER_DAO = new UserDAORAM();
    public static final AuthDAO AUTH_DAO = new AuthDAORAM();
    public static final GameDAO GAME_DAO = new GameDAORAM();
}
