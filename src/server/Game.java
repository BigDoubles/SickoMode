package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game extends Thread{
	private File f;
	private List<String> white, black, usedW, usedB; // Cards for appropriate decks
	private List<Integer> blanks, blanksUsed; // Amount of blanks per black card
	private List<ClientHandler> shuffled; // Ordered list before shuffling
	private String currentBlack;
	private int blanksNeeded;
	Random r;
	private int mode; // 0 = waiting, 1 = playing, 2 = game over
	private int cardZar; // player position of Czar in the players list
	
	// Initializes "Game"
	public Game() {
		mode = 0;
		cardZar = -1;
		white = new ArrayList<String>();
		black = new ArrayList<String>();
		usedW = new ArrayList<String>();
		usedB = new ArrayList<String>();
		blanks = new ArrayList<Integer>();
		blanksUsed = new ArrayList<Integer>();
		r = new Random();
		
		// Goes through the file filtering the black cards from white cards
		f = new File("TestPack.txt");
		BufferedReader s;
		try {
			s = new BufferedReader(new FileReader(f));
			String line = s.readLine();
			while(line != null) {
				if(line.charAt(0) == 'B') {
					blanks.add(Integer.parseInt(line.substring(1, 2)));
					black.add(line.substring(4));
				}else if(line.charAt(0) == 'W') {
					white.add(line.substring(3));
				}
			line = s.readLine();
			}
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(white.size() + " " + black.size());
	}
	
	//GAME LOOP
	public void run() {
		mode = 1;
		boolean hasWon = false;
		List<ClientHandler> players;
		while(!hasWon){
			players = Server.getPlayers();
			int totalPlayers = players.size();
			
			// Deal Cards to 10 per round
			for(ClientHandler c : players) {
				while(c.getPlayer().getCards().size() < 10) c.getPlayer().addCard(getWhite());
			}
			
			//Pick a black card for the round
			getBlack();
			
			//Change Card Czar for the round
			cardZar = (cardZar+1) % players.size();
			ClientHandler cZ = players.get(cardZar);
			
			// Sends Black card + white card(s) to player & number of cards needed
			Server.messageEveryone(MessageSender.sendAsChat("\n" + blanksNeeded + " cards. " + currentBlack));
			for(ClientHandler c : players) {
				if(!c.equals(cZ)) {
					c.sendMessage("$GAMEPLAY");
					c.setCardsNeeded(blanksNeeded);
					for(int x = 0; x < 10; x++) {
						c.sendMessage("$UCARD " + (x+1) + ": " + c.getPlayer().getCards().get(x));
					}
					c.sendMessage(MessageSender.sendAsChat(""));
				}
			}
			cZ.sendMessage(MessageSender.sendAsChat("You are the Card Czar.\n"));
			
			// Wait for players to choose cards
			boolean allPlayed = false;
			while(!allPlayed) {
				pause(); // Pauses for a specified time
				
				allPlayed = true;
				for(ClientHandler c : players) {
					if(!c.equals(cZ)) {
						if(allPlayed && c.getPlayer().totalSelected() != blanksNeeded) allPlayed = false;
					}
				}
			}
			
			shuffled = new ArrayList<ClientHandler>(players);
			// Send all combo's of cards to every player
			Collections.shuffle(shuffled);
			
			for(int x = 0; x < totalPlayers; x++) {
				String cs = "Player " + (x+1) + "\n";
				if(!shuffled.get(x).equals(cZ)) {
					shuffled.get(x).sendMessage(MessageSender.sendAsChat("You are Player " + (x+1)));
					for(String c : shuffled.get(x).getPlayer().getSelected()) {
						cs = cs + c + "\n";
					}
					Server.messageEveryone(MessageSender.sendAsChat(cs));
				}
				
			}
			
			for(ClientHandler c : players) {
				c.getPlayer().removeSelected();
			}
			
			// Card Czar picking
			cZ.sendMessage("$CZAR " + shuffled.size());
			cZ.setCardsNeeded(shuffled.size());
			cZ.resetWin();
			// Wait for winner to be chosen
			while(cZ.getWinner() == -1) {
				pause();
			}
			cZ.sendMessage("$NCZAR");
						
			Server.messageEveryone(MessageSender.sendAsChat(cZ.getWinner() + " WINS the round\n\n\n"));
			shuffled.get(cZ.getWinner()-1).addPoint();
		}
	}
	
	private void pause() {
		try {
			sleep(500); // Only checks every x milliseconds as to whether players have played
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Game Functions critical to gameplay
	 * */
	
	public void setMode(int z) {
		mode = z;
	}
	
	//Gets a black or white card
	//TODO Implement replacement system to shuffle cards back into "deck"
	private void getBlack() {
		int choice = r.nextInt(black.size());
		currentBlack = black.get(choice);
		blanksNeeded = blanks.get(choice);
		usedB.add(black.get(choice));
		black.remove(choice);
		blanksUsed.add(choice);
		blanks.remove(choice);
	}
	
	private String getWhite() {
		int choice = r.nextInt(white.size());
		String card = white.get(choice);
		white.remove(choice);
		usedW.add(card);
		return card;
	}
	
	/*
	 * Getters for various aspects of the game
	 * */
	public int blackCount() {
		return black.size();
	}
	
	public int whiteCount() {
		return white.size();
	}
	
	public int getMode() {
		return mode;
	}
}
