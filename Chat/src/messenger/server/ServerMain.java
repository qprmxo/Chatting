package messenger.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import util.Protocol;

public class ServerMain implements Protocol {
	private static ServerSocket server = null;
	private static ConcurrentHashMap<Long, SYThread> thread_hm = null;
	private static final String user[] = { "Jin", "Kim", "Choi" };

	static class SYThread extends Thread {
		DataOutputStream dos = null;
		DataInputStream dis = null;
		private String name;
		private Socket client;

		ConcurrentHashMap<String, Socket> hm_th;

		public SYThread(ConcurrentHashMap<String, Socket> hs, Socket socket, String name) {
			try {
				this.client = socket;
				this.name = name;
				hm_th = hs;
				dos = new DataOutputStream(socket.getOutputStream());
				dos.writeInt(3);
				dos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void run() {
			try {
				sendProtocol(USER_UPDATE); 
				sendProtocol(USER_LIST);
				dis = new DataInputStream(client.getInputStream());
				while (!Thread.currentThread().isInterrupted()) {
					int protocol = dis.readInt();
					if (protocol != -1) {
						sendProtocol(protocol);
					}
				}

			} catch (IOException e) {

			} finally {
				try {
					exit();
					if (client != null) {
						client.close();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}

		public void sendProtocol(int protocol) throws IOException {

			Set<String> set = hm_th.keySet();
			Iterator<String> it1 = set.iterator();

			switch (protocol) {
			case USER_UPDATE:
				sendMsg(USER_UPDATE, name);
				break;

			case USER_LIST:
				dos = new DataOutputStream(client.getOutputStream());
				dos.writeInt(USER_LIST);
				dos.writeInt(hm_th.size());

				while (it1.hasNext()) {
					String name = it1.next();
					dos.writeUTF(name);
				}
				dos.flush();
				break;

			case CHAT:
				String msg = dis.readUTF();
				sendMsg(CHAT, msg);
				break;

			case USER_UPDATE_EXIT:
				exit();
				break;

			case MESSAGE:
				String recv_name = dis.readUTF();
				String recv_msg = dis.readUTF();
				sendMsg(MESSAGE, recv_msg, recv_name);
				break;
			default:
				break;
			}
		}

		public void sendMsg(int protocol, String msg) throws IOException {
			Collection<Socket> col = hm_th.values();
			Iterator<Socket> it = col.iterator();
			while (it.hasNext()) {
				Socket sock = it.next();
				if (sock != client) {
					dos = new DataOutputStream(sock.getOutputStream());
					dos.writeInt(protocol);
					dos.writeUTF(msg);
					dos.flush();
				}
			}
		}

		public void sendMsg(int protocol, String msg, boolean option) throws IOException {
			Collection<Socket> col = hm_th.values();
			Iterator<Socket> it = col.iterator();
			while (it.hasNext()) {
				Socket sock = it.next();
				dos = new DataOutputStream(sock.getOutputStream());
				dos.writeInt(protocol);
				dos.writeUTF(msg);
				dos.flush();
			}
		}

		public void sendMsg(int protocol, String msg, String recv_name) throws IOException {
			Socket sock = hm_th.get(recv_name);
			dos = new DataOutputStream(sock.getOutputStream());
			dos.writeInt(protocol);
			dos.writeUTF(name);
			dos.writeUTF(msg);
			dos.flush();
		}

		public void exit() throws IOException {
			SYThread stop = thread_hm.get(getId());
			if (stop != null) {
				hm_th.remove(name);
				System.out.println("disconnect : " + name);
				sendMsg(USER_UPDATE_EXIT, name, true);
				thread_hm.remove(getId());
				stop.interrupt();
			}
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println("Server starting");

			ConcurrentHashMap<String, Socket> hm = null;
			BufferedReader br = null;
			DataOutputStream dos = null;
			server = new ServerSocket(5555);
			hm = new ConcurrentHashMap<>();
			thread_hm = new ConcurrentHashMap<>();
			while (true) {
				boolean login = false;
				Socket socket = server.accept();
				br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String name = br.readLine();

				dos = new DataOutputStream(socket.getOutputStream());
				dos.writeInt(LOGIN);

				for (int i = 0; i < user.length; i++) {
					if (user[i].equals(name)) {
						login = true;
						if (hm.get(name) == null) {
							hm.put(name, socket);
							SYThread worker = new SYThread(hm, socket, name);
							worker.start();
							thread_hm.put(worker.getId(), worker);
							System.out.println("Client connect : " + socket.getInetAddress());
							continue;
						} else {
							dos.writeInt(1);
							dos.flush();
							socket.close();
							continue;
						}
					}
				}

				if (login == false) {
					dos.writeInt(2);
					dos.flush();
					socket.close();
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
