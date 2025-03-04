package server;

import com.google.gson.Gson;
import requestmodel.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (request, response) -> {
            response.type("application/json");
            HandlerResponse hresponse = ServerHandler.handleDelete();
            response.status(hresponse.status());
            return new Gson().toJson(hresponse.response());
        });

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

        Spark.get("/game", (request, response) -> {
            response.type("application/json");
            HandlerResponse hresponse = ServerHandler.handleListGames(new ListGamesRequest(request.headers("authorization")));
            response.status(hresponse.status());
            return new Gson().toJson(hresponse.response());
        });

        Spark.post("/game", (request, response) -> {
            response.type("application/json");
            HandlerResponse hresponse = ServerHandler.handleCreateGame(request.headers("authorization"),
                    new Gson().fromJson(request.body(), CreateGameRequest.class));
            response.status(hresponse.status());
            return new Gson().toJson(hresponse.response());
        });

        Spark.put("/game", (request, response) -> {
            response.type("application/json");
            HandlerResponse hresponse = ServerHandler.handleJoinGame(request.headers("authorization"),
                    new Gson().fromJson(request.body(), JoinGameRequest.class));
            response.status(hresponse.status());
            return new Gson().toJson(hresponse.response());
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
