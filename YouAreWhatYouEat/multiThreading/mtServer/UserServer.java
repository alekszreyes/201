package mtServer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import user.RegisteredUser;
import user.UserList;

@ServerEndpoint (value="/ss", configurator = GetHttpSessionConfigurator.class)
public class UserServer {

	private static UserList users = new UserList();
	//private Vector<Session> sessions;
	private static ConcurrentHashMap<Session, Integer> sessions = new ConcurrentHashMap<>();
	private HttpSession httpSession;
	int id;
	public UserServer() {
		// TODO Auto-generated constructor stub
	}
	// TODO : Implement a function that allows the send a message to the user that calls from another servlet
	@OnOpen
	public void open(Session session, EndpointConfig config) {
		// TODO: retrieve the actual user from the user database and got it captured by this method
		// currently substituted with an imaginary user
		httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		this.id = (int) httpSession.getAttribute("userID");
		String email = (String) httpSession.getAttribute("userEmail");
		System.out.println("Connection from user id " + id);
		if (users.isActive(id)) {
			sessions.put(session, id);
			((RegisteredUser) users.getUser(id)).addSession();
		} else {
			users.addUser(new RegisteredUser(id, email));
			sessions.put(session, id);
			((RegisteredUser) users.getUser(id)).addSession();
		}
		
//		for (Map.Entry<Session, Integer> su: sessions.entrySet()) {
//			if (id != su.getValue()) {
//				try {
//					su.getKey().getBasicRemote().sendText(users.getUser(id).getUsername());
//				} catch (IOException ioe) {
//					System.out.println("ioe: " + ioe.getMessage());
//				}
//			}
//		}
		
		printSessionMap();
		users.printUserMap();
	}
	
	@OnMessage
	public void message(String message, Session session) {
		if (message.equals("login")) {
			message = users.getUser(id).getUsername() + " has just logged in!";
		}
		
		System.out.println("Received message from user " + id + ": " + message);
		for (Map.Entry<Session, Integer> su: sessions.entrySet()) {
//			if (((RegisteredUser) users.getUser(su.getValue())).isFollowing(id)) {
				try {
					su.getKey().getBasicRemote().sendText(message);
				} catch (IOException ioe) {
					System.out.println("ioe: " + ioe.getMessage());
				}
			//}
		}
	}
	
	@OnClose
	public void close(Session session) {
		System.out.println("Disconnection from id: " + id);
		if (session == null) {
			System.out.println("Note: Close method called on null object");
			return;
		}
		sessions.remove(session);
		if (!users.isActive(id)) {
			System.out.println("Note: user inteferes with open process.");
			return;
		}
		((RegisteredUser) users.getUser(id)).removeSession();
		if (((RegisteredUser) users.getUser(id)).noSession()) {
			users.removeUser(id);
		}
		
		
	}
	
	@OnError
	public void error(Throwable error) {
		System.out.println("Error!");
		error.printStackTrace(System.out);
	}
	
	// Debug Helper
	public void printSessionMap() {
		for (Map.Entry<Session, Integer> su: sessions.entrySet()) {
			System.out.println(su.getKey().getId() + ": id=" + su.getValue());
		}
	}

}
