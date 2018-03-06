import java.awt.Button;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class reciever extends JPanel {

	private static void acksend(DatagramPacket pck, DatagramSocket socket, JTextField port_num_reciever,
			JTextField port_num_sender, byte b) throws IOException {
		// TODO Auto-generated method stub

		InetAddress address;

		try {
			address = InetAddress.getByName(port_num_sender.getText());
		} catch (UnknownHostException e5) {
			System.err.println(e5.getMessage());
			return;
		}

		byte[] buffer = new byte[1];
		buffer[0] = b;

		DatagramPacket ackPacket = new DatagramPacket(buffer, buffer.length, address,
				Integer.parseInt(port_num_sender.getText()));
		socket.send(ackPacket);

	}

	static void displayreciever() {

		JFrame program = new JFrame("Sender");
		program.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		program.setPreferredSize(new Dimension(500, 500));

		program.setLayout(new GridLayout(4, 5));

		JTextField ip_num = new JTextField("IP Address");
		JTextField port_num_sender = new JTextField("Port # (Sender)");
		JTextField port_num_reciever = new JTextField("Port # (Reciever)");
		JTextField filenameout = new JTextField("Output File Path");

		program.add(ip_num);
		program.add(port_num_sender);
		program.add(port_num_reciever);
		program.add(filenameout);
		JButton transfer = new JButton("Inititate Transfer");
		program.add(transfer);

		JLabel progress_count = new JLabel("0");
		program.add(progress_count);
		program.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		program.setResizable(false);
		program.pack();
		program.setVisible(true);

		transfer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				FileOutputStream output = null;
				DatagramSocket socket = null;
				// Open File Stream try/catch

				// get input from gui

				try {
					output = new FileOutputStream(filenameout.getText());

				} catch (FileNotFoundException e1) {
					System.err.println("File name was not found or valid");

				}
				// Open Socket try/catch
				try {

					socket = new DatagramSocket(Integer.parseInt(port_num_sender.getText()));

				} catch (SocketException e2) {
					System.out.println("Failed to connect:" + port_num_sender.getText());
					try {
						output.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

				// File and Socket found sucessfully, continue....

				System.out.println("Ready to transfer");
				int nextack = 0;
				while (true) {
					DatagramPacket pck = new DatagramPacket(new byte[496], 496);

					// try to retrieve data

					try {
						socket.receive(pck);

					} catch (IOException e3) {
						System.err.println(e3.getMessage());
					}
					byte[] data = pck.getData();
					progress_count.setText(String.valueOf(data[0]));

					if (data[0] == -1) {
						try {
							acksend(pck, socket, port_num_reciever, port_num_sender, (byte) -1);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// close fileoutput stream;

						try {
							output.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;

					}
					if (data[0] != nextack) {
						try {
							acksend(pck, socket, port_num_reciever, port_num_sender, (byte) (nextack - 1));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}

					// otherwise the current ack = exp ack, write data into output file

					for (int i = 3; i < 3 + data[2]; i++) {
						try {
							output.write(data[i]);
						} catch (IOException e4) {
							System.err.println(e4.getMessage());
						}

						nextack = (byte) ((nextack) + 1) % 124;

						try {
							acksend(pck, socket, port_num_reciever, port_num_sender, data[0]);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}

			}
		});
	}

	public static void main(String[] args) throws IOException {
		// schedule this for the event dispatch thread (edt)
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				displayreciever();
			}
		});
	}

}
