package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread{
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private Player p;
	private int cardsNeeded;
	private int roundWinner; // -1 if no player is chosen, any other number indicates chosen
	
	public ClientHandler(Socket clientSocket) throws IOException {
		//Connection Setup
		connection = clientSocket;
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		
		//Player
		p = new Player("");
	}
	
	public void run(){
		String message = null;
		do {
			try {
				message = input.readObject().toString();
				interpret(message);				
			}catch(ClassNotFoundException e) {
				showMessage("\nContents unknown");
			}catch(IOException e) {
				break;
			}
		}while(true);
		closeConnection();
		
	}
	
	// Method to interpret message coming from client
	private void interpret(String message) {
		if(message.startsWith("$CARD")) {
			p.cardChosen(Integer.parseInt(message.substring(6)));
			if(p.getSelected().size() == cardsNeeded) {
				sendMessage("$CHATTING");
			}
			Server.showMessage(message);
		}else if(message.startsWith("$ZCHOOSE")) {
			roundWinner = Integer.parseInt(message.substring(9));
			sendMessage("$CHATTING");
			Server.showMessage(message);
		}else if(message.startsWith("$NAME")) {
			String t = p.getName();
			p.setName(message.substring(6));
			message = "$NAME " + t + " -> " + p.getName();
			Server.showMessage(message);
		}else if(message.startsWith("$RESETALL")) {
			Server.reset();
			Server.showMessage(message);
		}else if(message.startsWith("$START")) {
			Server.startGame();
			Server.showMessage(message);
		}else {
			message = MessageSender.sendAsChat(p.getName() + " - " + message);
			Server.messageEveryone(message);
		}
	}
	
	private void showMessage(String msg) {
		Server.showMessage(msg);
	}
	
	public void setCardsNeeded(int x) {
		cardsNeeded = x;
	}

	// Allows server class to send a message to the client
	public void sendMessage(String msg){
		try {
			output.writeObject(msg);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Close connection after they type ENDERINO
	public void closeConnection() {
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public int getWinner() {
		return roundWinner;
	}

	public void resetWin() {
		roundWinner = -1;
	}
	
	public void addPoint() {
		p.addPoint();
	}
	
	public int getPoints() {
		return p.getPoints();
	}
}
