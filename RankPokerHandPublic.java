import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class RankPokerHandPublic {

    public enum Combination {

        ROYAL_FLUSH(11),
        STRAIGHT_FLUSH(10),
        SKIP_STRAIGHT_FLUSH_ACE_LOW_TMP(9),
        FOUR_OF_A_KIND(8),
        FULL_HOUSE(7),
        FLUSH(6),
        STRAIGHT(5),
        SKIP_STRAIGHT_ACE_LOW_TMP(4),
        THREE_OF_A_KIND(3),
        TWO_PAIR(2),
        PAIR(1),
        HIGH_CARD(0);

        private final int rank;

        private final static HashMap<Integer, Combination> fromRank = new HashMap<Integer, Combination>();

        static {
            for (Combination combination: EnumSet.allOf(Combination.class)) {
                fromRank.put(combination.rank(), combination);
            }
        }

        private Combination(int rank) {
            this.rank = rank;
        }

        public int rank() {
            return rank;
        }

        public static Combination fromRank(int rank) {
            return fromRank.get(rank);
        }
    }

    private final static int[] hands={Combination.FOUR_OF_A_KIND.rank(), Combination.STRAIGHT_FLUSH.rank(), Combination.STRAIGHT.rank(),
            Combination.FLUSH.rank(), Combination.HIGH_CARD.rank(), Combination.PAIR.rank(), Combination.TWO_PAIR.rank(),
            Combination.ROYAL_FLUSH.rank(), Combination.THREE_OF_A_KIND.rank(), Combination.FULL_HOUSE.rank()};

    public final static int A=14, K=13, Q=12, J=11;
    public final static int[] suits5 = new int[] {1,2,4,8};

    /*
     * Get an int that is bigger as the hand is bigger, breaking ties and keeping actual (real) ties.
     * Apply >>26 to the returned value afterwards, to get the rank of the Combination (0..11)
     *
     * Based and improved on:
     * http://www.codeproject.com/Articles/569271/A-Poker-hand-analyzer-in-JavaScript-using-bit-math
     *
     * nr is an array of length 5, where each number should be 2..14.
     * suit is an array of length 5, where each number should be 1,2,4 or 8.
     */
    public static int rankPokerHand5(int[] nr, int[] suit) {
        long v = 0L;
        int set = 0;
        for (int i=0; i<5; i++) {
            v += (v&(15L<<(nr[i]*4))) + (1L<<(nr[i]*4));
            set |= 1 << (nr[i]-2);
        }
        int value = (int) (v % 15L - ((hasStraight(set)) || (set == 0x403c/4) ? 3L : 1L)); // keep the v value at this point
        value -= (suit[0] == (suit[1]|suit[2]|suit[3]|suit[4]) ? 1 : 0) * ((set == 0x7c00/4) ? -5 : 1);
        value = hands[value];

        // break ties
        value = value << 26;
        value |= value == Combination.FULL_HOUSE.rank()<<26 ? 64-Long.numberOfLeadingZeros(v & (v<<1) & (v<<2)) << 20
                : set == 0x403c/4 ? 0
                : (64-Long.numberOfLeadingZeros(v & (v<<1)) << 20) | (Long.numberOfTrailingZeros(v & (v<<1)) << 14);
        value |= set;

        return value;
    }

    public static void initRankPokerHand7() {
        for (int i=0; i<32768; i++) {
            straightFlush[i] = getStraight(i);
            if ((i&(0x403c/4))==0x403c/4) {
                straightFlush[i] |= 1<<3;
            }
            flush[i] = Integer.bitCount(i) >= 5 ? getUpperFive(i) : 0;
        }
        int max=0;
        int count = 0;
        int[] nrs = {1,2,4,8,1};
        for (int nr1=2; nr1<=14; nr1++) {
            for (int nr2=nr1; nr2<=14; nr2++) {
                for (int nr3=nr2; nr3<=14; nr3++) {
                    for (int nr4=nr3; nr4<=14; nr4++) {
                        for (int nr5=nr4; nr5<=14; nr5++) {
                            if (nr1==nr5) {
                                continue;
                            }
                            for (int nr6=nr5; nr6<=14; nr6++) {
                                if (nr2==nr6) {
                                    continue;
                                }
                                for (int nr7=nr6; nr7<=14; nr7++) {
                                    if (nr3==nr7) {
                                        continue;
                                    }
                                    int[][] hands = get5HandsFrom7(new int[]{nr1,nr2,nr3,nr4,nr5,nr6,nr7});
                                    int maxValue = 0;
                                    int straight = 0;
                                    for (int[] hand: hands) {
                                        int value = rankPokerHand5(hand, nrs);
                                        int set = value & ((1<<14)-1);
                                        straight |= (hasStraight(set) || set == 0x403c/4) ? set : 0;
                                        maxValue = value>maxValue ? value : maxValue;
                                    }
                                    int sum = recurrence[nr1-2] + recurrence[nr2-2] + recurrence[nr3-2] + recurrence[nr4-2] + recurrence[nr5-2] + recurrence[nr6-2] + recurrence[nr7-2];
                                    count++;
                                    lookup[sum%4731770] = maxValue;
                                    if (sum%4731770>max) {
                                        max = sum%4731770; //7561824
                                    }
                                    RankPokerHandPublic.straight[sum%4731770] = straight;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("count (lower bound):" + count);
        System.out.println("MAX:" + max);
    }

    private static int totalLength=0;

    final private static int[] COUNTS = new int[] {1, 13, 78, 286, 715, 1287, 1716, 1716};

    private static int getUpperFive(int cards) {
        cards = Integer.bitCount(cards) > 5 ? cards & ~((1<<Integer.numberOfTrailingZeros(cards)+1)-1) : cards;
        cards = Integer.bitCount(cards) > 5 ? cards & ~((1<<Integer.numberOfTrailingZeros(cards)+1)-1) : cards;
        return cards;
    }

    private static final int[] recurrence = {
            0, 1, 5, 24, 106, 472, 2058, 8768, 29048, 70347, 233028, 583164, 1472911
    };

    private static int[] lookup = new int[4731770];
    private final static int[] straight = new int[4731770];
    private final static int[] flush = new int[32768];
    private final static int[] straightFlush = new int[32768];

    private static int[] cards = new int[4];

    // suit must be 0..3 here!!
    public static int rankPokerHand7(int[] nr, int[] suit) {
        int index=0;
        for (int i=0; i<4; i++) {
            cards[i] = 0;
        }
        for (int i=0; i<7; i++) {
            cards[suit[i]] |= 1L << nr[i];
            index += recurrence[nr[i]];
        }
        index = index % 4731770;
        int value = lookup[index];
        int fl = 0;
        for (int i=0; i<4; i++) {
            fl |= flush[cards[i]];
        }
        int str = straight[index];

        int straightFl = fl==0 ? 0 :
                (straightFlush[str&cards[0]] | straightFlush[str&cards[1]] | straightFlush[str&cards[2]] | straightFlush[str&cards[3]]);

        straightFl = Integer.highestOneBit(straightFl);

        return straightFl == 1 << 12 ? (Combination.ROYAL_FLUSH.rank() << 26)
                : straightFl != 0 ? (Combination.STRAIGHT_FLUSH.rank() << 26) | straightFl
                : fl != 0 ? (Combination.FLUSH.rank() << 26) | fl
                : value;
    }

    private static boolean hasStraight(int set) {
        return 0 != (set&(set>>1)&(set>>2)&(set>>3)&(set>>4));
    }

    private static int getStraight(int set) {
        return set&(set<<1)&(set<<2)&(set<<3)&(set<<4);
    }

    public static void main(String[] args) {
        int count = 0;
        for (int i=0; i<128; i++) {
            if (Integer.bitCount(i)==3) {
                count++;
            }
        }

        System.out.println(count);
        System.out.println(14*14*14*14*13*13*13/count);

        test(rankPokerHand5(new int[]{10, J, A, K, Q}, suits(new int[]{1, 1, 1, 1, 1  }))); // Royal Flush
        test(rankPokerHand5(new int[]{ 4, 5, 6, 7, 8}, suits(new int[]{1, 1, 1, 1, 1  }))); // Straight Flush
        test(rankPokerHand5(new int[]{ 4, 5, 6, 7, 3}, suits(new int[]{1, 1, 1, 1, 1  }))); // Straight Flush
        test(rankPokerHand5(new int[]{ 4, 5, 6, 3, 2}, suits(new int[]{1, 1, 1, 1, 1  }))); // Straight Flush
        test(rankPokerHand5(new int[]{ 2, 3, 4, 5, A}, suits(new int[]{1, 1, 1, 1, 1  }))); // Straight Flush (ace low)
        test(rankPokerHand5(new int[]{ 8, 8, 8, 8, 9}, suits(new int[]{1, 2, 3, 0, 1  }))); // 4 of a Kind
        test(rankPokerHand5(new int[]{ 8, 8, 8, 8, 7}, suits(new int[]{1, 2, 3, 0, 1  }))); // 4 of a Kind
        test(rankPokerHand5(new int[]{ 7, 7, 7, A, 7}, suits(new int[]{1, 2, 3, 0, 1  }))); // 4 of a Kind
        test(rankPokerHand5(new int[]{ 7, 7, 7, 9, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // Full house
        test(rankPokerHand5(new int[]{ 7, 7, 7, 8, 8}, suits(new int[]{1, 2, 3, 1, 2  }))); // Full house
        test(rankPokerHand5(new int[]{ 6, 6, 6, A, A}, suits(new int[]{1, 2, 3, 1, 2  }))); // Full house
        test(rankPokerHand5(new int[]{10, J, 6, K, 9}, suits(new int[]{2, 2, 2, 2, 2  }))); // Flush
        test(rankPokerHand5(new int[]{10, J, 6, 3, 9}, suits(new int[]{2, 2, 2, 2, 2  }))); // Flush
        test(rankPokerHand5(new int[]{10, J, 5, 3, 9}, suits(new int[]{2, 2, 2, 2, 2  }))); // Flush
        test(rankPokerHand5(new int[]{10, J, Q, K, A}, suits(new int[]{1, 2, 3, 2, 0  }))); // Straight
        test(rankPokerHand5(new int[]{10, J, Q, K, 9}, suits(new int[]{1, 2, 3, 2, 0  }))); // Straight
        test(rankPokerHand5(new int[]{10, J, Q, 8, 9}, suits(new int[]{1, 2, 3, 2, 0  }))); // Straight
        test(rankPokerHand5(new int[]{ 2, 3, 4, 5, A}, suits(new int[]{1, 2, 3, 2, 0  }))); // Straight (ace low)
        test(rankPokerHand5(new int[]{ 4, 4, 4, 8, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 3 of a Kind
        test(rankPokerHand5(new int[]{ 4, 4, 4, 8, 7}, suits(new int[]{1, 2, 3, 1, 2  }))); // 3 of a Kind
        test(rankPokerHand5(new int[]{ 3, 3, 3, K, A}, suits(new int[]{1, 2, 3, 1, 2  }))); // 3 of a Kind
        test(rankPokerHand5(new int[]{ 3, 3, 3, 6, 5}, suits(new int[]{1, 2, 3, 1, 2  }))); // 3 of a Kind
        test(rankPokerHand5(new int[]{ 8, 8, J, 9, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 8, 8,10, 9, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 7, 7, Q, 9, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 7, 7, 6, 9, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 6, 6, A, 8, 8}, suits(new int[]{1, 2, 3, 1, 2  }))); // 2 Pair
        test(rankPokerHand5(new int[]{ 8, 8, 3, 5, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 1 Pair
        test(rankPokerHand5(new int[]{ 7, 7, 3, 5, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 1 Pair
        test(rankPokerHand5(new int[]{ 7, 7, 3, 2, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // 1 Pair
        test(rankPokerHand5(new int[]{10, 5, 4, 7, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // High Card
        test(rankPokerHand5(new int[]{10, 5, 3, 7, 9}, suits(new int[]{1, 2, 3, 1, 2  }))); // High Card
        System.out.println("Wrongs: " + testWrongs);

        System.out.print("Setting up lookup tables... ");
        long startTime = System.currentTimeMillis();
        initRankPokerHand7();

        long runTime = System.currentTimeMillis() - startTime;
        System.out.println(runTime + " ms");

        System.out.println("totalLength = " + totalLength);

        startTime = System.currentTimeMillis();
        count7();
        runTime = System.currentTimeMillis() - startTime;
        printHandToCount();
        System.out.println(runTime + " ms");
    }

    private static int[] suits(int[] is) {
        int[] s = new int[is.length];
        for (int i=0; i<is.length; i++) {
            s[i] = suits5[is[i]];
        }
        return s;
    }

    private static long previousTestValue = Long.MAX_VALUE;
    private static int testWrongs = 0;

    private static void test(int rankPokerHand) {
        System.out.println(rankPokerHand + ": " + Combination.fromRank(rankPokerHand >> 26));
        if (rankPokerHand >= previousTestValue) {
            System.out.println("WRONG"); // should not happen
            testWrongs++;
        } else {
            previousTestValue = rankPokerHand;
        }
    }


    private static void printHandToCount() {
        float sum = 0;
        for (int count: handToCount){
            sum += count;
        }
        System.out.println("total hands: " + sum);
        for (int rank=handToCount.length-1; rank>=0; rank--) {
            System.out.println(rank + ": " + Combination.fromRank(rank) + " => " + handToCount[rank] + " (" + handToCount[rank]/sum*100 + "%)");
        }
    }

    static int[] handToCount = new int[12];

    public static int[][] get5HandsFrom7(int[] nrs) {
        int[][] result = new int[21][];
        int index = 0;
        for (int x1=0; x1<7; x1++) {
            for (int x2=x1+1; x2<7; x2++) {
                for (int x3=x2+1; x3<7; x3++) {
                    for (int x4=x3+1; x4<7; x4++) {
                        for (int x5=x4+1; x5<7; x5++) {
                            result[index] = new int[] {nrs[x1], nrs[x2], nrs[x3], nrs[x4], nrs[x5]};
                            index++;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static void count7() {
        int[] nr = new int[7];
        int[] suit = new int[7];
        for (int card1=0; card1<52; card1++) {
            suit[0] = card1%4;
            nr[0] = card1/4 ;
            for (int card2=card1+1; card2<52; card2++) {
                suit[1] = card2%4;
                nr[1] = card2/4 ;
                for (int card3=card2+1; card3<52; card3++) {
                    suit[2] = card3%4;
                    nr[2] = card3/4 ;
                    for (int card4=card3+1; card4<52; card4++) {
                        suit[3] =card4%4;
                        nr[3] = card4/4 ;
                        for (int card5=card4+1; card5<52; card5++) {
                            suit[4] = card5%4;
                            nr[4] = card5/4 ;
                            for (int card6=card5+1; card6<52; card6++) {
                                suit[5] = card6%4;
                                nr[5] = card6/4 ;
                                for (int card7=card6+1; card7<52; card7++) {
                                    suit[6] = card7%4;
                                    nr[6] = card7/4 ;
                                    int rank = rankPokerHand7(nr, suit) >> 26;
                                    handToCount[rank]++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}