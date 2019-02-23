package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
	
	private ServerSocket server; // "Server" for new connections
	private Socket acceptingConnection; // Temporary connection socket for new connections
	private static List<ClientHandler> clients; // All clients connected to server
	
	private static Game g;
	
	private static final int PORT = 1373;
	
	public void run() {
		g = new Game();
		clients = new ArrayList<ClientHandler>();
		try {
			server = new ServerSocket(PORT);
			while(true) {
				waitForConnection();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	private void waitForConnection() throws IOException{
		acceptingConnection = server.accept();
		clients.add(new ClientHandler(acceptingConnection));
		clients.get(clients.size()-1).start();
	}
	
	// Goes through every client deleting the entire list of clients and resets Game object
	public static void reset() {
		for(ClientHandler c : clients) {
			c.sendMessage("$ENDERINO");
			c.closeConnection();
			c.interrupt();
		}
		clients.clear();
		g = new Game();
	}

	// Method to display message to the server console window
	public static void showMessage(String message) {
		Window.showMessage(message);
	}
	
	// Sends every client connected a string, originating from the server
	public void sendAll(String msg) {
		for(ClientHandler c : clients) {
			c.sendMessage(msg);
		}
	}
	
	// Method to send everyone a message from the Client
	public static void messageEveryone(String m) {
		showMessage(m);
		for(ClientHandler c : clients) {
			c.sendMessage(m);
		}
	}
	
	public static List<ClientHandler> getPlayers(){
		return clients;
	}
	
	/*
	 * Game specific methods that the "server" runs
	 * */
	// Main Game Method
	public static void startGame() {
		if(g.getMode() == 1) return; // Game already in progress
		g.start();
	}
	
}
