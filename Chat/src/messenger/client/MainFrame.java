package messenger.client;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import util.Protocol;

public class MainFrame extends JFrame implements Protocol {

	private JList<String> list_chat;
	private JList<String> list_user;
	private JList<String> list_off;
	private DefaultListModel<String> model_chat;
	private DefaultListModel<String> model_user;
	private DefaultListModel<String> model_off;
	private JTextField txt_msg;
	private JButton btn_send;
	private DataOutputStream dos = null;
	private String name;
	private Socket socket;
	private WorkerThread thread;
	private JScrollPane scrollPane_2;
	private JLabel label;
	private static final String user[] = { "Jin", "Kim", "Choi" };
	private JLabel lbl_user;
	private int count = -1;

	public MainFrame(Socket socket, String name) {
		this.name = name;
		this.socket = socket;
		model_chat = new DefaultListModel<>();
		model_user = new DefaultListModel<>();
		model_off = new DefaultListModel<>();
		setTitle("SY system");
		setVisible(true);
		setSize(596, 500);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 411, 400);
		getContentPane().add(scrollPane);

		list_chat = new JList<String>(model_chat);

		scrollPane.setViewportView(list_chat);

		JScrollPane scrollPane_1 = new JScrollPane();

		scrollPane_1.setBounds(445, 43, 120, 182);
		getContentPane().add(scrollPane_1);

		list_off = new JList<String>(model_off);
		list_user = new JList<String>(model_user);
		list_user.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JList list = (JList) e.getSource();
				if (e.getClickCount() == 2) {
					int index = list.locationToIndex(e.getPoint());
					String str = model_user.getElementAt(index);
					if (!str.contains("*")) {
						new SendMsgFrame(str, null);
					}
				}
			}
		});
		scrollPane_1.setViewportView(list_user);

		lbl_user = new JLabel("Now (" + count + "/16)");

		lbl_user.setBounds(460, 14, 105, 15);
		getContentPane().add(lbl_user);

		txt_msg = new JTextField();
		txt_msg.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					send();
				}
			}
		});

		txt_msg.setBounds(12, 431, 304, 21);
		getContentPane().add(txt_msg);
		txt_msg.setColumns(10);

		btn_send = new JButton("Send");
		btn_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		btn_send.setBounds(328, 430, 97, 23);
		getContentPane().add(btn_send);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(445, 270, 120, 182);
		getContentPane().add(scrollPane_2);

		scrollPane_2.setViewportView(list_off);

		label = new JLabel("Offline");
		label.setBounds(475, 245, 57, 15);
		getContentPane().add(label);
		setLocationRelativeTo(null);
		Windows l = new Windows();
		addWindowListener(l);
		setResizable(false);
		// 오프라인 목록 등록
		for (int i = 0; i < user.length; i++) {
			model_off.addElement(user[i]);
		}

		thread = new WorkerThread(socket);
		thread.start();
		try {
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		final PopupMenu popup = new PopupMenu();

		final TrayIcon trayIcon = new TrayIcon(new ImageIcon("Penguins.jpg", "tray icon").getImage());
		final SystemTray tray = SystemTray.getSystemTray();

		MenuItem openItem = new MenuItem("Open");
		MenuItem exitItem = new MenuItem("Exit");

		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("SY system");

		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {

				int modifiers = mouseEvent.getModifiers();
				if ((modifiers & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) {
					setVisible(true);
				}

			}
		});

		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setAlwaysOnTop(true);
				setVisible(true);
			}
		});

		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					dos.writeInt(USER_UPDATE_EXIT);
					dos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				thread.interrupt();
				setDefaultCloseOperation(EXIT_ON_CLOSE);
				System.exit(1);
			}
		});
		popup.add(openItem);
		popup.addSeparator();
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}

	}

	public void send() {
		String msg = name + " : " + txt_msg.getText();

		try {
			model_chat.addElement(msg);
			list_chat.ensureIndexIsVisible(model_chat.size() - 1);
			txt_msg.setText("");

			dos.writeInt(CHAT);
			dos.writeUTF(msg);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exit() {

		try {
			dos.writeInt(CHAT);
			dos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class WorkerThread extends Thread {
		Socket client;
		DataInputStream dis = null;

		public WorkerThread(Socket socket) {
			this.client = socket;
		}

		@Override
		public void run() {
			try {
				dis = new DataInputStream(client.getInputStream());
				while (!Thread.currentThread().isInterrupted()) {
					int protocol = dis.readInt();
					if (protocol == -1) {
						if (socket != null) {
							socket.close();
							break;
						}
					}
					switch (protocol) {
					case USER_LIST:
						int length = dis.readInt();

						for (int i = 0; i < length; i++) {
							String name = dis.readUTF();
							for (int j = 0; j < model_off.size(); j++) {
								if (name.equals(model_off.get(j))) {
									model_off.remove(j);
									break;
								}
							}

							if (MainFrame.this.name.equals(name)) {
								model_user.add(0, name + " (*)");
							} else {
								model_user.addElement(name);
							}
							count++;
						}
						lbl_user.setText("Now (" + (++count) + "/16)");
						break;

					case USER_UPDATE:
						String name = dis.readUTF();
						model_user.addElement(name);
						lbl_user.setText("Now (" + (++count) + "/16)");

						for (int i = 0; i < model_off.size(); i++) {
							if (name.equals(model_off.get(i))) {
								model_off.remove(i);
								break;
							}
						}
						break;

					case CHAT:
						String msg = dis.readUTF();
						model_chat.addElement(msg);
						list_chat.ensureIndexIsVisible(model_chat.size() - 1);
						break;

					case USER_UPDATE_EXIT:
						String exit_name = dis.readUTF();
						int list_length = model_user.size();

						lbl_user.setText("Now (" + (--count) + "/16)");
						for (int i = 0; i < list_length; i++) {
							if (exit_name.equals(model_user.get(i))) {
								model_user.remove(i);
								break;
							}
						}

						model_off.addElement(exit_name);
						break;

					case MESSAGE:
						String sender_name = dis.readUTF();
						String send_msg = dis.readUTF();
						new RecvMsgFrame(send_msg, sender_name);
						break;
					default:

						break;
					}

				}
			} catch (IOException e) {

			} finally {
				try {
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class Windows extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {

		}

	}
}
