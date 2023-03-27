package server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ServerSideConnection extends Thread {
	
	protected Socket socket = null;
	protected DataInputStream input;
	protected DataOutputStream output;
	protected JSONParser parser;
	// Using the ConcurrentHashMap helps multi-clients synchornization.
	protected ConcurrentHashMap<String, String> dictionary = Server.dictionary;
	
	@Override
	public void run() {
		try {
			while (true) {
				
				String[] processesReq = readRequest();
				String method = processesReq[0];
				String word = processesReq[1];
				String definition = processesReq[2];
				
				/*
				 * Perform actions according to different request type
				 */
				switch(method) {
				
					case "POST":
						post(word,definition);
						break;
		
					case "DELETE":
						delete(word);
						break;
						
					case "UPDATE":
						update(word, definition);
						break;
						
					case "GET":
						get(word);
						break;
						
					case "ILLEGAL":
						error();
						break;
				}
				
			} 
		} catch (NullPointerException e) {
			Server.serverID--;
			Server.sui.setClientNumber(Integer.toString(Server.serverID) + "\n");
			Server.sui.appendConsole("Currently there are " + Server.serverID + " clients" + "\n");
		}
	}
	
	/**
	 * Constructor for the multi thread server connection class
	 */
	public ServerSideConnection(Socket socket) {
		this.socket = socket;
		try {
			// The JSON Parser
			parser = new JSONParser();
			// Input stream
			input = new DataInputStream(socket.getInputStream());
			// Output Stream
		    output = new DataOutputStream(socket.getOutputStream());
		    
		} catch(SocketException e) {
			System.out.println("Illegal socket!");
		} catch (IOException e) {
			System.out.println("Error occuring when creating buffers");
		}
	}
	
	/**
	 * This method is to read each request from the clients,
	 * and process the JSON request into dictionary format.
	 */
	public String[] readRequest() {
		
		/**
		 * Read one request from clients
		 */
		JSONObject request = null;
		try {
			request = (JSONObject) parser.parse(input.readUTF());
		} catch (ParseException | IOException e) {
			Server.sui.appendConsole("One client closed connection" + "\n");
		}
		
		String method = (String) request.get("method");
		String word = (String) request.get("word");
		String definition = (String) request.get("definition");
		
		
		
		/*
		 * Get rid of all white space in the word,
		 * if user enters all white space in the word,
		 * return illegal input error.
		 */
		if (word != null && (word = word.replaceAll("\\s+","")) == "") {
			method = "ILLEGAL";
		}
		
		
		/*
		 * If user enters all white space in the definition 
		 * return illegal input error.
		 */
		if (definition != null && definition.replaceAll("\\s+","") == "") {
			method = "ILLEGAL";
		}
		if (method == "ILLEGAL") {
			Server.sui.appendConsole("Illegal request" + "\n");
		} else {
			Server.sui.appendConsole("Recieved one request: " + method + " " + word + "\n");
		}

		
		String[] processedReq = new String[]{method, word, definition};
		
		return processedReq;
	}
	
	/**
	 * Method: POST
	 * Purpose: Adding a new word to the dictionary.
	 * Request Data Format&Content: JSON&word+definition
	 * Response Data Format&Content: JSON&Success Message
	 */
	@SuppressWarnings("unchecked")
	public void post(String word, String def) {
		JSONObject res = new JSONObject();
		res.put("resType", "REMINDER");
		try {
			if (def == "") {
				error();
				return;
			}
			if (dictionary.get(word) != null) {
				res.put("message", "The word: " + word + " already exsits!");
				output.writeUTF(res.toJSONString());
				return;
			} else {
				res.put("message", "The word: " + word + " has been added into the dictionary!");
				output.writeUTF(res.toJSONString());
				dictionary.put(word, def);
				Server.updateDcitionary();
			}			
		} catch (IOException e) {
			System.out.println("Error occuring when adding word from dictionary");
		}
	}
	
	/**
	 * Method: DELETE
	 * Purpose: Deleting a word from the dictionary.
	 * Request Data Format&Content: JSON&word
	 * Response Data Format&Content: JSON&Success Message
	 */
	@SuppressWarnings("unchecked")
	public void delete(String word) {
		JSONObject res = new JSONObject();
		res.put("resType", "REMINDER");
		try {
			if (dictionary.get(word) != null) {
				dictionary.remove(word);
				Server.updateDcitionary();
				res.put("message", "The word: " + word + " has been deleted from the dictionary!");
				output.writeUTF(res.toJSONString());
				return;
			} else {
				res.put("message", "The word: " + word + " is not in the dictionary!");
				output.writeUTF(res.toJSONString());
			}
		} catch (IOException e) {
			System.out.println("Error occuring when deleting word from dictionary");
		}
	}
	
	/**
	 * Method: UPDATE
	 * Purpose: Update a word's definition in the dictionary.
	 * Request Data Format&Content: JSON&word+definition(new)
	 * Response Data Format&Content: JSON&Success Message
	 */
	@SuppressWarnings("unchecked")
	public void update(String word, String def) {
		JSONObject res = new JSONObject();
		res.put("resType", "REMINDER");
		try {
			if(dictionary.containsKey(word)) {
				if (dictionary.get(word).equals(def)) {
					res.put("message", "The word: " + word + "'s definition unchanged");
					output.writeUTF(res.toJSONString());
					return;
				} else {
					dictionary.put(word, def);
					Server.updateDcitionary();
					res.put("message", "The word: " + word + "'s definition has been updated");
					output.writeUTF(res.toJSONString());			
				}
			} else {
				res.put("message", "The word: " + word + " is not in the dictionary");
				output.writeUTF(res.toJSONString());
				return;
			}
		}  catch (IOException e) {
			System.out.println("Error occuring when updating word from dictionary");
		}
	}
	
	/**
	 * Method: QUERY
	 * Purpose: Searching new word from the dictionary.
	 * Request Data Format&Content: JSON&word
	 * Response Data Format&Content: JSON&word+definition
	 */
	@SuppressWarnings("unchecked")
	public String get(String word) {
		try {
			if(dictionary.containsKey(word)) {
				String def = dictionary.get(word);
				def = def.replaceAll(";", "\n");
				JSONObject res = new JSONObject();
				res.put("resType", "RESULT");
				res.put("message", "The meaning of " + word + " is: " + "\n" + def);
				output.writeUTF(res.toJSONString());
				
			} else {
				JSONObject res = new JSONObject();
				res.put("resType", "REMINDER");
				res.put("message", "The word: " + word + " is not in the dictionary!");
				output.writeUTF(res.toJSONString());
			}
		} catch (IOException e) {
			System.out.println("Error occuring when searching word from dictionary");
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void error() {
		try {
			JSONObject res = new JSONObject();
			res.put("resType", "ERROR");
			res.put("message", "Illegal input");
			output.writeUTF(res.toJSONString());			
		} catch (IOException e) {
			System.out.println("Error occuring when checking word");
		}
	}
	
}
