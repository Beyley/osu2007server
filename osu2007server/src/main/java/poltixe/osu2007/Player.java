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

        for (Score score : allScores) {
            sum += score.accuracy;
        }

        // sum *= 100.0;

        double roundOff = Math.round((double) ((double) sum / (double) allScores.size()) * 100.0) / 100.0;

        this.accuracy = roundOff;
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
