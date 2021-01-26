package crazyjugglerdrummer;

public class Main {

    public static void main(String[] args) {
        SevenCardEvaluator evaluator = new SevenCardEvaluator();

        int[] nrs1 = new int[] {3,3,4,4,7,5,5};
        int[] suits1 = new int[] {0,1,0,1,0,0,1};
        int[] nrs2 = new int[] {2,1,4,4,7,5,5};
        int[] suits2 = new int[] {0,1,0,1,0,0,1};

        int result = evaluator.compare(nrs1, suits1, nrs2, suits2);

        // Both players have two pair 5s and 4s.
        // Prints -1 because player 2 has an Ace kicker and player 1 has only a 7 kicker.
        System.out.println(result);
    }
}
