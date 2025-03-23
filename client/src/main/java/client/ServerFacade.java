package client;

import com.google.gson.Gson;
import requestmodel.LoginRequest;
import requestmodel.LoginResult;
import requestmodel.RegisterRequest;
import requestmodel.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverURL = "";

    public RegisterResult registerRequest(String username, String password, String email) {
        return makeRequest("POST", "/user", new RegisterRequest(username, password, email), RegisterResult.class);
    }

    public LoginResult loginRequest(String username, String password) {
        return makeRequest("POST", "/session", new LoginRequest(username, password), LoginResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(http, request);
            http.connect();
            validateHttpSuccess(http);

            return readBody(http, responseClass);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
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
