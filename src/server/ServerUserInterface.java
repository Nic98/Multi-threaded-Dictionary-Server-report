package server;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import java.awt.Font;
import javax.swing.JTextArea;

public class ServerUserInterface extends Thread{

	private JFrame frame;
	protected static JLabel number;
	protected static JTextArea Console;
	/**
	 * Launch the application.
	 */
	public void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerUserInterface window = new ServerUserInterface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	public void setClientNumber(String n) {
		ServerUserInterface.number.setText(n);
	}
	
	public void appendConsole(String s) {
		ServerUserInterface.Console.append(s);
		ServerUserInterface.Console.setVisible(true);
		ServerUserInterface.Console.repaint();
	}
	
	/**
	 * Create the application.
	 */
	public ServerUserInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 494, 336);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server UI");
		lblNewLabel.setFont(new Font("Arial Black", Font.BOLD, 28));
		lblNewLabel.setBounds(133, 10, 238, 49);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Log:");
		lblNewLabel_1.setFont(new Font("SimSun", Font.BOLD, 16));
		lblNewLabel_1.setBounds(21, 122, 98, 36);
		frame.getContentPane().add(lblNewLabel_1);
		
		Console = new JTextArea();
		Console.setEditable(false);
		Console.setBounds(101, 129, 303, 111);
		Console.setFont(new Font("Arial", Font.BOLD, 14));
		Console.setWrapStyleWord(true);
		Console.setLineWrap(true);
		frame.getContentPane().add(Console);
		

		
		JScrollPane sp = new JScrollPane(Console, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setFont(new Font("Arial", Font.BOLD, 18));
		sp.setBounds(100, 125, 340, 165);
		frame.getContentPane().add(sp);
		
		
		
		
		JLabel lblNewLabel_2 = new JLabel("Number of Clients:");
		lblNewLabel_2.setFont(new Font("SimSun", Font.BOLD, 16));
		lblNewLabel_2.setBounds(21, 66, 171, 27);
		frame.getContentPane().add(lblNewLabel_2);
		
		number = new JLabel("0");
		number.setFont(new Font("SimSun", Font.BOLD, 16));
		number.setBounds(179, 67, 32, 24);
		frame.getContentPane().add(number);
	}
}
