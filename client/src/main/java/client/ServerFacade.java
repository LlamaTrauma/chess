package client;

import chess.ChessGame;
import com.google.gson.Gson;
import requestmodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverURL = "http://localhost:8080";

    public RegisterResult registerRequest(String username, String password, String email) {
        return makeRequest("POST", "/user", new RegisterRequest(username, password, email), null, RegisterResult.class);
    }

    public LoginResult loginRequest(String username, String password) {
        return makeRequest("POST", "/session", new LoginRequest(username, password), null, LoginResult.class);
    }

    public void logoutRequest(String authToken) {
        makeRequest("DELETE", "/session", new LogoutRequest(authToken), authToken, LogoutResult.class);
    }

    public CreateGameResult createRequest(String authToken, String name) {
        return makeRequest("POST", "/game", new CreateGameRequest(name), authToken, CreateGameResult.class);
    }

    public void playRequest(String authToken, String team, int id) {
        makeRequest("PUT", "/game", new JoinGameRequest(team, id), authToken, JoinGameResult.class);
    }

    public ListGamesResult listRequest(String authToken) {
        return makeRequest("GET", "/game", new ListGamesRequest(authToken), authToken, ListGamesResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseClass) throws RuntimeException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if(method.equalsIgnoreCase("GET")) {
                http.setDoInput(true);
            } else {
                http.setDoOutput(true);
            }

            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }
            if(!method.equalsIgnoreCase("GET")) {
                writeBody(http, request);
            }
            http.connect();
            validateHttpSuccess(http);

            T response = readBody(http, responseClass);
            http.disconnect();
            return response;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new RuntimeException(e);
        }
    }

    private void validateHttpSuccess(HttpURLConnection http) throws IOException {
        var code = http.getResponseCode();
        if (code != 200) {
            throw new RuntimeException("Bad response");
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() == -1) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private void writeBody(HttpURLConnection http, Object request) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String data = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(data.getBytes());
            }
        }
    }
}
