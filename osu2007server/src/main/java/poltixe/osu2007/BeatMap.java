package poltixe.osu2007;

public class BeatMap {
    public String hash;
    public Score topScore;

    BeatMap(String hash, Score topScore) {
        this.hash = hash;
        this.topScore = topScore;
    }
}
