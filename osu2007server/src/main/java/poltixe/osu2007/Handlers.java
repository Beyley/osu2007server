package poltixe.osu2007;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        // a5b99395a42bd55bc5eb1d2411cbdf8b:PoltixeTheDerg:f07e856520cefc0b8b0c5cfe1b619e8e:167:25:2:24:17:0:766124:200:False:A:0:True
        String mapHash = req.queryParams("c");

        // System.out.println("Getting all map scores!");
        List<Score> mapScores = sqlHandler.getAllMapScores(mapHash);

        // System.out.println(mapScores.length);

        for (Score score : mapScores) {
            returnString += score.asGetScoresString();
            // System.out.println(returnString);
        }

        System.out.println(returnString);

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

        // System.out.println(String.valueOf(rawBodyBytes));

        return "";
    }
}
