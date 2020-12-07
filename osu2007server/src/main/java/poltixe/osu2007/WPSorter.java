package poltixe.osu2007;

import java.util.Comparator;

public class WPSorter implements Comparator<Score> {
    @Override
    public int compare(Score o2, Score o1) {
        return (int) ((o1.wp * 100) - (o2.wp * 100));
    }
}