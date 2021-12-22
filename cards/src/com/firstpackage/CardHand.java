package com.firstpackage;
//TP1 IFT2015
//Patcharin Cheng 20182620

/**
 * CardHand.java calls CreateCard.java.
 * In order to compile the code, please ensure that CreateCard.java is present before run Main() in CardHand.java
 */

import java.util.*;
import java.util.Iterator;

public class CardHand {
    public final static int ACE = 1;
    public final static int JACK = 11;
    public final static int QUEEN = 12;
    public final static int KING = 13;
    public final static int SPADES = 3;
    public final static int HEARTS = 2;
    public final static int DIAMONDS = 1;
    public final static int CLUBS = 0;

    private ArrayList<CreateCard> hand;   // The cards in the hand.

    /**
     * Create a hand that is initially empty.
     */
    public CardHand() {
        hand = new ArrayList<CreateCard>();
    }

    /**
     * Add a card to the end of the hand, and then sort the added card in the hand by suits so that the card of the same suits are grouped together,
     *  and within the same suits, the cards are sorted by ranks.
     * @param c the non-null card to be added.
     * @throws NullPointerException if c is null.
     */
    public void addCard(int rank, int suit) {
        System.out.println("\n...Adding a card...");
        // call CreateCard Class. This class create a new card with rank r, and suit s to the hand.
        CreateCard c = new CreateCard(rank, suit);
        if (c == null)
            throw new NullPointerException("Can't add a null card to a hand.");
        //add a new card to the hand
        hand.add(c);

        //sort the added card so that the card of the same suits are grouped together.
        ArrayList<CreateCard> newHand = new ArrayList<CreateCard>();
        while (hand.size() > 0) {
            int pos = 0;  // Position of minimal card.
            CreateCard c2 = hand.get(0);
            for (int i = 1; i < hand.size(); i++) {
                CreateCard c1 = hand.get(i);
                if ( c1.getSuit() < c2.getSuit() ||
                        (c1.getSuit() == c2.getSuit() && c1.getRank() < c2.getRank()) ) {
                    pos = i;
                    c2 = c1;
                }
            }
            hand.remove(pos);
            newHand.add(c2);
        }
        hand = newHand;
    }
    /**
     * return an iterator for all cards currently in the hand
     * **/
    public ArrayList<CreateCard> Iterator() {
        ArrayList<CreateCard> newHand = new ArrayList<CreateCard>();

        Iterator itr = hand.iterator();
        while (itr.hasNext()) {
            //  Moving cursor to next element
            CreateCard i = (CreateCard) itr.next();
            newHand.add(i);

        }
        System.out.println("All cards in hand are:" + newHand);
        return newHand;
    }

    /**return an iterator for all cards of suit s that are currently in the hand.**/
    public ArrayList<CreateCard> suitIterator(int suit) {
        ArrayList<CreateCard> newHand = new ArrayList<CreateCard>();

        Iterator itr = hand.iterator();
        while (itr.hasNext()) {
            //  Moving cursor to next element
            CreateCard i = (CreateCard) itr.next();
            // pick only the same suit
            if ( i.getSuit() == suit){
                newHand.add(i);
            }
        }
        System.out.println("All " + getSuitAsString(suit) + " in hand are:" + newHand);
        return newHand;
    }


    /**remove and return a card of suit s from the player’s hand; if there is no card of suit s,
     * then remove and return the first card from the hand. **/
    public CreateCard Play(int suit) {
        System.out.println("\n...Play a card...");
        int pos = 0;  // Position of minimal card.
        for (int i = 1; i < hand.size(); i++) {
            CreateCard c1 = hand.get(i);
            if ( c1.getSuit() == suit) {
                pos = i;
                return hand.remove(pos);
            }
        }
        return hand.remove(0); // If the suit does not exist, remove the first position
    }

    /**
     * toString method for printing hand
     */
    public String toString() {
        String t = "";

        for (int i = 0; i < hand.size(); i++)
            t = t + hand.get(i) + "\n";
        return t;

    }

    /**
     * Cast Integer to String for representing suits
     */
    public String getSuitAsString(int suit) {
        switch ( suit ) {
            case SPADES:   return "Spades";
            case HEARTS:   return "Hearts";
            case DIAMONDS: return "Diamonds";
            default:       return "Clubs";
        }
    }

    public static void main(String[] args) {
        CardHand hand = new CardHand(); //initialize card hand

        // Simulate "addCard"
        hand.addCard(ACE, SPADES ); //CreateCard() is located in addCard()
        hand.addCard(10,DIAMONDS);
        hand.addCard(10, SPADES );
        hand.addCard(8, DIAMONDS );
        hand.addCard(2, SPADES );
        hand.addCard(JACK, HEARTS);
        hand.addCard(7, HEARTS );
        hand.addCard(QUEEN, DIAMONDS);
        hand.addCard(KING, CLUBS);
        hand.addCard(ACE, DIAMONDS );

        // Simulate "Play"
        CreateCard play1 = hand.Play(CLUBS); //remove and return a card of suit s from the player’s hand
        System.out.println(play1 + " is removed.");
        hand.Iterator();
        CreateCard play2 = hand.Play(CLUBS); //remove and return a card of suit s from the player’s hand
        System.out.println(play2 + " is removed.");

        //Simulate "Iterator"
        hand.Iterator(); //return an iterator for all cards currently in the hand

        //Simulate "suitIterator"
        hand.suitIterator(DIAMONDS);
        hand.suitIterator(HEARTS);
        hand.suitIterator(CLUBS);
        hand.suitIterator(SPADES);
    }
}