package client;
import java.awt.EventQueue;


import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import java.awt.Font;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class ClientUserInterface extends Thread{

	private JFrame frmDictionary;
	private JTextField WordInput;
	private JTextField WordDefinition;
	protected static JTextArea definition;
	protected static DataInputStream input;
	protected static DataOutputStream output;
	
	public void run() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientUserInterface window = new ClientUserInterface();
					window.frmDictionary.setVisible(true);
				} catch (Exception e) {
					definition.setText("The server has closed");
				}
			}
		});
	}
	
	/**
	 * Public method for changing the text on the text area.
	 */
	public void setTextArea(String s) {
		ClientUserInterface.definition.setText(s);
		ClientUserInterface.definition.setVisible(true);
		ClientUserInterface.definition.repaint();
	}
	

	/**
	 * Create the application.
	 */
	public ClientUserInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDictionary = new JFrame();
		frmDictionary.setTitle("Dictionary");
		frmDictionary.setResizable(false);
		frmDictionary.setBounds(100, 100, 551, 384);
		frmDictionary.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDictionary.getContentPane().setLayout(null);
		
		JLabel Title = new JLabel("Dictionary");
		Title.setFont(new Font("Segoe UI Historic", Font.BOLD, 26));
		Title.setBounds(197, 10, 162, 52);
		frmDictionary.getContentPane().add(Title);
		
		WordInput = new JTextField();
		WordInput.setFont(new Font("SimSun", Font.PLAIN, 14));
		WordInput.setBounds(120, 63, 374, 28);
		frmDictionary.getContentPane().add(WordInput);
		WordInput.setColumns(10);
		
		WordDefinition = new JTextField();
		WordDefinition.setFont(new Font("SimSun", Font.PLAIN, 14));
		WordDefinition.setBounds(120, 108, 374, 28);
		frmDictionary.getContentPane().add(WordDefinition);
		WordDefinition.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Word:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		lblNewLabel.setBounds(31, 64, 79, 20);
		frmDictionary.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Definition:");
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 16));
		lblNewLabel_1.setBounds(31, 112, 79, 15);
		frmDictionary.getContentPane().add(lblNewLabel_1);
		
		definition = new JTextArea();
		frmDictionary.getContentPane().add(definition);
		definition.setEditable(false);
		definition.setWrapStyleWord(true);
		definition.setLineWrap(true);
		definition.setFont(new Font("Arial", Font.BOLD, 14));
		definition.setBounds(118, 189, 374, 125);
//		frmDictionary.getContentPane().add(definition);
		
		JScrollPane sp = new JScrollPane(definition, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setFont(new Font("Arial", Font.BOLD, 18));
		sp.setBounds(118, 189, 374, 144);
		frmDictionary.getContentPane().add(sp);
		
		
		JButton postButton = new JButton("Add");
		postButton.setFont(new Font("SimSun", Font.BOLD, 16));
		postButton.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			/**
			 * Method: POST
			 * Purpose: Adding a new word to the dictionary.
			 * Request Data Format&Content: JSON&word+definition
			 * Response Data Format&Content: JSON&Success Message
			 */
			public void mouseClicked(MouseEvent e) {
				
				String word = WordInput.getText();
				String def = WordDefinition.getText();
				
				JSONObject req = new JSONObject();
				req.put("method", "POST");
				req.put("word", word);
				req.put("definition", def);
				try {
					Client.output.writeUTF(req.toJSONString());
				} catch (IOException e1) {
					System.out.println("Error occuring when sending request to server");
				} catch (NullPointerException e2) {
					definition.setText("Dictonary server closed");
				}
			}
		});
		
		postButton.setBounds(31, 157, 88, 23);
		frmDictionary.getContentPane().add(postButton);
		
		JButton delButton = new JButton("Delete");
		delButton.setFont(new Font("SimSun", Font.BOLD, 16));
		delButton.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			/**
			 * Method: DELETE
			 * Purpose: Deleting a word from the dictionary.
			 * Request Data Format&Content: JSON&word
			 * Response Data Format&Content: JSON&Success Message
			 */
			public void mouseClicked(MouseEvent e) {
				String word = WordInput.getText();
				JSONObject req = new JSONObject();
				req.put("method", "DELETE");
				req.put("word", word);
				try {
					Client.output.writeUTF(req.toJSONString());
				} catch (IOException e1) {
					System.out.println("Error occuring when sending request to server");
				} catch (NullPointerException e2) {
					definition.setText("Dictonary server closed");
				}
			}
		});
		delButton.setBounds(161, 157, 88, 23);
		frmDictionary.getContentPane().add(delButton);
		
		JButton getButton = new JButton("Search");
		getButton.setFont(new Font("SimSun", Font.BOLD, 16));
		getButton.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			/**
			 * Method: QUERY
			 * Purpose: Searching new word from the dictionary.
			 * Request Data Format&Content: JSON&word
			 * Response Data Format&Content: JSON&word+definition
			 */
			public void mouseClicked(MouseEvent e) {
				String word = WordInput.getText();
				JSONObject req = new JSONObject();
				req.put("method", "GET");
				req.put("word", word);
				try {
					Client.output.writeUTF(req.toJSONString());
				} catch (IOException e1) {
					System.out.println("Error occuring when sending request to server");
				} catch (NullPointerException e2) {
					definition.setText("Dictonary server closed");
				}
			}
		});
		getButton.setBounds(290, 157, 88, 23);
		frmDictionary.getContentPane().add(getButton);
		
		JButton updatedButton = new JButton("Update");
		updatedButton.setFont(new Font("SimSun", Font.BOLD, 16));
		updatedButton.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			/**
			 * Method: UPDATE
			 * Purpose: Update a word's definition in the dictionary.
			 * Request Data Format&Content: JSON&word+definition(new)
			 * Response Data Format&Content: JSON&Success Message
			 */
			public void mouseClicked(MouseEvent e) {
				String word = WordInput.getText();
				String def = WordDefinition.getText();
				JSONObject req = new JSONObject();
				req.put("method", "UPDATE");
				req.put("word", word);
				req.put("definition", def);
				try {
					Client.output.writeUTF(req.toJSONString());
				} catch (IOException e1) {
					System.out.println("Error occuring when sending request to server");
				} catch (NullPointerException e2) {
					definition.setText("Dictonary server closed");
				}
			}
		});
		updatedButton.setBounds(419, 157, 88, 23);
		frmDictionary.getContentPane().add(updatedButton);
		
		JLabel lblNewLabel_2 = new JLabel("Message:");
		lblNewLabel_2.setFont(new Font("SimSun", Font.BOLD, 18));
		lblNewLabel_2.setBounds(31, 178, 123, 52);
		frmDictionary.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Adding multiple meaning by inserting \";\" among meanings");
		lblNewLabel_3.setBounds(120, 135, 374, 28);
		frmDictionary.getContentPane().add(lblNewLabel_3);
		

		

		
	}
}
