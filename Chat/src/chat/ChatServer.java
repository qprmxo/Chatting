package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

class EchoThread extends Thread{
	Vector<Socket> list;
	Socket socket;	
	public EchoThread(Vector<Socket> list,Socket socket) {
		this.list=list;
		this.socket=socket;
	}
	@Override
	public void run() {
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				String msg=br.readLine();
				if(msg==null){
					System.out.println("Client Lost...");
					list.remove(socket);
					socket.close();
					break;
				}
				sendMsg(msg);
			}
			
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
	public void sendMsg(String msg) throws IOException{
		for(Socket sock:list){
			if(socket!=sock){
				PrintWriter pw=new PrintWriter(sock.getOutputStream());
				pw.println(msg);
				pw.flush();
			}
		}
			
	}
}

public class ChatServer {
	public static void main(String[] args) {
		ServerSocket server=null;
		Vector<Socket> list=new Vector<Socket>();
		try{
			server=new ServerSocket(3000);
			while(true){
				System.out.println("Server starting......");
				Socket socket=server.accept();
				list.add(socket);
				InetAddress ia=socket.getInetAddress();
				System.out.println("[" + ia.getHostAddress() +"]Client connected!");
				new EchoThread(list, socket).start();
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
