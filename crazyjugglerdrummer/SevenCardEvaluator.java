package crazyjugglerdrummer;

public class SevenCardEvaluator {

    /**
     * @param nrs1 array of length 7 of rankings 1..13 (inclusive) 1=Ace, 2=Two ... 13=King
     * @param suits1 array of length 7 of suits 0..3 (inclusive)
     * @param nrs2 array of length 7 of rankings 1..13 (inclusive) 1=Ace, 2=Two ... 13=King
     * @param suits2 array of length 7 of suits 0..3 (inclusive)
     * @return 1 if player 1 wins; -1 if player 2 wins; 0 on a draw
     */
    public int compare(int[] nrs1, int[] suits1, int[] nrs2, int[] suits2) {
        PocketAndStreet player1 = best5outOf7(nrs1, suits1);
        PocketAndStreet player2 = best5outOf7(nrs2, suits2);

        return compare5(player1.nrs, player1.suits, player2.nrs, player2.suits);
    }

    private PocketAndStreet best5outOf7(int[] nrs, int[] suits) {
        int[] bestNrs = new int[5];
        int[] bestSuits = new int[5];
        boolean init = true;
        for (int remove1=0; remove1<7; remove1++) {
            for (int remove2=remove1+1; remove2<7; remove2++) {
                int index = 0;
                int[] nrs5 = new int[5];
                int[] suits5 = new int[5];
                for (int i=0; i<7; i++) {
                    if (i==remove1 || i==remove2) {
                        continue;
                    }
                    nrs5[index] = nrs[i];
                    suits5[index] = suits[i];
                    index++;
                }
                if (init || compare5(nrs5, suits5, bestNrs, bestSuits) > 0) {
                    bestNrs = nrs5;
                    bestSuits = suits5;
                }
                init = false;
            }
        }
        return new PocketAndStreet(bestNrs, bestSuits);
    }

    /**
     * @param nrs1 array of length 5 of rankings 1..13 (inclusive) 1=Ace, 2=Two ... 13=King
     * @param suits1 array of length 5 of suits 0..3 (inclusive)
     * @param nrs2 array of length 5 of rankings 1..13 (inclusive) 1=Ace, 2=Two ... 13=King
     * @param suits2 array of length 5 of suits 0..3 (inclusive)
     * @return 1 if player 1 wins; -1 if player 2 wins; 0 on a draw
     */
    public int compare5(int[] nrs1, int[] suits1, int[] nrs2, int[] suits2) {
        Deck deck1 = new Deck(nrs1, suits1);
        Hand hand1 = new Hand(deck1);
        Deck deck2 = new Deck(nrs2, suits2);
        Hand hand2 = new Hand(deck2);
        return hand1.compareTo(hand2);
    }

    private static class PocketAndStreet {
        PocketAndStreet(int[] nrs, int[] suits) {
            this.nrs = nrs;
            this.suits = suits;
        }
        int[] nrs;
        int[] suits;
    }
}
