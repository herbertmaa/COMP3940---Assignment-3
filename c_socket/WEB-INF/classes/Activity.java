import java.io.IOException;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.util.Scanner;

public class Activity {

	private static final String uri = "ws://localhost:8080/socket/websocket/chat";
	public static void main(String[] args) throws DeploymentException, IOException {
		
		Scanner keyboard = new Scanner(System.in);

		boolean connected = false;
		// TODO Auto-generated method stub
	    final WebSocketContainer container = ContainerProvider.getWebSocketContainer();
	    if(container == null) {
	    	System.out.println("NULL POINTER");
	    	System.exit(1);
	    }
	    Session session = container.connectToServer(new Endpoint() {

			@Override
			public void onOpen(Session arg0, EndpointConfig arg1) {
				System.out.println("Connected to " + uri);
			}

	    	
	    }, URI.create(uri));
	    
	    
	    session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				System.out.println("Message from the server: " + message);
			}
		});
	    
	    
	    while(session.isOpen()) {
	    	String myMessage = keyboard.nextLine();
	    	session.getBasicRemote().sendText(myMessage);
	    }

	}

}
