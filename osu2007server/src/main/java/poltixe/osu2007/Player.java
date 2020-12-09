package poltixe.osu2007;

import java.util.*;

public class Player {
    public int userId;
    public String username;
    public String displayUsername;
    public int rankedScore;
    public int amountOfNumberOnes;
    public String userPassword;
    public boolean userExists;
    public int globalRank;
    public double accuracy;
    public double wp;

    private static MySqlHandler sqlHandler = new MySqlHandler();

    Player(int userId) {
        this.userId = userId;
        this.username = sqlHandler.getUsername(this.userId);
        this.displayUsername = "<a href=\"/web/u?id=" + this.userId + "\">" + this.username + "</a>";
        this.rankedScore = sqlHandler.getRankedScoreOfUser(this.userId);
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

    public void calculateOverallWP() {
        List<Score> allScores = sqlHandler.getAllScoresOfUser(userId);

        allScores.sort(new WPSorter());

        double wp = 0;

        List<BeatMap> rankedMaps = sqlHandler.getAllRankedMaps();

        int n = 1;
        int rankedScoreSize = 0;

        for (Score score : allScores) {
            boolean ranked = false;

            for (BeatMap map : rankedMaps) {
                if (map.md5.equals(score.mapHash)) {
                    ranked = true;
                }
            }

            if (ranked) {
                // System.out.println("num" + n + " Unweighted:" + score.wp + " Weighted:"
                // + (double) score.wp * (double) Math.pow((double) 0.95, (double) n));
                wp += (double) score.wp * (double) Math.pow((double) 0.95, (double) n);
                // System.out.println(wp);
                n++;
                rankedScoreSize++;
            }
        }

        // sum *= 100.0;

        this.wp = wp;
    }

    Player(int userId, String userPassword, boolean userExists) {
        this.userId = userId;
        this.username = sqlHandler.getUsername(this.userId);
        this.displayUsername = "<a href=\"/web/u?id=" + this.userId + "\">" + this.username + "</a>";
        this.userPassword = userPassword;
        this.userExists = userExists;
        this.rankedScore = sqlHandler.getRankedScoreOfUser(this.userId);
    }
}
