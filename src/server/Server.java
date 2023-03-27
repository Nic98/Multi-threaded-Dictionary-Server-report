package server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

	protected static ConcurrentHashMap<String, String> dictionary = new ConcurrentHashMap<>();
	//counter to keep track of the number of clients
	protected static int serverID = 0;
	protected static String filename;
	protected static String path;
	protected static ServerUserInterface sui;
	
	public static void main(String[] args) {
		
		ServerSocket listeningSocket = null;
		Socket clientSocket = null;
		sui = new ServerUserInterface();
		sui.start();
		try {
			
			/*
			 * Checking the command input.
			 * The first argument is the port number.
			 * The seconde arguement is the name of the stored dictionary file.
			 */
			if (args.length != 2) {
				System.err.println("Server <port number> <dictionary-file>");
				System.exit(1);
			}
			int portNumber = Integer.parseInt(args[0]);
			filename = args[1];
			
			// Get the current dirctory path for creating dictionary file.
			path = System.getProperty("user.dir");
			
			sui.appendConsole("Dictonary Server started\\n");
			sui.appendConsole("Your dictionary file has been created under: " + path + "\n");
			
			// Load the dictionary into the server before establish connection with clients.
			readDictionary();
			listeningSocket = new ServerSocket(portNumber);
			
			//Listen for incoming connections for ever 
			while (true) {
				
				/*
				 * Each time recived a client connection will creat a connection object
				 * for processing request and response.
				 */
				clientSocket = listeningSocket.accept();
				ServerSideConnection cnt = new ServerSideConnection(clientSocket);
				cnt.start();
				
				/*
				 * Record the number of client currently online.
				 */
				serverID++;
				sui.setClientNumber(Integer.toString(serverID));
				sui.appendConsole("New client-connection established" + "\n");
				sui.appendConsole("Currently there are " + serverID + " clients" + "\n");
				
			}
			
		} catch (IOException e) {
			System.out.println("Dictonary Server rebooted");
		} finally {
			if(listeningSocket != null)
			{
				try
				{
					listeningSocket.close();
				}
				catch (IOException e) 
				{
					System.out.println("Unable to close server");
				}
			}
		}
	}
	
	/**
	 * Method for read the dictionary file when the server has started,
	 * read all vocabulary into the Concurrent HashMap
	 */
	public static void readDictionary() {
		
		// Creating the dictonary file
		File fd = new File(path, filename);
		InputStreamReader inputStreamReader = null;
		try {
			
			fd.createNewFile();
			inputStreamReader = new InputStreamReader(new FileInputStream(fd), "UTF-8");
			
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			System.out.println("File doesn't exist");
		} catch (IOException e1) {
			System.out.println("File name illegal");
		}
		
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String oneLine = null;

		try {
			while ((oneLine = bufferedReader.readLine()) != null) {
				String[] vocb = oneLine.split(":");
				dictionary.put(vocb[0], vocb[1]);
			}
			inputStreamReader.close();
			bufferedReader.close();
		} catch (IOException e) {
			System.out.println("Error occuring when reading file");
		}
	}
	
	/**
	 * Method for updating the dictionary file when: 
	 * new word added,
	 * word definition changed,
	 * word deleted.
	 */
	public static void updateDcitionary() {
		
		File fd = new File(filename);
		
		BufferedWriter bufferedWriter = null;
		
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(fd));
		} catch (IOException e1) {
			System.out.println("Error occuring when writing file");
		}

		for (Entry<String, String> entry : dictionary.entrySet()) {

			try {
				bufferedWriter.write(entry.getKey() + ":" + entry.getValue() + "\n");
			} catch (IOException e) {
				System.out.println("Error occuring when writing file");
			}
		}
		
		try {
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			System.out.println("Error occuring when writing file");
		}

	}	
}
