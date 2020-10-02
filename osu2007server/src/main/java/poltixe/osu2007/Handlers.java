package poltixe.osu2007;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import spark.Request;

public class Handlers {
    public static MySqlHandler sqlHandler = new MySqlHandler();

    public static String login(Request req) {
        String returnString = "1";

        String username = req.queryParams("username");
        String password = req.queryParams("password");

        User userData = sqlHandler.checkUserData(username);

        if (userData.userExists) {
            if (!userData.userPassword.equals(password))
                returnString = "0";
        } else {
            sqlHandler.addUser(username, password);
        }

        return returnString;
    }

    public static String getTopPlayers(Request req) {
        String returnString = "Top players!<br>";

        List<Score> allScores = sqlHandler.getAllScores();

        List<Player> allPlayers = new ArrayList<Player>();

        for (int i = 0; i < allScores.size(); i++) {
            Score score = allScores.get(i);

            boolean playerInList = false;
            for (Player player : allPlayers) {
                if (score.playerUsername.equals(player.username)) {
                    playerInList = true;
                }
            }

            if (!playerInList) {
                allPlayers.add(new Player(score.playerUsername, score.score));
            } else {
                for (int playerI = 0; playerI < allPlayers.size(); playerI++) {
                    Player player = allPlayers.get(playerI);
                    if (score.playerUsername.equals(player.username)) {
                        allPlayers.set(playerI, new Player(player.username, player.score + score.score));
                    }
                }
            }

            // returnString += score.playerUsername + "<br>";
        }

        Collections.sort(allPlayers, new ScoreSorter());

        for (Player player : allPlayers) {
            returnString += player.username + ":" + player.score + "<br>";
        }

        return returnString;
    }

    public static String getScores(Request req) {
        String returnString = "";
        String mapHash = req.queryParams("c");

        List<Score> mapScores = sqlHandler.getAllMapScores(mapHash);

        Collections.sort(mapScores, new MapLeaderBoardSorter());

        for (Score score : mapScores) {
            returnString += score.asGetScoresString();
        }

        return returnString;
    }

    public static byte[] getReplay(Request req) {
        byte[] returnString = {};

        String scoreId = req.queryParams("c");

        try (FileInputStream fos = new FileInputStream("replays/" + scoreId + ".osr")) {
            // returnString = new String(fos.readAllBytes());
            returnString = fos.readAllBytes();
        } catch (IOException e) {
        }

        return returnString;
    }

    public static String submit(Request req) {
        String scoreDetails = req.queryParams("score");
        String password = req.queryParams("pass");

        byte[] rawBodyBytes = req.bodyAsBytes();

        byte[] replayData = FileHandler.parseBody(rawBodyBytes);

        Score scoreToSubmit = new Score(scoreDetails);

        List<Score> mapScores = sqlHandler.getAllMapScores(scoreToSubmit.osuFileHash);

        boolean newTopOnMap = true;

        for (Score scoreToCheck : mapScores) {
            if (scoreToCheck.playerUsername.equals(scoreToSubmit.playerUsername)) {
                if (scoreToSubmit.score < scoreToCheck.score) {
                    newTopOnMap = false;
                } else {
                    sqlHandler.removeScore(scoreToCheck);
                }
            }
        }

        User user = sqlHandler.checkUserData(scoreToSubmit.playerUsername);

        if (scoreToSubmit.pass && user.userPassword.equals(password)) {
            if (newTopOnMap)
                sqlHandler.addScore(scoreToSubmit, replayData);
        }
        return "";
    }
}
