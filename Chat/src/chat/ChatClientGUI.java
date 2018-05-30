package chat;

import java.awt.BorderLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

class ChatFrame extends JFrame{
	JPanel pan=new JPanel();
	JTextField txtMsg=new JTextField(30);
	JButton btnSend=new JButton("Send");
	JButton btnExit=new JButton("Exit");
	List list=new List();
	Socket socket=null;
	public ChatFrame() {
		setLayout(new BorderLayout());
		pan.add(txtMsg);
		pan.add(btnSend);
		pan.add(btnExit);
		
		add(list,BorderLayout.CENTER);
		add(pan,BorderLayout.SOUTH);
		
		setSize(500, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		try{
			socket=new Socket("192.168.100.103", 3000);
			list.add("Server connection success.........");
			new RecThread().start();
			
		}catch(UnknownHostException ue){
			System.out.println(ue.getMessage());
			list.add("Server connection fail.........");
		}catch(IOException ie){
			System.out.println(ie.getMessage());
			list.add("Server connection fail.........");
		}
		
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					PrintWriter pw=new PrintWriter(socket.getOutputStream());
					String msg=txtMsg.getText();
					pw.println(msg);
					pw.flush();
					list.add("Me>>" + msg);
					list.select(list.getItemCount()-1);
				}catch(IOException ie){
					System.out.println(ie.getMessage());
				}
			}
		});	
	}
	class RecThread extends Thread{
		@Override
		public void run() {
			try{
				BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				while(true){
					String msg=br.readLine();
					if(msg==null){
						list.add("Server disconnected..");
						br.close();
						socket.close();
						System.exit(0);
					}
					list.add("Other person>>"+ msg);
					list.select(list.getItemCount()-1);			
				}
			}catch(IOException ie){
				System.out.println(ie.getMessage());
			}
		}
	}
}
public class ChatClientGUI {
	public static void main(String[] args) {
		new ChatFrame();
	}
}