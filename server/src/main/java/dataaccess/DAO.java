package dataaccess;

public class DAO {
    public static final UserDAO USER_DAO;
    public static final AuthDAO AUTH_DAO;
    public static final GameDAO GAME_DAO;

    static {
        try {
            USER_DAO = new UserDAOSQL();
            AUTH_DAO = new AuthDAOSQL();
            GAME_DAO = new GameDAOSQL();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
