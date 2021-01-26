This package "crazyjugglerdrummer" is not used in the main program (not even imported) and could be removed. It is used by the author locally to test and validate the main program.

Source: https://www.codeproject.com/Articles/38821/Make-a-poker-hand-evalutator-in-Java + bugfixes added here.

Example Usage of this package:

    SevenCardEvaluator evaluator = new SevenCardEvaluator();

    int[] nrs1 = new int[] {3,3,4,4,7,5,5};
    int[] suits1 = new int[] {0,1,0,1,0,0,1};
    int[] nrs2 = new int[] {2,1,4,4,7,5,5};
    int[] suits2 = new int[] {0,1,0,1,0,0,1};

    int result = evaluator.compare(nrs1, suits1, nrs2, suits2);

    // Both players have two pair 5s and 4s.
    // Prints -1 because player 2 has an Ace kicker and player 1 has only a 7 kicker.
    System.out.println(result);
    
- Q: Why is this evaluator not used instead of the main program?
- A: The main program is (much, much) optimized for speed. The code in the current package is slower but also much
     better readable.
- Q: Can this package be used standalone without the main program?
- A: Yes.    