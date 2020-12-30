package poltixe.osu2007;

import java.io.*;
import java.text.ParseException;
import java.util.List;

import poltixe.osu2007.HandlerFunctions.*;
import spark.Request;

public class GameHandlers {
    // Gets a new instance of the MySQL handler
    public MySqlHandler sqlHandler = null;

    GameHandlers() {
        this.sqlHandler = new MySqlHandler();
    }

    public static String[] disallowedNames = { "ppy", "peppy", "cookiezi", "shigetora", "whitecat", "btmc",
            "beasttrollmc", "vaxei", "badeu", "legendre", "nathan on osu", "chocomint", "mrekk", "ryuk", "aeterna",
            "merami", "idke", "flyingtuna", "fgsky", "mathi", "micca", "lifeline", "obito", "karthy", "rafis",
            "paraqeet", "andros", "spare", "umbre", "fieryrage", "okinamo", "akolibed", "ayyeve" };

    @Path(path = "/osu-submit.php", verb = "post")
    public String submit(Request req) {
        String scoreDetails = req.queryParams("score");
        String password = req.queryParams("pass");

        byte[] rawBodyBytes = req.bodyAsBytes();

        byte[] replayData = FileHandler.parseBody(rawBodyBytes);

        Score scoreToSubmit = null;

        try {
            scoreToSubmit = new Score(scoreDetails);

        } catch (ParseException ex) {
            return "Parse exception";
        }

        List<Score> mapScores = sqlHandler.getMapLeaderboard(scoreToSubmit.mapHash);

        boolean newTopOnMap = true;
        int oldTopId = -1;

        for (Score scoreToCheck : mapScores) {
            if (scoreToCheck.userId == scoreToSubmit.userId) {
                if (scoreToSubmit.score < scoreToCheck.score) {
                    newTopOnMap = false;
                } else {
                    oldTopId = scoreToCheck.scoreId;
                }
            }
        }

        Player user = sqlHandler.checkUserData(scoreToSubmit.userId);

        if (scoreToSubmit.pass && user.userPassword.equals(password)) {
            if (newTopOnMap) {
                if (oldTopId == -1) {
                    sqlHandler.addScore(scoreToSubmit, replayData);
                } else {
                    scoreToSubmit.scoreId = oldTopId;
                    sqlHandler.updateScore(oldTopId, scoreToSubmit, replayData);
                }
            }

            sqlHandler.addToPlaycount(scoreToSubmit.userId);
        } else if (user.userPassword.equals(password)) {
            sqlHandler.addFailedScore(scoreToSubmit);

            sqlHandler.addToPlaycount(scoreToSubmit.userId);
        }

        return "";
    }

    // Handles a login request
    @Path(path = "/osu-login.php")
    public String login(Request req) {
        // The string to be returned to the osu! client, in this case has a default
        // value of "1", to indicate the login was sucsessful
        String returnString = "1";

        // Gets the parameters for the login, in this case the username and the password
        String username = req.queryParams("username");
        String password = req.queryParams("password");

        if (!HandlerFunctions.isMD5(password)) {
            return "0";
        }

        if (!HandlerFunctions.isAlphaNumeric(username)) {
            return "0";
        }

        if (username.length() < 2) {
            return "0";
        }

        for (String blacklistedName : disallowedNames) {
            if (username.toLowerCase().contains(blacklistedName)) {
                System.out.println("IP:" + req.ip() + " tried to register with blacklisted name " + username);
                return "0";
            }
        }

        // Gets the users data of the person the client is attempting to sign in as
        Player userData = sqlHandler.checkUserData(sqlHandler.getUserId(username));

        // Checks if the user exists in the database or not
        if (userData.userExists) {
            // Checks if the password is wrong, if so, tell the client that the password was
            // incorrect
            if (!userData.userPassword.toLowerCase().equals(password))
                return "0";

            System.out.println(username + " has logged in from IP: " + req.ip());
            sqlHandler.updateUserIp(username, req.ip());
        } else {
            if (sqlHandler.isIpInUse(req.ip())) {
                System.out.println("IP:" + req.ip() + " tried to register a second account with name " + username);
                return "0";
            }

            // If the user does not exist, create a new user with the specified username and
            // password
            sqlHandler.addUser(username, password, req.ip());
        }

        // Returns the string to be sent to the client
        return returnString;
    }

    @Path(path = "/osu-getreplay.php")
    public byte[] getReplay(Request req) {
        byte[] returnString = {};

        String scoreId = req.queryParams("c");

        try (FileInputStream fos = new FileInputStream("replays/" + scoreId + ".osr")) {
            returnString = fos.readAllBytes();
        } catch (IOException e) {
        }

        return returnString;
    }

    @Path(path = "/osu-getscores.php")
    public String getScores(Request req) {
        String returnString = "";
        String mapHash = req.queryParams("c");

        List<Score> mapScores = sqlHandler.getMapLeaderboard(mapHash);

        for (Score score : mapScores) {
            returnString += score.asGetScoresString();
        }

        return returnString;
    }
}
