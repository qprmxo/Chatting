package messenger.client;

import java.net.Socket;

public class Session {
	private static Session session = new Session();
	private static String name;
	private static Socket client;

	public static void setSession(Socket client, String name) {
		Session.name = name;
		Session.client = client;
	}

	public static String getName() {
		return name;
	}

	public static Socket getClient() {
		if (client.isConnected() && !client.isClosed()) {
			return client;
		} else {
			return null;
		}
	}

}
