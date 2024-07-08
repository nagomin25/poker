package com.example;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final List<Card> hands = new ArrayList<>(2);
    private int chips;
    public List<Card> getHands() {
        return hands;
    }
    public void setHands(List<Card> cards) {
        this.hands.addAll(cards);
    }
    public int getChips() {
        return chips;
    }
    public void setChips(int chips) {
        this.chips = chips;
    }
    @Override
    public String toString() {
        return "Player [hands=" + hands + ", chips=" + chips + "]";
    }

    

}
