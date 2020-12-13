package poltixe.osu2007;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BeatMap {
    public String md5;
    public Score topScore;
    public String artist;
    public String title;
    public String diffName;
    public String creator;
    public double starRating;

    public double circleSize;
    public double hpDrainRate;
    public double overallDifficulty;
    public double sliderVelocity;
    public double sliderTickRate;
    public double bpm;
    public double length;
    public double drainTime;

    BeatMap(String md5Hash, Score topScore) {
        this.md5 = md5Hash;
        this.topScore = topScore;
    }

    BeatMap(ResultSet rs) throws SQLException {
        this.md5 = rs.getString(2);
        this.starRating = rs.getDouble(3);
        this.artist = rs.getString(4);
        this.title = rs.getString(5);
        this.diffName = rs.getString(6);
        this.creator = rs.getString(7);
        this.circleSize = rs.getDouble(8);
        this.hpDrainRate = rs.getDouble(9);
        this.overallDifficulty = rs.getDouble(10);
        this.sliderVelocity = rs.getDouble(11);
        this.sliderTickRate = rs.getDouble(12);
        this.bpm = rs.getDouble(13);
        this.length = rs.getDouble(14);
        this.drainTime = rs.getDouble(15);
    }

    BeatMap(String[] split) {
        this.artist = split[0];
        this.title = split[1];
        this.diffName = split[2];
        this.creator = split[3];
        this.md5 = split[4];
        this.starRating = Double.parseDouble(split[5]);
        this.circleSize = Double.parseDouble(split[6]);
        this.hpDrainRate = Double.parseDouble(split[7]);
        this.overallDifficulty = Double.parseDouble(split[8]);
        this.sliderVelocity = Double.parseDouble(split[9]);
        this.sliderTickRate = Double.parseDouble(split[10]);
        this.bpm = Double.parseDouble(split[11]);
        this.length = Double.parseDouble(split[12]);
        this.drainTime = Double.parseDouble(split[13]);
    }
}
