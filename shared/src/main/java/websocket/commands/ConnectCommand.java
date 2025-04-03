package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    private final ConnectionType conn_type;
    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, ConnectionType conn) {
        super(commandType, authToken, gameID);
        this.conn_type = conn;
    }

    public ConnectionType getConnType() {
        return conn_type;
    }
}
