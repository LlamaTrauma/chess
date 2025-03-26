package client;

import dataaccess.DAO;
import org.junit.jupiter.api.*;
import requestmodel.LoginResult;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade();
        try {
            DAO.GAME_DAO.deleteGames();
            DAO.AUTH_DAO.deleteAuths();
            DAO.USER_DAO.deleteUsers();
        } catch (Exception ignored){

        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerRequestHappy() {
        try {
            facade.registerRequest("a", "b", "c");
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void registerRequestFail() {
        try {
            facade.registerRequest("a", "b", "c");
            facade.registerRequest("a", "b", "c");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void loginRequestHappy() {
        try {
            facade.registerRequest("a", "b", "c");
            facade.loginRequest("a", "b");
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void loginRequestFail() {
        try {
            facade.registerRequest("a", "b", "c");
            facade.loginRequest("a", "c");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void logoutRequestHappy() {
        try {
            facade.registerRequest("a", "b", "c");
            String auth = facade.loginRequest("a", "b").authToken();
            facade.logoutRequest(auth);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void logoutRequestFail() {
        try {
            facade.registerRequest("a", "b", "c");
            String auth = facade.loginRequest("a", "b").authToken();
            facade.logoutRequest(auth + "1");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void createRequestHappy() {
        try {
            facade.registerRequest("a", "b", "c");
            String auth = facade.loginRequest("a", "b").authToken();
            facade.createRequest(auth, "game");
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void createRequestFail() {
        try {
            facade.registerRequest("a", "b", "c");
            String auth = facade.loginRequest("a", "b").authToken();
            facade.createRequest(auth, "game");
            facade.createRequest(auth, "game");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void playRequestHappy() {
        try {
            facade.registerRequest("a", "b", "c");
            String auth = facade.loginRequest("a", "b").authToken();
            int id = facade.createRequest(auth, "game").gameID();
            facade.playRequest(auth, "white", id);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void playRequestFail() {
        try {
            facade.registerRequest("a", "b", "c");
            String auth = facade.loginRequest("a", "b").authToken();
            int id = facade.createRequest(auth, "game").gameID();
            facade.playRequest(auth, "whitee", id);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void listRequestHappy() {
        try {
            facade.registerRequest("a", "b", "c");
            String auth = facade.loginRequest("a", "b").authToken();
            facade.listRequest(auth);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void listRequestFail() {
        try {
            facade.registerRequest("a", "b", "c");
            String auth = facade.loginRequest("a", "b").authToken();
            facade.listRequest(auth + "1");
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }
}
