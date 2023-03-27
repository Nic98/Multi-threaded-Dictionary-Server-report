package client;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Client {

	protected static ClientUserInterface cui;
	protected static DataInputStream input;
	protected static DataOutputStream output;
	protected static JSONParser parser;
	
	public static void main(String[] args) 
	{
		// Initialise the client UI
		cui = new ClientUserInterface();
		cui.start();
		Socket socket = null;
		try 
		{
			/*
			 * Checking the user command input.
			 * The first argument is the host name.
			 * The seconde arguement is the port number.
			 */
			if (args.length != 2) {
				System.err.println("Clent <host name> <port number>");
				System.exit(1);
			}
			String hostName = args[0];
			int portNumber = Integer.parseInt(args[1]);

			// Create a stream socket bounded to any port and connect it to the
			socket = new Socket(hostName, portNumber);
			cui.setTextArea("ClientSide socket ready");
			
			// Get the input/output streams for reading/writing data from/to the socket
			input = new DataInputStream(socket.getInputStream());
		    output = new DataOutputStream(socket.getOutputStream());
		    // Json parser for parsing the recieved response
			parser = new JSONParser();
			
			/*
			 * Keep receiving from the dictionary server
			 * Change the client UI according to the response from the server
			 */
			while(true) {
				JSONObject response = (JSONObject) parser.parse(input.readUTF());
				String resType = (String) response.get("resType");
				String resMsg = (String) response.get("message");
				switch(resType) {
					case "REMINDER":
						resMsg = "REMINDER: " + resMsg;
						break;
					case "ERROR":
						resMsg = "ERROR: " + resMsg;
						break;
					case "RESULT":
						break;
				}
				
				cui.setTextArea(resMsg);
			}			

		} 
		catch (UnknownHostException e)
		{
			cui.setTextArea("Server not found");
			System.out.println("Server not found");
		} catch (IOException | NullPointerException e)
		{
			cui.setTextArea("Dictonary server closed");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occuring when parsing the message");
		}
	}
}
