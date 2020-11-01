
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket/chat")
public class SocketServer {
	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());

	@OnOpen
	public void onOpen(Session session) throws IOException {

		clients.add(session);

		synchronized (clients) {
			// Iterate over the connected sessions
			// and broadcast the received message
			for (Session client : clients) {
				if (!client.equals(session)) {
					client.getBasicRemote().sendText(session.toString() + " connected to the chat");
				}
			}
		}
		session.getBasicRemote().sendText("Welcome to miRC");
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		
		synchronized (clients) {
			// Iterate over the connected sessions
			// and broadcast the received message
			for (Session client : clients) {
				if (!client.equals(session)) {
					client.getBasicRemote().sendText(session.toString() + " has left to the chat");
				}
			}
		}
		
		clients.remove(session);
		System.out.println("Close Connection ...");
	}

	@OnMessage
	public void onMessage(String message, Session session) throws IOException {

		synchronized (clients) {
			// Iterate over the connected sessions
			// and broadcast the received message
			for (Session client : clients) {
				if (!client.equals(session)) {
					client.getBasicRemote().sendText(message);
				}
			}
		}
	}

	@OnError
	public void onError(Throwable e) {
		e.printStackTrace();
	}

}
