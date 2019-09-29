import java.net.URI;
import java.net.URISyntaxException;

public class Start {
    public static void main(String[] args) {
        try {
            // open websocket
            final WebSocketClient clientEndPoint = new WebSocketClient(new URI("ws://echo.websocket.org"));

            // add listener
            clientEndPoint.addMessageHandler(new WebSocketClient.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println(message);
                }
            });

            // send message to websocket
            clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");

            // wait 5 seconds for messages from websocket
            Thread.sleep(50000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}
