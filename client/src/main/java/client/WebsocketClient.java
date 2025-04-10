package client;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketClient extends Endpoint {
    int port;
    public Session session;

    public WebsocketClient (int port, Client client) throws URISyntaxException, DeploymentException, IOException {
        this.port = port;
        URI uri = new URI("ws://localhost:" + String.valueOf(port) + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ClientWebsocketHandler.handleMessage(client, message);
            }

            @OnError
            public void onError() {
                System.out.println("Error in websocket client");
            }
        });
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void send(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
    }
}
