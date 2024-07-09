package com.example;

import java.util.ArrayList;
import java.util.List;

public class Enemy {

    private final List<Card> hands = new ArrayList<>(2);
    private int chips = 20000;
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
        return "Enemy [hands=" + hands + ", chips=" + chips + "]";
    }
}