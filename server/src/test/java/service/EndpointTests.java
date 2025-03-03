package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.RegisterRequest;

public class EndpointTests {
    @Test
    public void testRegisterHappy(){
        RegisterRequest testReq = new RegisterRequest("test_user", "password", "a@b.c");
        try {
            Service.register(testReq);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    public void testRegisterFail(){
        RegisterRequest testReq = new RegisterRequest("test_user", "password", "a@b.c");
        try {
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
}
