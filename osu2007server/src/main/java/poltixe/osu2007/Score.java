package poltixe.osu2007;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Score {
    public String mapHash;
    public int userId;
    public String username;
    public String replayHash;
    public int hit300Count;
    public int hit100Count;
    public int hit50Count;
    public int hitGekiCount;
    public int hitKatuCount;
    public int hitMissCount;
    public int score;
    public int maxCombo;
    public boolean perfectCombo;
    public char grade;
    public int mods;
    public boolean pass;
    public int scoreId;
    public double accuracy;

    private static MySqlHandler sqlHandler = new MySqlHandler();

    Score(String scoreString, int scoreId) {
        String[] splitString = scoreString.split(":");

        this.mapHash = splitString[0];
        this.userId = sqlHandler.getUserId(splitString[1]);
        this.username = sqlHandler.getUsername(this.userId);
        this.replayHash = splitString[2];
        this.hit300Count = Integer.parseInt(splitString[3]);
        this.hit100Count = Integer.parseInt(splitString[4]);
        this.hit50Count = Integer.parseInt(splitString[5]);
        this.hitGekiCount = Integer.parseInt(splitString[6]);
        this.hitKatuCount = Integer.parseInt(splitString[7]);
        this.hitMissCount = Integer.parseInt(splitString[8]);
        this.score = Integer.parseInt(splitString[9]);
        this.maxCombo = Integer.parseInt(splitString[10]);
        this.perfectCombo = Boolean.parseBoolean(splitString[11]);
        this.grade = splitString[12].charAt(0);
        this.mods = Integer.parseInt(splitString[13]);
        this.pass = Boolean.parseBoolean(splitString[14]);

        this.accuracy = calculateAccuracy();

        this.scoreId = scoreId;
    }

    Score(String scoreString) {
        String[] splitString = scoreString.split(":");

        this.mapHash = splitString[0];
        this.userId = sqlHandler.getUserId(splitString[1]);
        this.username = sqlHandler.getUsername(this.userId);
        this.replayHash = splitString[2];
        this.hit300Count = Integer.parseInt(splitString[3]);
        this.hit100Count = Integer.parseInt(splitString[4]);
        this.hit50Count = Integer.parseInt(splitString[5]);
        this.hitGekiCount = Integer.parseInt(splitString[6]);
        this.hitKatuCount = Integer.parseInt(splitString[7]);
        this.hitMissCount = Integer.parseInt(splitString[8]);
        this.score = Integer.parseInt(splitString[9]);
        this.maxCombo = Integer.parseInt(splitString[10]);
        this.perfectCombo = Boolean.parseBoolean(splitString[11]);
        this.grade = splitString[12].charAt(0);
        this.mods = Integer.parseInt(splitString[13]);
        this.pass = Boolean.parseBoolean(splitString[14]);

        this.accuracy = calculateAccuracy();
    }

    public Score(ResultSet rs) {
        try {
            this.scoreId = rs.getInt(1);
            this.mapHash = rs.getString(2);
            this.userId = rs.getInt(3);
            this.username = sqlHandler.getUsername(this.userId);
            this.replayHash = rs.getString(4);
            this.hit300Count = rs.getInt(5);
            this.hit100Count = rs.getInt(6);
            this.hit50Count = rs.getInt(7);
            this.hitGekiCount = rs.getInt(8);
            this.hitKatuCount = rs.getInt(9);
            this.hitMissCount = rs.getInt(10);
            this.score = rs.getInt(11);
            this.maxCombo = rs.getInt(12);
            this.perfectCombo = Boolean.parseBoolean(rs.getString(13));
            this.grade = rs.getString(14).charAt(0);
            this.mods = rs.getInt(15);
            this.pass = Boolean.parseBoolean(rs.getString(16));

            this.accuracy = calculateAccuracy();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double calculateAccuracy() {
        double acc = ((double) (300 * (double) this.hit300Count) + (double) (100 * (double) this.hit100Count)
                + (double) (50 * (double) this.hit50Count))
                / (double) (double) (300
                        * (double) (this.hit300Count + this.hit100Count + this.hit50Count + this.hitMissCount));

        acc *= 100.0;

        double roundOff = Math.round((double) (acc * 100.0)) / 100.0;

        return roundOff;
    }

    public String asSubmitString() {
        String combinedString = "";

        combinedString += this.mapHash + ":";
        combinedString += sqlHandler.getUsername(this.userId) + ":";
        combinedString += this.replayHash + ":";
        combinedString += this.hit300Count + ":";
        combinedString += this.hit100Count + ":";
        combinedString += this.hit50Count + ":";
        combinedString += this.hitGekiCount + ":";
        combinedString += this.hitKatuCount + ":";
        combinedString += this.hitMissCount + ":";
        combinedString += this.score + ":";
        combinedString += this.maxCombo + ":";
        combinedString += this.perfectCombo + ":";
        combinedString += this.grade + ":";
        combinedString += this.mods + ":";
        combinedString += this.pass;

        return combinedString;
    }

    public String asGetScoresString() {
        String combinedString = "";

        combinedString += this.scoreId + ":";
        combinedString += this.username + ":";
        combinedString += this.score + ":";
        combinedString += this.maxCombo + ":";
        combinedString += this.hit50Count + ":";
        combinedString += this.hit100Count + ":";
        combinedString += this.hit300Count + ":";
        combinedString += this.hitMissCount + ":";
        combinedString += this.hitKatuCount + ":";
        combinedString += this.hitGekiCount + ":";
        combinedString += this.perfectCombo + ":";
        combinedString += this.mods + "\n";

        return combinedString;
    }
}
