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

    public long lastPing;

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

    public void calculateUserRank() {
        // Creates a list of all scores submitted
        List<Score> allScores = sqlHandler.getAllScores();

        // Creates a list to store all unique players and their ranked score
        List<Player> allPlayers = new ArrayList<Player>();

        List<BeatMap> allMaps = new ArrayList<BeatMap>();

        // Loops through all submitted scores
        for (int i = 0; i < allScores.size(); i++) {
            // Gets the score we are currently iterating on
            Score score = allScores.get(i);

            // Creates a variable with a default value of false to store whether the player
            // is already in the player list
            boolean playerInList = false;
            // Loops through all players
            for (Player player : allPlayers) {
                // Checks if the current player we are iterating on is equal to the playername
                // in the score
                if (score.userId == player.userId) {
                    // Sets the variable to show that the player is in fact inside of the playerlist
                    playerInList = true;
                }
            }

            // Checks if the player is or is not in the list
            if (!playerInList) {
                // If they are not add them to the list with the score of the current score we
                // are iterating on
                allPlayers.add(new Player(score.userId));
            }
        }

        for (int i = 0; i < allScores.size(); i++) {
            Score score = allScores.get(i);
            boolean mapInList = false;
            // Loops through all players
            for (BeatMap map : allMaps) {
                if (score.mapHash.equals(map.md5)) {
                    // Sets the variable to show that the player is in fact inside of the playerlist
                    mapInList = true;
                }
            }

            if (mapInList) {
                for (int mapI = 0; mapI < allMaps.size(); mapI++) {
                    BeatMap map = allMaps.get(mapI);

                    if (map.topScore.score < score.score && map.md5.equals(score.mapHash)) {
                        BeatMap oldTop = allMaps.get(mapI);
                        allMaps.set(mapI, new BeatMap(map.md5, score));

                        for (int playerI = 0; playerI < allPlayers.size(); playerI++) {
                            Player player = allPlayers.get(playerI);

                            if (score.userId == player.userId) {
                                allPlayers.get(playerI).amountOfNumberOnes += 1;
                            }

                            if (oldTop.topScore.userId == player.userId) {
                                allPlayers.get(playerI).amountOfNumberOnes -= 1;
                            }
                        }
                    }
                }
            } else {
                allMaps.add(new BeatMap(score.mapHash, score));

                for (int playerI = 0; playerI < allPlayers.size(); playerI++) {
                    Player player = allPlayers.get(playerI);

                    if (score.userId == player.userId) {
                        allPlayers.get(playerI).amountOfNumberOnes += 1;
                    }
                }
            }
        }

        // Sorts the players in the correct order
        Collections.sort(allPlayers, new ScoreSorter());

        int currentRank = 1;

        // Iterates through all the players
        for (int i = 0; i < allPlayers.size(); i++) {
            if (allPlayers.get(i).username.equals(this.username)) {
                this.globalRank = currentRank;
                break;
            }

            currentRank++;
        }
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
