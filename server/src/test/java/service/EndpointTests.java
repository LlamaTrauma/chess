package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.HandlerResponse;
import server.RegisterRequest;
import server.RegisterResult;
import server.ServerHandler;

public class EndpointTests {
    @Test
    public void testRegisterHappy(){
        RegisterRequest testReq = new RegisterRequest("test_user", "password", "a@b.c");
        try {
            UserService.register(testReq);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void testRegisterFail(){
        RegisterRequest testReq = new RegisterRequest("test_user", "password", "a@b.c");
        try {
            UserService.register(testReq);
        } catch (Exception e) {
            Assertions.fail();
        }
        RegisterRequest testReq2 = new RegisterRequest("test_user", "password", "a@b.c");
        try {
            UserService.register(testReq2);
            Assertions.fail();
        } catch (Exception ignored) {}
    }
}
