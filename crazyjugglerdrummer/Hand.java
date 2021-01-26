package crazyjugglerdrummer;

public class Hand {
    private Card[] cards;
    private int[] value;

    Hand(Deck d)
    {
        value = new int[6];
        cards = new Card[5];
        for (int x=0; x<5; x++)
        {
            cards[x] = d.drawFromDeck();
        }

        int[] ranks = new int[14];
        //miscellaneous cards that are not otherwise significant
        int[] orderedRanks = new int[5];
        boolean flush=true, straight=false;
        int sameCards=1,sameCards2=1;
        int largeGroupRank=0,smallGroupRank=0;
        int index=0;
        int topStraightValue=0;

        for (int x=0; x<=13; x++)
        {
            ranks[x]=0;
        }
        for (int x=0; x<=4; x++)
        {
            ranks[ cards[x].getRank() ]++;
        }
        for (int x=0; x<4; x++) {
            if ( cards[x].getSuit() != cards[x+1].getSuit() )
                flush=false;
        }

        for (int x=13; x>=1; x--)
        {
            if (ranks[x] > sameCards)
            {
                if (sameCards != 1)
                //if sameCards was not the default value
                {
                    sameCards2 = sameCards;
                    smallGroupRank = largeGroupRank;
                }

                sameCards = ranks[x];
                largeGroupRank = x == 1 ? 14 : x;

            } else if (ranks[x] > sameCards2)
            {
                sameCards2 = ranks[x];
                smallGroupRank = x == 1 ? 14 : x;
            }
        }

        if (ranks[1]==1) //if ace, run this before because ace is highest card
        {
            orderedRanks[index]=14;
            index++;
        }

        for (int x=13; x>=2; x--)
        {
            if (ranks[x]==1)
            {
                orderedRanks[index]=x;
                index++;
            }
        }

        for (int x=0; x<=9; x++)
        //can't have straight with lowest value of more than 10
        {
            if (ranks[x]==1 && ranks[x+1]==1 && ranks[x+2]==1 &&
                    ranks[x+3]==1 && ranks[x+4]==1)
            {
                straight=true;
                topStraightValue=x+4; //4 above bottom value
                break;
            }
        }

        if (ranks[10]==1 && ranks[11]==1 && ranks[12]==1 &&
                ranks[13]==1 && ranks[1]==1) //ace high
        {
            straight=true;
            topStraightValue=14; //higher than king
        }

        for (int x=0; x<=5; x++)
        {
            value[x]=0;
        }


        //start hand evaluation
        if ( sameCards==1 ) {
            value[0]=1;
            value[1]=orderedRanks[0];
            value[2]=orderedRanks[1];
            value[3]=orderedRanks[2];
            value[4]=orderedRanks[3];
            value[5]=orderedRanks[4];
        }

        if (sameCards==2 && sameCards2==1)
        {
            value[0]=2;
            value[1]=largeGroupRank; //rank of pair
            value[2]=orderedRanks[0];
            value[3]=orderedRanks[1];
            value[4]=orderedRanks[2];
        }

        if (sameCards==2 && sameCards2==2) //two pair
        {
            value[0]=3;
            //rank of greater pair
            value[1]= largeGroupRank>smallGroupRank ? largeGroupRank : smallGroupRank;
            value[2]= largeGroupRank<smallGroupRank ? largeGroupRank : smallGroupRank;
            value[3]=orderedRanks[0];  //extra card
        }

        if (sameCards==3 && sameCards2!=2)
        {
            value[0]=4;
            value[1]= largeGroupRank;
            value[2]=orderedRanks[0];
            value[3]=orderedRanks[1];
        }

        if (straight && !flush)
        {
            value[0]=5;
            value[1]=topStraightValue;
        }

        if (flush && !straight)
        {
            value[0]=6;
            value[1]=orderedRanks[0]; //tie determined by ranks of cards
            value[2]=orderedRanks[1];
            value[3]=orderedRanks[2];
            value[4]=orderedRanks[3];
            value[5]=orderedRanks[4];
        }

        if (sameCards==3 && sameCards2==2)
        {
            value[0]=7;
            value[1]=largeGroupRank;
            value[2]=smallGroupRank;
        }

        if (sameCards==4)
        {
            value[0]=8;
            value[1]=largeGroupRank;
            value[2]=orderedRanks[0];
        }

        if (straight && flush)
        {
            value[0]=9;
            value[1]=orderedRanks[0];
        }

    }

    void display()
    {
        String s;
        switch( value[0] )
        {

            case 1:
                s="high card";
                break;
            case 2:
                s="pair of " + Card.rankAsString(value[1]) + "\'s";
                break;
            case 3:
                s="two pair " + Card.rankAsString(value[1]) + " " +
                        Card.rankAsString(value[2]);
                break;
            case 4:
                s="three of a kind " + Card.rankAsString(value[1]) + "\'s";
                break;
            case 5:
                s=Card.rankAsString(value[1]) + " high straight";
                break;
            case 6:
                s="flush";
                break;
            case 7:
                s="full house " + Card.rankAsString(value[1]) + " over " +
                        Card.rankAsString(value[2]);
                break;
            case 8:
                s="four of a kind " + Card.rankAsString(value[1]);
                break;
            case 9:
                s="straight flush " + Card.rankAsString(value[1]) + " high";
                break;
            default:
                s="error in Hand.display: value[0] contains invalid value";
        }
        s = "                " + s;
        System.out.println(s);
    }

    void displayAll()
    {
        for (int x=0; x<5; x++)
            System.out.println(cards[x]);
    }

    int compareTo(Hand that)
    {
        for (int x=0; x<6; x++)
        {
            if (this.value[x]>that.value[x])
                return 1;
            else if (this.value[x]<that.value[x])
                return -1;
        }
        return 0; //if hands are equal
    }
}