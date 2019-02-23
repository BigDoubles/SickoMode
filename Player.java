package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Player Class, for every player, they are this object
 * */

public class Player {
	private String name;
	private int points;
	private List<String> cards; // Cards currently in hand
	private List<String> selection; // Card(s) indexed in cards that the player selected
	private List<Integer> cSec;
	
	Player(String name){
		this.name = name;
		points = 0;
		cards = new ArrayList<String>();
		selection = new ArrayList<String>();
		cSec = new ArrayList<Integer>();
	}
	
	public void setName(String n) {
		name = n;
	}
	
	// Adds the card to their hand at the end
	public void addCard(String card) {
		cards.add(card);
	}
	
	// Cards chosen one at a time, moving them to the selected List
	public void cardChosen(int x) {
		selection.add(cards.get(x-1));
		cSec.add(x-1);
	}
	
	//Resets cards chosen for the round
	public void resetChosenCards() {		
		for(String c : selection) {
			cards.add(c);
		}
		clearChosenCards();
	}
		
	//Clear selection cards for the round
	public void clearChosenCards() {
		selection.clear();
		cSec.clear();
	}
	
	public void addPoint() {
		points++;
	}
	
	/*
	 * Getters for all Player fields
	 * */
	public String getName() {
		return name;
	}

	public int getPoints() {
		return points;
	}
	
	public List<String> getCards() {
		return cards;
	}
	
	public int totalSelected() {
		return cSec.size();
	}
	
	public List<String> getSelected() {
		return selection;
	}
	
	public void removeSelected() {
		Collections.sort(cSec);
		Collections.reverse(cSec);
		for(int x : cSec) {
			cards.remove(x);
		}
	}
	
}
