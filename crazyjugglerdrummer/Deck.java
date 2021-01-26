package crazyjugglerdrummer;

import java.util.Random;import java.util.ArrayList;
public class Deck {
    private ArrayList<Card> cards;

    public Deck(int[] nrs, int[] suits) {
        cards = new ArrayList<>();
        for (int i=0; i<nrs.length; i++) {
            cards.add(new Card((short)suits[i], (short)nrs[i]));
        }
    }

    Deck()
    {
        cards = new ArrayList<Card>();
        int index_1, index_2;
        Random generator = new Random();
        Card temp;

        for (short a=0; a<=3; a++)
        {
            for (short b=0; b<=12; b++)
            {
                cards.add( new Card(a,b) );
            }
        }

        int size = cards.size() -1;

        for (short i=0; i<100; i++)
        {
            index_1 = generator.nextInt( size );
            index_2 = generator.nextInt( size );

            temp = (Card) cards.get( index_2 );
            cards.set( index_2 , cards.get( index_1 ) );
            cards.set( index_1, temp );
        }
    }

    public Card drawFromDeck()
    {
        return cards.remove( cards.size()-1 );
    }

    public int getTotalCards()
    {
        return cards.size();
        //we could use this method when making
        //a complete poker game to see if we needed a new deck
    }
}