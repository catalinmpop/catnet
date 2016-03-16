import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.*;

public class Server {
	public static void main(String argv[]) throws Exception {
		System.out.println("Server Started \n");
		// The dictionary is a 2D ArrayList of strings
		final ArrayList<ArrayList<String>> dictionary = new ArrayList<ArrayList<String>>();
		// Get the port number from the command line.
		int port = Integer.parseInt(argv[0]);
		ServerSocket socket = new ServerSocket(port);
		Semaphore gate = new Semaphore(1);

		// Listen for connections
		while (true) {
			Socket connection = socket.accept();
			System.out.println("Connection Accepted \n");
			// Create new client connection
			new ServerThread(connection, dictionary, gate).start();
		}
	}
}

// Client thread
class ServerThread extends Thread {
	private Socket socket;
	final ArrayList<ArrayList<String>> dictionary;
	Semaphore gate;

	ServerThread(Socket socket, ArrayList<ArrayList<String>> dictionary, Semaphore gate) {
		this.socket = socket;
		this.dictionary = dictionary;
		this.gate = gate;
	}

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {

		OutputStream outToClient = socket.getOutputStream();
		DataOutputStream out = new DataOutputStream(outToClient);
		InputStream inFromClient = socket.getInputStream();
		DataInputStream in = new DataInputStream(inFromClient);
		while (true) {
			// Get the request line of the message.
			String input = in.readUTF().trim();
			System.out.println("Client: " + input);
			String sendBack = process(input);
			out.writeUTF(sendBack);
			System.out.println("Sent: " + sendBack);

		}
	}

	// this is where we receive messages from clients, we send the appropriate
	// response after processing it.
	private String process(String input) {
		
		String message;
		int spaces = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == ' ') {
				spaces++;
			}
		}if (input.startsWith("SET ")) {
			
			// System.out.println("Spaces: " + spaces);
			if (spaces == 1 && input.length()>=5)
				message = "Error: that's only one word";
			else if (spaces == 2 && input.length()>=6)
				message = addToDictionary(input.substring(4));
			else if (spaces > 2)
				message = "Error: Too many words";
			else
				message = "Error: You have to enter a word";
		} else if (input.startsWith("GET ")) {
			if (input.length()>=5 && spaces == 1)
				message = getWord(input.substring(4));
			else
				message = "Error: Please type in one word";
		} else if (input.startsWith("REM ")){
			if (input.length()>=5 && spaces == 1)
				message = removeWord(input.substring(4));
			else
				message = "Error: Please type in one word";
		} else 
			message = "Error: Command not recognized";
		return message;
	}

	private String addToDictionary(String input) {
		String out = "";
		// Flags for the first and second word
		Boolean f1=false;
		Boolean f2=false;
		// Location for the first and second word
		int loc1=0;
		int loc2=0;
		StringTokenizer tokens = new StringTokenizer(input);
		String word1=tokens.nextToken();
		String word2=tokens.nextToken();
		
		// If there are no words in the dictionary they are automatically added
		if (dictionary.size()==0){
			ArrayList<String> group = new ArrayList<String>();
			group.add(word1);
			group.add(word2);
			dictionary.add(group);
			out = "Success: "+word1+", "+word2+" have been added";
		}else{
			for (int i=0; i<dictionary.size();i++){
				ArrayList<String> current=dictionary.get(i);
				// Store the location of the word if found
				if (current.contains(word1)==true){
					f1=true; loc1=i;
					}
				if (current.contains(word2)==true){
					f2=true; loc2=i;
				}
			}
			if (f1==true && f2==true){
				// If they are in the same column they are already synonyms
				if (loc1==loc2){
					out="Error: "+word1+" and "+word2+" are already synonyms";
				}else{
					ArrayList<String> group1=dictionary.get(loc1);
					ArrayList<String> group2=dictionary.get(loc2);
					group1.addAll(group2);
					dictionary.remove(group2);
					out="Success: "+word1+" and "+word2+" synonym groups have been combined";
				}
			}else if(f1==false && f2==false){
				ArrayList<String> group = new ArrayList<String>();
				group.add(word1);
				group.add(word2);
				dictionary.add(group);
				out = "Success: "+word1+", "+word2+" have been added";
			}else if(f1==true || f2==true){
				if (f1==true){
					ArrayList<String> group1=dictionary.get(loc1);
					group1.add(word2);
					out = "Success: "+word2+" has been added to "+word1+"'s synonym group";
				}else if (f2==true){
					ArrayList<String> group2=dictionary.get(loc2);
					group2.add(word1);
					out = "Success: "+word1+" has been added to "+word2+"'s synonym group";
				}
			}else{
				out = "Error: Something went wrong somewhere it shouldn't have";
			}
		}
		return out;
	}

	private String getWord(String word) {
		String out="";
		Boolean f1=false;
		int loc=0;
		
		for (int i=0; i<dictionary.size();i++){
			ArrayList<String> current=dictionary.get(i);
			if (current.contains(word)==true){
				f1=true; loc=i;
				}
		}
		if (f1==false)
			out="Error: "+word+" is not in the current dictionary";
		else if (f1==true){
			ArrayList<String> current=dictionary.get(loc);
			if (current.size()==1){
				out = word+" has no synonyms";
			}else{
				out = word+" is a synonym to: ";
				for (int i=0;i<current.size();i++){
					if (current.get(i).equals(word)==false){
						if (i==current.size()-1){
							out=out+current.get(i)+".";
						}else{
							out=out+current.get(i)+", ";
						}
					}
				}
			}
		}
		return out;
	}
	
	private String removeWord(String word){
		String out="";
		Boolean f1=false;
		
		for (int i=0; i<dictionary.size();i++){
			ArrayList<String> current=dictionary.get(i);
			if (current.contains(word)==true){
				f1=true;
				current.remove(word);
				}
		}
		if (f1==false)
			out="Error: "+word+" is not in the current dictionary";
		else if (f1==true){
			out="Success: "+word+" has been removed from the dictionary";
		}else{
			out = "Error: Something went wrong somewhere it shouldn't have";
		}
		return out;
	}
}