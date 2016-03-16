
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {
	public static void main(String[] args) {
		Gui g = new Gui();
		g.setVisible(true);
	}
}

class Gui extends JFrame {
	private final JTextField host;
	private final JTextField port;
	private final JTextArea setT;
	private final JTextArea getT;
	private final JTextArea remT;
	private final JTextArea resT;
	private final JLabel portL = new JLabel("Port Number: ");
	private Socket client;
	private OutputStream toServer;
	private DataOutputStream out;
	private InputStream fromServer;
	private DataInputStream in;

	public Gui() {
		super();
		this.setResizable(false);
		setSize(470, 400);
		setTitle("Client");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new FlowLayout(10, 10, 10));
		setBackground(Color.WHITE);
		host = new JTextField(10);
		port = new JTextField(10);
		host.setText("localhost");
		port.setText("5000");
		setT = new JTextArea(2, 26);
		getT = new JTextArea(2, 26);
		remT = new JTextArea(2, 26);
		resT = new JTextArea(10, 35);
		resT.setLineWrap(true);
		resT.setWrapStyleWord(true);
		final JLabel hostIP = new JLabel("Server Name: ");
		final JButton con = new JButton("Connect");
		final JButton dis = new JButton("Disconnect");
		
		con.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent event) {
				try {
					System.out.println("Trying to connect to " + host.getText() + " on port " + port.getText());
					client = new Socket(host.getText(), Integer.parseInt(port.getText()));
					System.out.println("Connected successfully to " + client.getRemoteSocketAddress());
					toServer = client.getOutputStream();
					out = new DataOutputStream(toServer);
					fromServer = client.getInputStream();
					in = new DataInputStream(fromServer);
				} catch (NumberFormatException | IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		dis.addActionListener(new ActionListener() {
			
			public void actionPerformed(final ActionEvent event) {
				try {
					System.out.println("Disconnecting from " + host.getText() + " on port " + port.getText());
					out.close();
					in.close();
					toServer.close();
					fromServer.close();
					client.close();
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		final JButton setB = new JButton("Set");
		setB.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {

					System.out.println("Sending: SET " + setT.getText());
					String output = setT.getText();
					out.writeUTF("SET "+output);
					String response = in.readUTF();
					System.out.println("Server: " + response);
					resT.setText("Server: " + response);
				} catch (IOException evt) {
					evt.printStackTrace();
				}

			}
		});
		
		final JButton getB = new JButton("Get");
		getB.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				try {

					System.out.println("Sending: GET " + getT.getText());
					String output = getT.getText();
					out.writeUTF("GET "+output);
					String response = in.readUTF();
					System.out.println("Server: " + response);
					resT.setText("Server: " + response);
				} catch (IOException evt) {
					evt.printStackTrace();
				}

			}
		});
		
		final JButton remB = new JButton("Rem");
		remB.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {

					System.out.println("Sending: REM " + remT.getText());
					String output = remT.getText();
					out.writeUTF("REM "+output);
					String response = in.readUTF();
					System.out.println("Server: " + response);
					resT.setText("Server: " + response);
				} catch (IOException evt) {
					evt.printStackTrace();
				}

			}
		});
		
		add(hostIP);
		add(host);
		add(con);
		add(dis);
		add(portL);
		add(port);
		add(setT);
		add(setB);
		add(getT);
		add(getB);
		add(remT);
		add(remB);
		add(resT);
	}
}
