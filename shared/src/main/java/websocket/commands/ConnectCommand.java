package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final ConnectionType conn_type;
    public ConnectCommand(String authToken, Integer gameID, ConnectionType conn) {
        super(CommandType.CONNECT, authToken, gameID);
        this.conn_type = conn;
    }

    public ConnectionType getConnType() {
        return conn_type;
    }
}
