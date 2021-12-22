package com.firstpackage;
//TP1 IFT2015
//Patcharin Cheng 20182620
/**
 * A CreateCard object represents a regular playing card. The playing card has 4 suits, each suit has 13 ranks. A spade, heart,
 * diamond, or club has one of the 13 values: ace, 2, 3, ...,10, jack, queen, or king, where ace is the smallest value, and king is the largest.
 */
public class CreateCard {
    public final static int ACE = 1;
    public final static int JACK = 11;
    public final static int QUEEN = 12;
    public final static int KING = 13;
    public final static int SPADES = 3;
    public final static int HEARTS = 2;
    public final static int DIAMONDS = 1;
    public final static int CLUBS = 0;

    private final int suit;
    private final int rank;

    /**
     * Creates a card with a specified suit and rank.
     * @param rankR the value of the new card.
     * the value must be between 1 to 13, with 1 representing an Ace.
     * @param suitS the suit of the new card must be one of the SPADES, HEARTS,DIAMONDS,CLUBS
     * @throws IllegalArgumentException if the parameter values are not in the
     * permissible ranges
     */
    public CreateCard(int rankR, int suitS) {
        if (suitS != SPADES && suitS != HEARTS && suitS != DIAMONDS &&
                suitS != CLUBS)
            throw new IllegalArgumentException("Illegal suit.");
        if (rankR < 1 || rankR > 13)
            throw new IllegalArgumentException("Illegal rank, Rank is not in the expected range.");
        rank = rankR;
        suit = suitS;
    }

    /**
     * @return one of the strings "Spades", "Hearts", "Diamonds", "Clubs"
     */
    public String getSuitAsString() {
        switch ( suit ) {
            case SPADES:   return "Spades";
            case HEARTS:   return "Hearts";
            case DIAMONDS: return "Diamonds";
            default:    return "Clubs";
        }
    }

    /**
     * Returns a String representation of the card's value.
     * @return one of the strings "Ace", "2",..., "10", "Jack", "Queen", or "King".
     */
    public String getRankAsString() {
            switch (rank) {
                case 1:   return "Ace";
                case 2:   return "2";
                case 3:   return "3";
                case 4:   return "4";
                case 5:   return "5";
                case 6:   return "6";
                case 7:   return "7";
                case 8:   return "8";
                case 9:   return "9";
                case 10:  return "10";
                case 11:  return "Jack";
                case 12:  return "Queen";
                default:  return "King";

        }
    }

    /**
    return cards as: "Queen-Hearts", "10-Diamonds", "Ace-Spades",
     */
    public String toString() {
        return getRankAsString() + "-" + getSuitAsString();
    }

    public int getSuit() {
        return suit;
    }
    public int getRank() {
        return rank;
    }
}