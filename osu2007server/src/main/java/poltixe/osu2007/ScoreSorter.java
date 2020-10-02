package poltixe.osu2007;

import java.util.*;

public class ScoreSorter implements Comparator<Player> {
    @Override
    public int compare(Player o2, Player o1) {
        return o1.score - o2.score;
    }
}