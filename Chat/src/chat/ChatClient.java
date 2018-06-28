package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

class SendThread extends Thread{
	private Socket socket;
	public SendThread(Socket socket){
		this.socket=socket;
	}
	@Override
	public void run() {
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		try{
			PrintWriter pw=new PrintWriter(socket.getOutputStream());
			while(true){
				String msg=br.readLine();
				if(msg.equals("exit")){
					System.out.println("ByeBye...");
					pw.close();
					socket.close();
					System.exit(0);
				}
				pw.println(msg);
				pw.flush();
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}

class RecThread extends Thread{
	private Socket socket;
	public RecThread(Socket socket){
		this.socket=socket;
	}
	@Override
	public void run() {
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while(true){
				String msg=br.readLine();
				if(msg==null){
					System.out.println("Server disconnected...");
					br.close();
					socket.close();
					System.exit(0);
				}
				System.out.println("Other person>>" + msg);
			}
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
public class ChatClient {
	public static void main(String[] args) {
		Socket socket=null;
		try{
			socket=new Socket("192.168.17.221",3000);
			System.out.println("Server connection success!!..........");
			new SendThread(socket).start();
			new RecThread(socket).start();
		}catch(UnknownHostException ue){
			System.out.println(ue.getMessage());
		}catch(IOException ie){
			System.out.println(ie.getMessage());
		}
	}
}
