package client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import util.Protocol;

public class SendMsgFrame extends JFrame implements Protocol {
	DataOutputStream dos = null;
	private JButton btn_send;
	private JTextArea txt_msg;
	private RecvMsgFrame recvMsgFrame;

	public SendMsgFrame(String name, RecvMsgFrame recvMsgFrame) {

		this.recvMsgFrame = recvMsgFrame;

		setTitle("Write message");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setResizable(false);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 34, 410, 233);
		getContentPane().add(scrollPane);

		txt_msg = new JTextArea();
		txt_msg.setBorder(
				BorderFactory.createCompoundBorder(txt_msg.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		txt_msg.addKeyListener(new KeyListener() {
			@Override
			public synchronized void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					sendMsg(name);
				}
			}

			@Override
			public synchronized void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
		});

		txt_msg.setLineWrap(true);
		scrollPane.setViewportView(txt_msg);

		JLabel lbl_reciver = new JLabel("Take person : " + name);
		lbl_reciver.setBounds(12, 10, 166, 15);
		getContentPane().add(lbl_reciver);

		btn_send = new JButton("Send (Ctrl+S)");

		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMsg(name);
			}
		});
		btn_send.setBounds(299, 277, 123, 25);
		getContentPane().add(btn_send);

		setSize(450, 350);
		setVisible(true);
		setLocationRelativeTo(null);

	}

	public void sendMsg(String name) {
		try {
			dos = new DataOutputStream(Session.getClient().getOutputStream());
			dos.writeInt(MESSAGE);
			dos.writeUTF(name); 
			dos.writeUTF(txt_msg.getText());
			dos.flush();
			if (recvMsgFrame != null) {
				recvMsgFrame.dispose();
			}
			this.dispose();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
