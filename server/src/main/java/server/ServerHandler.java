package server;

import dataaccess.DataAccessException;
import service.RequestException;
import service.TakenException;
import service.UnauthorizedException;
import service.Service;

public class ServerHandler {
    public static HandlerResponse handleRegister(RegisterRequest req) {
        int status;
        ResponseBody body;
        try {
            status = 200;
            body = Service.register(req);
        } catch (RequestException e) {
            status = 400;
            body = new StandardResponse(400);
        } catch (TakenException e) {
            status = 403;
            body = new StandardResponse(403);
        } catch (DataAccessException e) {
            status = 500;
            body = new StandardResponse("Error: internal server error");
        } catch (Exception e) {
            status = 500;
            body = new StandardResponse("Error: unhandled exception");
        }
        return new HandlerResponse(status, body);
    }

    public static HandlerResponse handleLogin(LoginRequest req) {
        int status;
        ResponseBody body;
        try {
            status = 200;
            body = Service.login(req);
        } catch (UnauthorizedException e) {
            status = 401;
            body = new StandardResponse(401);
        } catch (DataAccessException e) {
            status = 500;
            body = new StandardResponse("Error: internal server error");
        } catch (Exception e) {
            status = 500;
            body = new StandardResponse("Error: unhandled exception");
        }
        return new HandlerResponse(status, body);
    }

    public static HandlerResponse handleLogout(LogoutRequest req) {
        int status;
        ResponseBody body;
        try {
            status = 200;
            body = Service.logout(req);
        } catch (UnauthorizedException e) {
            status = 401;
            body = new StandardResponse(401);
        } catch (DataAccessException e) {
            status = 500;
            body = new StandardResponse("Error: internal server error");
        } catch (Exception e) {
            status = 500;
            body = new StandardResponse("Error: unhandled exception");
        }
        return new HandlerResponse(status, body);
    }

    public static HandlerResponse handleListGames(ListGamesRequest req) {
        int status;
        ResponseBody body;
        try {
            status = 200;
            body = Service.listGames(req);
        } catch (UnauthorizedException e) {
            status = 401;
            body = new StandardResponse(401);
        } catch (DataAccessException e) {
            status = 500;
            body = new StandardResponse("Error: internal server error");
        } catch (Exception e) {
            status = 500;
            body = new StandardResponse("Error: unhandled exception");
        }
        return new HandlerResponse(status, body);
    }

    public static HandlerResponse handleCreateGame(ListGamesRequest req) {
        int status;
        ResponseBody body;
        try {
            status = 200;
            body = Service.createGame(req);
        } catch (UnauthorizedException e) {
            status = 401;
            body = new StandardResponse(401);
        } catch (DataAccessException e) {
            status = 500;
            body = new StandardResponse("Error: internal server error");
        } catch (Exception e) {
            status = 500;
            body = new StandardResponse("Error: unhandled exception");
        }
        return new HandlerResponse(status, body);
    }
}
