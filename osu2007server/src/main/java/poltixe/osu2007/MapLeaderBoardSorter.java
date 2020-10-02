package poltixe.osu2007;

import java.util.Comparator;

public class MapLeaderBoardSorter implements Comparator<Score> {
    @Override
    public int compare(Score o2, Score o1) {
        return o1.score - o2.score;
    }
}