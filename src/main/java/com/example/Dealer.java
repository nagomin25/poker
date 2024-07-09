package com.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Dealer {
    private final List<Card> communityCardList = new ArrayList<>(5);
    private int pot;
    public List<Card> getCommunityCardList() {
        return communityCardList;
    }
    public int getPot() {
        return pot;
    }
    public void setPot(int pot) {
        this.pot = pot;
    }
    @Override
    public String toString() {
        return "Dealer [communityCardList=" + communityCardList + ", pot=" + pot + "]";
    }

    public List<Card> dealCardsToPlayer(Boolean cardsDealt, LinkedList<Card> deck, Player player) {
		if (!cardsDealt && deck.size() >= 2) {
			List<Card> hands = new ArrayList<>(2);
			hands.add(deck.poll());
			hands.add(deck.poll());
			player.setHands(hands);
			System.out.println("Cards dealt: " + player.getHands());
			cardsDealt = true; // カードが配られたことを記録
			return hands;
		} else if (cardsDealt) {
			System.out.println("Cards have already been dealt.");
			return null;
		} else {
			System.out.println("Not enough cards in the deck!");
			return null;
		}
	}

    public List<Card> dealCardsToEnemy(Boolean cardsDealtToEnemy, LinkedList<Card> deck, Enemy enemy) {
		if (!cardsDealtToEnemy && deck.size() >= 2) {
			List<Card> hands = new ArrayList<>(2);
			hands.add(deck.poll());
			hands.add(deck.poll());
			enemy.setHands(hands);
			System.out.println("Cards dealt: " + enemy.getHands());
			cardsDealtToEnemy = true; // カードが配られたことを記録
			return hands;
		} else if (cardsDealtToEnemy) {
			System.out.println("Cards have already been dealt.");
			return null;
		} else {
			System.out.println("Not enough cards in the deck!");
			return null;
		}
	}
    
    
    

}
