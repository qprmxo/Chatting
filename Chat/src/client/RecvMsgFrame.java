package client;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import util.Protocol;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.Font;

public class RecvMsgFrame extends JFrame implements Protocol {
	DataOutputStream dos = null;
	private JButton btn_send;
	private JTextArea txt_msg;
	private JLabel lbl_sender;

	public RecvMsgFrame(String msg, String sender_name) {
		setTitle(sender_name + "'s Message");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 59, 410, 228);
		getContentPane().add(scrollPane);
		setResizable(false);
		txt_msg = new JTextArea(msg);
		txt_msg.setBorder(
				BorderFactory.createCompoundBorder(txt_msg.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		txt_msg.setEnabled(false);
		txt_msg.setLineWrap(true);
		txt_msg.setDisabledTextColor(Color.BLACK);

		scrollPane.setViewportView(txt_msg);

		JLabel lbl_recver = new JLabel("Take person : " + Session.getName());
		lbl_recver.setBounds(12, 35, 410, 15);
		getContentPane().add(lbl_recver);

		btn_send = new JButton("Answer (Ctrl+R)");
		btn_send.addKeyListener(new KeyListener() {
			@Override
			public synchronized void keyPressed(KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
					exitMsg(sender_name);
				}

				if ((e.getKeyCode() == KeyEvent.VK_ESCAPE)) {
					disPose();
				}
			}

			@Override
			public synchronized void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {

			}
		});
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitMsg(sender_name);
			}
		});
		btn_send.setBounds(313, 297, 109, 25);
		getContentPane().add(btn_send);

		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sf.format(date);
		lbl_sender = new JLabel("Send person : " + sender_name + "    " + "(" + str + ")");
		lbl_sender.setBounds(12, 10, 410, 15);
		getContentPane().add(lbl_sender);

		setAlwaysOnTop(true);
		toFront();
		setSize(450, 370);
		setVisible(true);
	}

	public void sendMsg(String name) {
		try {
			dos = new DataOutputStream(Session.getClient().getOutputStream());
			dos.writeInt(MESSAGE);
			dos.writeUTF(name); 
			dos.writeUTF(txt_msg.getText());
			dos.flush();
			this.dispose();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void disPose() {
		this.dispose();
	}

	public void exitMsg(String sender_name) {
		new SendMsgFrame(sender_name,this);
		// this.dispose();
	}
}
