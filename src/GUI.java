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
import java.net.SocketException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI extends JPanel implements ActionListener {

	public static void main(String[] args) throws IOException {
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

		program.add(new Button("Inititate Transfer"));

		JLabel progress_label = new JLabel("Progress:");
		JLabel progress_count = new JLabel("0");
		program.add(progress_label);
		program.add(progress_count);
		program.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		program.setResizable(false);
		program.pack();
		program.setVisible(true);

		FileOutputStream output = null;
		DatagramSocket socket = null;
		// Open File Stream try/catch


		try {
			output = new FileOutputStream(filenameout.getText());

		} catch (FileNotFoundException e1) {
			System.err.println("File name was not found or valid");

		}
		// Open Socket try/catch 
		try {

			socket = new DatagramSocket(Integer.parseInt(port_num_sender.getText()));

		} catch (SocketException e2) {
			System.out.println("Failed to connect:" + port_num_sender);
			output.close();

		}
		
		// File and Socket found sucessfully, continue....
		
		System.out.println("Ready to transfer");
		
		while (true){
			DatagramPacket pck = new DatagramPacket(new byte[ 496],496);
			
			// try to retrieve data

			try {
				socket.receive(pck);
				
			}
			catch(IOException e3) {
				System.err.println(e3.getMessage());
			}
			byte[]data = pck.getData();
			progress_count.setText(String (data[0]));
			
			
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
