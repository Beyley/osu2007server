package poltixe.osu2007;

import java.util.*;

public class Player {
    public int userId;
    public String username;
    public String displayUsername;
    public int rankedScore;
    public int totalScore;
    public int playcount;
    public int amountOfNumberOnes;
    public String userPassword;
    public boolean userExists;
    public int globalRank;
    public double accuracy;

    private static MySqlHandler sqlHandler = new MySqlHandler();

    Player(int userId) {
        this.userId = userId;
        this.username = sqlHandler.getUsername(this.userId);
        this.displayUsername = "<a href=\"/web/u?id=" + this.userId + "\">" + this.username + "</a>";
        this.rankedScore = sqlHandler.getRankedScoreOfUser(this.userId);
        this.totalScore = sqlHandler.getTotalScoreOfUser(this.userId);
        this.playcount = sqlHandler.getPlaycountOfUser(this.userId);
        this.amountOfNumberOnes = 0;
    }

    public void calculateOverallAccuracy() {
        List<Score> allScores = sqlHandler.getAllScoresOfUser(userId);

        double sum = 0;

        List<BeatMap> rankedMaps = sqlHandler.getAllRankedMaps();

        int rankedScoreSize = 0;

        for (Score score : allScores) {
            boolean ranked = false;

            for (BeatMap map : rankedMaps) {
                if (map.md5.equals(score.mapHash)) {
                    ranked = true;
                }
            }

            if (ranked) {
                sum += score.accuracy;
                rankedScoreSize++;
            }
        }

        this.accuracy = (double) sum / (double) rankedScoreSize;
    }

    Player(int userId, String userPassword, boolean userExists) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userExists = userExists;

        if (userId == -1) {
            return;
        }

        this.username = sqlHandler.getUsername(this.userId);
        this.displayUsername = "<a href=\"/web/u?id=" + this.userId + "\">" + this.username + "</a>";
        this.rankedScore = sqlHandler.getRankedScoreOfUser(this.userId);
        this.totalScore = sqlHandler.getTotalScoreOfUser(this.userId);
        this.playcount = sqlHandler.getPlaycountOfUser(this.userId);
    }
}
