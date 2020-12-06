package poltixe.osu2007;

public class BeatMap {
    public String md5Hash;
    public Score topScore;

    BeatMap(String md5Hash, Score topScore) {
        this.md5Hash = md5Hash;
        this.topScore = topScore;
    }
}
