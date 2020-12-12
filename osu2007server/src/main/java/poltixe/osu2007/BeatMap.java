package poltixe.osu2007;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BeatMap {
    public String md5;
    public Score topScore;
    public String artist;
    public String songName;
    public String diffName;
    public String creator;
    public double starRating;

    BeatMap(String md5Hash, Score topScore) {
        this.md5 = md5Hash;
        this.topScore = topScore;
    }

    BeatMap(ResultSet rs) throws SQLException {
        this.md5 = rs.getString(2);
        this.artist = rs.getString(3);
        this.songName = rs.getString(4);
        this.diffName = rs.getString(5);
        this.creator = rs.getString(6);
    }

    BeatMap(String artist, String songName, String diffName, String creator, String md5, String sr) {
        this.artist = artist;
        this.songName = songName;
        this.diffName = diffName;
        this.creator = creator;
        this.md5 = md5;
        this.starRating = Double.parseDouble(sr);
    }
}
