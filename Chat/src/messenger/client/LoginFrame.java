package messenger.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import util.Protocol;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {
	private JTextField txt_name;
	private Socket socket = null;
	private JLabel lbl_state;

	public LoginFrame() {
		setTitle("SY system");
		getContentPane().setLayout(null);
		setResizable(false);
		txt_name = new JTextField();
		txt_name.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				txt_name.setText("");
			}
		});
		txt_name.setBounds(101, 50, 130, 21);
		getContentPane().add(txt_name);
		txt_name.setColumns(10);

		JLabel label = new JLabel("name");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(46, 53, 57, 15);
		getContentPane().add(label);

		JLabel lblHta = new JLabel("SY system");
		lblHta.setForeground(new Color(255, 140, 0));
		lblHta.setHorizontalAlignment(SwingConstants.CENTER);
		lblHta.setBounds(118, 10, 98, 15);
		getContentPane().add(lblHta);

		JButton btn_login = new JButton("login");
		btn_login.setBounds(118, 92, 97, 23);
		getContentPane().add(btn_login);

		lbl_state = new JLabel("");
		lbl_state.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_state.setBounds(75, 135, 182, 15);
		getContentPane().add(lbl_state);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setSize(324, 196);

		btn_login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					login();
				} catch (IOException e1) {
					lbl_state.setText(e1.getMessage());
				}
			}
		});

		KeyAdapter l = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						login();
					} catch (IOException e1) {
						lbl_state.setText(e1.getMessage());
					}
				}
			}

		};
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		txt_name.addKeyListener(l);
	}

	private void login() throws IOException {
		String name = txt_name.getText();
		PrintWriter pw = null;
		DataInputStream dis = null;

		try {
			if (name.length() == 0) {
				lbl_state.setText("Write name!");
				return;
			}
			// socket = new Socket("127.0.0.1", 5555);
			socket = new Socket("192.168.100.103", 5555);
			// socket = new Socket("1.240.130.211", 5555);
			// socket = new Socket("192.168.0.21", 5555);
			pw = new PrintWriter(socket.getOutputStream(), true);
			dis = new DataInputStream(socket.getInputStream());
		} catch (UnknownHostException e1) {
			lbl_state.setText(e1.getMessage());
			txt_name.setText("");
			return;
		} catch (IOException e2) {
			lbl_state.setText(e2.getMessage());
			txt_name.setText("");
			return;
		}

		lbl_state.setText("Now loading");
		pw.println(name);

		try {
			int a = dis.readInt();
			if (a == Protocol.LOGIN) {
				int ret = dis.readInt();
				switch (ret) {
				case 1: 
					lbl_state.setText("Already login name!");
					socket.close();
					return;
				case 2: 
					lbl_state.setText("Not exist name");
					socket.close();
					return;
				case 3: 
					Session.setSession(socket, name);
					new MainFrame(Session.getClient(), Session.getName());
					setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					this.dispose();
					break;
				}

			} else {
				lbl_state.setText("Failed login!");
				socket.close();
				return;
			}

		} catch (IOException e) {
			socket.close();
		}
	}

}
