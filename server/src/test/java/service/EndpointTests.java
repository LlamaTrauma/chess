package service;

import dataaccess.DAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import requestmodel.*;

public class EndpointTests {
    @Test
    public void testRegisterHappy(){
        RegisterRequest testReq = new RegisterRequest("test_user", "password", "a@b.c");
        try {
            Service.delete();
            Service.register(testReq);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail("fail");
        }
    }

    @Test
    public void testLoginHappy(){
        LoginRequest testLogin = new LoginRequest("test_user", "password");
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", BCrypt.hashpw("password", BCrypt.gensalt()), ""));
            Service.login(testLogin);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail("fail");
        }
    }

    @Test
    public void testLogoutHappy(){
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", BCrypt.hashpw("password", BCrypt.gensalt()), ""));
            String authToken = DAO.AUTH_DAO.createAuth("test_user");
            Service.logout(new LogoutRequest(authToken));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail("fail");
        }
    }

    @Test
    public void testCreateGameHappy(){
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", "password", ""));
            String authToken = DAO.AUTH_DAO.createAuth("test_user");
            Service.createGame(authToken, new CreateGameRequest("game"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail("fail");
        }
    }

    @Test
    public void testListGamesHappy(){
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", "password", ""));
            String authToken = DAO.AUTH_DAO.createAuth("test_user");
            Service.createGame(authToken, new CreateGameRequest("game"));
            ListGamesResult result = Service.listGames(new ListGamesRequest(authToken));
            Assertions.assertEquals("game", result.games().getFirst().gameName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail("fail");
        }
    }

    @Test
    public void testJoinGameHappy(){
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", "password", ""));
            String authToken = DAO.AUTH_DAO.createAuth("test_user");
            CreateGameResult createdGame = Service.createGame(authToken, new CreateGameRequest("game"));
            Service.joinGame(authToken, new JoinGameRequest("BLACK", createdGame.gameID()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail("fail");
        }
    }

    @Test
    public void testDeleteHappy(){
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", "password", ""));
            Service.delete();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Assertions.fail("fail");
        }
    }

    @Test
    public void testRegisterFail(){
        RegisterRequest testReq = new RegisterRequest("test_user", "password", "a@b.c");
        try {
            Service.delete();
            Service.register(testReq);
        } catch (Exception e) {
            Assertions.fail();
        }
        RegisterRequest testReq2 = new RegisterRequest("test_user", "password", "a@b.c");
        try {
            Service.register(testReq2);
            Assertions.fail();
        } catch (Exception ignored) {}
    }

    @Test
    public void testLoginFail(){
        LoginRequest testLogin = new LoginRequest("test_user", "password");
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", "passwordd", ""));
            Service.login(testLogin);
            Assertions.fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testLogoutFail(){
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", "password", ""));
            Service.logout(new LogoutRequest("invalid"));
            Assertions.fail("fail");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testCreateGameFail(){
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", "password", ""));
            String authToken = DAO.AUTH_DAO.createAuth("test_user");
            Service.createGame(authToken, new CreateGameRequest(null));
            Assertions.fail("fail");
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testListGamesFail(){
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", "password", ""));
            String authToken = DAO.AUTH_DAO.createAuth("test_user");
            Service.createGame(authToken, new CreateGameRequest("game"));
            Service.listGames(new ListGamesRequest("invalid"));
            Assertions.fail("fail");
        } catch (Exception ignored) {

        }
    }

    @Test
    public void testJoinGameFail(){
        try {
            Service.delete();
            DAO.USER_DAO.createUser(new UserData("test_user", "password", ""));
            String authToken = DAO.AUTH_DAO.createAuth("test_user");
            CreateGameResult createdGame = Service.createGame(authToken, new CreateGameRequest("game"));
            Service.joinGame(authToken, new JoinGameRequest("BLACK", createdGame.gameID()+1));
            Assertions.fail("fail");
        } catch (Exception ignored) {

        }
    }
}
