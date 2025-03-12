package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.Service;
import service.UnauthorizedException;

public class DAOTests {
    @Test
    public void testCreateAuthHappy () {
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("username", "pw", "dasf"));
            DAO.AUTH_DAO.createAuth("username");
        } catch (DataAccessException e) {
            Assertions.fail("fail");
        }
    }

    @Test
    public void testCreateAuthFail () {
        try {
            Service.delete();
            DAO.AUTH_DAO.createAuth("username".repeat(100));
            Assertions.fail("fail");
        } catch (DataAccessException ignored) {
        }
    }

    @Test
    public void testValidateAuthHappy () {
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("username", "pw", "dasf"));
            String auth = DAO.AUTH_DAO.createAuth("username");
            DAO.AUTH_DAO.validateAuth(auth);
        } catch (DataAccessException e) {
            Assertions.fail("fail");
        }
    }

    @Test
    public void testValidateAuthFail () {
        try {
            DAO.USER_DAO.createUser(new UserData("user", "pw", "dasf"));
            String auth = DAO.AUTH_DAO.createAuth("username");
            DAO.AUTH_DAO.validateAuth(auth.repeat(2));
            Assertions.fail("fail");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testDeleteAuthHappy () {
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("username", "pw", "dasf"));
            String auth = DAO.AUTH_DAO.createAuth("username");
            DAO.AUTH_DAO.deleteAuth(auth);
        } catch (DataAccessException e) {
            Assertions.fail("fail");
        }
    }

    @Test
    public void testDeleteAuthFail () {
        try {
            DAO.USER_DAO.createUser(new UserData("user", "pw", "dasf"));
            String auth = DAO.AUTH_DAO.createAuth("username");
            DAO.AUTH_DAO.deleteAuth(auth.repeat(2));
            Assertions.fail("fail");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testDeleteAuths() {
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("username", "pw", "dasf"));
            String auth = DAO.AUTH_DAO.createAuth("username");
            DAO.AUTH_DAO.deleteAuths();
            DAO.AUTH_DAO.validateAuth(auth);
            Assertions.fail("fail");
        } catch (UnauthorizedException ignored) {
        } catch (DataAccessException ignored) {
            Assertions.fail("fail");
        }
    }

    @Test
    public void testCreateGameHappy() {
        try {
            Service.delete();
            DAO.GAME_DAO.createGame("game");
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void testCreateGameFail() {
        try {
            Service.delete();
            DAO.GAME_DAO.createGame("game".repeat(100));
            Assertions.fail("fail");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testListGamesHappy() {
        try {
            Service.delete();
            DAO.GAME_DAO.createGame("game");
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void testListGamesFail() {
        try {
            Service.delete();
            DAO.GAME_DAO.createGame("game".repeat(100));
            Assertions.fail("fail");
        } catch (Exception ignored) {
        }
    }
}
