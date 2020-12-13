package poltixe.osu2007;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class Score {
    public String mapHash;
    public int userId;
    public String username;
    public String replayHash;
    public int hit300;
    public int hit100;
    public int hit50;
    public int hitGeki;
    public int hitKatu;
    public int hitMiss;
    public int score;
    public int maxCombo;
    public boolean perfectCombo;
    public long timeSubmitted;
    public char grade;
    public int mods;
    public boolean pass;
    public int scoreId;
    public double accuracy;

    private static MySqlHandler sqlHandler = new MySqlHandler();

    public static boolean isAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9]*$");
    }

    Score(String scoreString, int scoreId) throws ParseException {
        String[] splitString = scoreString.split(":");

        if (isAlphaNumeric(splitString[0])) {
            this.mapHash = splitString[0];
        } else {
            throw new ParseException("Error when parsing map hash", 0);
        }

        this.userId = sqlHandler.getUserId(splitString[1]);
        this.username = sqlHandler.getUsername(this.userId);

        if (isAlphaNumeric(splitString[2])) {
            this.replayHash = splitString[2];
        } else {
            throw new ParseException("Error when parsing replay hash", 2);
        }

        this.hit300 = Integer.parseInt(splitString[3]);
        this.hit100 = Integer.parseInt(splitString[4]);
        this.hit50 = Integer.parseInt(splitString[5]);
        this.hitGeki = Integer.parseInt(splitString[6]);
        this.hitKatu = Integer.parseInt(splitString[7]);
        this.hitMiss = Integer.parseInt(splitString[8]);
        this.score = Integer.parseInt(splitString[9]);
        this.maxCombo = Integer.parseInt(splitString[10]);
        this.perfectCombo = Boolean.parseBoolean(splitString[11]);

        if (isAlphaNumeric(splitString[12])) {
            this.grade = splitString[12].charAt(0);
        } else {
            throw new ParseException("Error when parsing map grade", 12);
        }

        this.mods = Integer.parseInt(splitString[13]);
        this.pass = Boolean.parseBoolean(splitString[14]);

        this.accuracy = calculateAccuracy();

        this.scoreId = scoreId;
    }

    Score(String scoreString) throws ParseException {
        String[] splitString = scoreString.split(":");

        if (isAlphaNumeric(splitString[0])) {
            this.mapHash = splitString[0];
        } else {
            throw new ParseException("Error when parsing map hash", 0);
        }

        this.userId = sqlHandler.getUserId(splitString[1]);
        this.username = sqlHandler.getUsername(this.userId);

        if (isAlphaNumeric(splitString[2])) {
            this.replayHash = splitString[2];
        } else {
            throw new ParseException("Error when parsing replay hash", 2);
        }

        this.hit300 = Integer.parseInt(splitString[3]);
        this.hit100 = Integer.parseInt(splitString[4]);
        this.hit50 = Integer.parseInt(splitString[5]);
        this.hitGeki = Integer.parseInt(splitString[6]);
        this.hitKatu = Integer.parseInt(splitString[7]);
        this.hitMiss = Integer.parseInt(splitString[8]);
        this.score = Integer.parseInt(splitString[9]);
        this.maxCombo = Integer.parseInt(splitString[10]);
        this.perfectCombo = Boolean.parseBoolean(splitString[11]);

        if (isAlphaNumeric(splitString[12])) {
            this.grade = splitString[12].charAt(0);
        } else {
            throw new ParseException("Error when parsing map grade", 12);
        }

        this.mods = Integer.parseInt(splitString[13]);
        this.pass = Boolean.parseBoolean(splitString[14]);

        this.timeSubmitted = (long) (System.currentTimeMillis() / 1000F);

        this.accuracy = calculateAccuracy();
    }

    public Score(ResultSet rs) throws ParseException {
        try {
            this.scoreId = rs.getInt(1);

            if (isAlphaNumeric(rs.getString(2))) {
                this.mapHash = rs.getString(2);
            } else {
                throw new ParseException("Error when parsing map hash", 0);
            }

            this.userId = rs.getInt(3);
            this.username = sqlHandler.getUsername(this.userId);

            if (isAlphaNumeric(rs.getString(4))) {
                this.replayHash = rs.getString(4);
            } else {
                throw new ParseException("Error when parsing replay hash", 2);
            }

            this.hit300 = rs.getInt(5);
            this.hit100 = rs.getInt(6);
            this.hit50 = rs.getInt(7);
            this.hitGeki = rs.getInt(8);
            this.hitKatu = rs.getInt(9);
            this.hitMiss = rs.getInt(10);
            this.score = rs.getInt(11);
            this.maxCombo = rs.getInt(12);
            this.perfectCombo = rs.getBoolean(13);

            if (isAlphaNumeric(rs.getString(14))) {
                this.grade = rs.getString(14).charAt(0);
            } else {
                throw new ParseException("Error when parsing map grade", 12);
            }

            this.mods = rs.getInt(15);
            this.pass = rs.getBoolean(16);
            this.timeSubmitted = rs.getLong(17);

            this.accuracy = calculateAccuracy();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double calculateAccuracy() {
        double acc = ((double) (300 * (double) this.hit300) + (double) (100 * (double) this.hit100)
                + (double) (50 * (double) this.hit50))
                / (double) (double) (300 * (double) (this.hit300 + this.hit100 + this.hit50 + this.hitMiss));

        acc *= 100.0;

        double roundOff = Math.round((double) (acc * 100.0)) / 100.0;

        return roundOff;
    }

    public String asSubmitString() {
        String combinedString = "";

        combinedString += this.mapHash + ":";
        combinedString += sqlHandler.getUsername(this.userId) + ":";
        combinedString += this.replayHash + ":";
        combinedString += this.hit300 + ":";
        combinedString += this.hit100 + ":";
        combinedString += this.hit50 + ":";
        combinedString += this.hitGeki + ":";
        combinedString += this.hitKatu + ":";
        combinedString += this.hitMiss + ":";
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
        combinedString += this.hit50 + ":";
        combinedString += this.hit100 + ":";
        combinedString += this.hit300 + ":";
        combinedString += this.hitMiss + ":";
        combinedString += this.hitKatu + ":";
        combinedString += this.hitGeki + ":";
        combinedString += this.perfectCombo + ":";
        combinedString += this.mods + "\n";

        return combinedString;
    }
}
