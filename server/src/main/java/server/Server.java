package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccessException;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (request, response) -> {
            response.type("application/json");
            HandlerResponse hresponse = ServerHandler.handleRegister(new Gson().fromJson(request.body(), RegisterRequest.class));
            response.status(hresponse.status());
            return new Gson().toJson(hresponse.response());
        });

        Spark.post("/session", (request, response) -> {
            response.type("application/json");
            HandlerResponse hresponse = ServerHandler.handleLogin(new Gson().fromJson(request.body(), LoginRequest.class));
            response.status(hresponse.status());
            return new Gson().toJson(hresponse.response());
        });

        Spark.delete("/session", (request, response) -> {
            response.type("application/json");
            HandlerResponse hresponse = ServerHandler.handleLogout(new LogoutRequest(request.headers("authorization")));
            response.status(hresponse.status());
            return new Gson().toJson(hresponse.response());
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
