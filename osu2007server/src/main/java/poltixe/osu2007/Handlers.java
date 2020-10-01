package poltixe.osu2007;

import java.io.FileInputStream;
import java.io.IOException;
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

    public static String getScores(Request req) {
        String returnString = "";
        String mapHash = req.queryParams("c");

        List<Score> mapScores = sqlHandler.getAllMapScores(mapHash);

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
