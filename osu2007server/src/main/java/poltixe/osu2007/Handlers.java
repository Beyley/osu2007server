package poltixe.osu2007;

import java.io.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spark.Request;

public class Handlers {
    // Gets a new instance of the MySQL handler
    public static MySqlHandler sqlHandler = new MySqlHandler();

    public static boolean isMD5(String s) {
        return s.matches("^[a-fA-F0-9]{32}$");
    }

    public static boolean isAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9]*$");
    }

    private static String toHexString(byte[] bytes) {
        char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v / 16];
            hexChars[j * 2 + 1] = hexArray[v % 16];
        }

        return new String(hexChars);
    }

    public static String getNavbar() {
        String returnString = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/navbar.html");

        try {
            returnString = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return returnString.toString();
    }

    public static String getStandardHeader() {
        String returnString = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/header.html");

        try {
            returnString = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return returnString.toString();
    }

    public static String getJSFooter() {
        String returnString = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/js.html");

        try {
            returnString = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return returnString.toString();
    }

    public static String createHtmlPage(String content) {
        StringBuilder returnString = new StringBuilder();

        returnString.append(getStandardHeader() + "\n");
        returnString.append("<body>" + getNavbar() + "\n");

        returnString.append("<div class=\"container\"> <div class=\"starter-template\">\n");

        returnString.append(content);

        returnString.append("</div>\n</div>\n");
        returnString.append(getJSFooter());

        returnString.append("</body>");

        return returnString.toString();
    }

    public static String getNameChangePage(Request req) {
        String content = "<form action=\"/web/namechange\"> <label for=\"oldusername\">Old username:</label> <input type=\"text\" id=\"oldusername\" name=\"oldusername\"><br><br> <label for=\"newusername\">New username:</label><input type=\"text\" id=\"newusername\" name=\"newusername\"><br><br> <label for=\"password\">Password:</label><input type=\"password\" id=\"password\" name=\"password\"><br><br> <input type=\"submit\" value=\"Submit\"> </form>";

        if (req.queryParams("oldusername") != null && req.queryParams("newusername") != null
                && req.queryParams("password") != null) {
            Player player = sqlHandler.checkUserData(sqlHandler.getUserId(req.queryParams("oldusername")));

            // if (playerTaken) {
            // returnString += "<br> That username is taken!";
            // return returnString;
            // }

            // if (!playerExists) {
            // returnString += "<br> That user does not exist!";
            // return returnString;
            // } else {
            String password = req.queryParams("password");

            String hashedPassword = "";

            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(password.getBytes());
                byte[] digest = md.digest();
                hashedPassword = toHexString(digest).toLowerCase();
            } catch (Exception ex) {
            }

            if (hashedPassword.equals(player.userPassword)) {
                sqlHandler.changeUsername(player.userId, req.queryParams("newusername"));
                content += "<br> Username changed!";
                return content;
            } else {
                content += "<br> That password is incorrect!";
                return content;
            }
        }
        // }

        return createHtmlPage(content);
    }

    public static String getUserPage(Request req) {
        String content = "";

        int userId = Integer.parseInt(req.queryParams("id"));

        Player thisPlayer = sqlHandler.checkUserData(userId);

        thisPlayer.globalRank = 0;

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

        // Iterates through all the players
        for (int i = 0; i < allPlayers.size(); i++) {
            Player player = allPlayers.get(i);
            if (player.userId == thisPlayer.userId) {
                thisPlayer.globalRank = i + 1;
            }
        }

        thisPlayer.calculateOverallAccuracy();
        thisPlayer.calculateOverallWP();

        content += "<link rel=\"stylesheet\" href=\"/web/userpage.css\">";

        Html.Image rankedMap = new Html.Image("/web/selection-ranked.png");
        Html.Image approvedMap = new Html.Image("/web/selection-approved.png");
        Html.Image avatar = new Html.Image("/web/testavatar.png");

        avatar.style = "text-align: center; vertical-align: middle;";

        content += "<table class=\"center\"><tr><td>" + avatar.getAsHtml() + "</td>\n";

        // new DecimalFormat("#.##").format(thisPlayer.wp)

        content += "<td style=\"vertical-align: middle;\">"
                + Html.header1(thisPlayer.displayUsername + Html.bold(" (#" + thisPlayer.globalRank + ")\n", ""), "");
        content += Html.header2("Ranked Score : " + thisPlayer.rankedScore, "")
                + Html.header3("Overall Accuracy: " + thisPlayer.accuracy, "") + "</td></table>";

        content += Html.header2(
                Html.bold("<br>Experimental: Overall WP " + new DecimalFormat("#.##").format(thisPlayer.wp) + "<br>",
                        "clear: both;"),
                "");
        content += Html.header2(Html.bold("<br>Top plays<br>", "clear: both;"), "");
        content += "<table border=\"1\" class=\"center\"><tr> <th>Status</th> <th>Song</th> <th>Score</th> <th>WP</th> <th>Accuracy</th> <th>Grade</th> </tr>";

        List<Score> scores = sqlHandler.getAllScoresOfUser(userId);

        scores.sort(new MapLeaderBoardSorter());

        List<BeatMap> rankedMaps = sqlHandler.getAllRankedMaps();

        for (Score score : scores) {
            BeatMap thisMap = null;

            for (BeatMap map : rankedMaps) {
                if (map.md5.equals(score.mapHash)) {
                    thisMap = map;
                }
            }

            if (thisMap == null) {
                content += "<tr> <td style=\"text-align: center;\">" + approvedMap.getAsHtml() + "</td> <td>"
                        + score.mapHash + "</td> <td>" + score.score + "</td> <td>"
                        + new DecimalFormat("#.##").format(score.wp) + "</td> <td>" + score.accuracy + "</td> <td>"
                        + score.grade + "</td> </tr>";
                // content += score.mapHash + " : " + score.score + " : " + score.grade;
            } else {
                content += "<tr> <td style=\"text-align: center;\">" + rankedMap.getAsHtml() + "</td> <td>"
                        + thisMap.artist + " - " + thisMap.songName + " (" + thisMap.creator + ") [" + thisMap.diffName
                        + "]" + "</td> <td>" + score.score + "</td> <td>" + new DecimalFormat("#.##").format(score.wp)
                        + "</td> <td>" + score.accuracy + "</td> <td>" + score.grade + "</td> </tr>";
                // content += rankedMap.getAsHtml() + thisMap.artist + " - " + thisMap.songName
                // + " (" + thisMap.creator
                // + ") [" + thisMap.diffName + "] : " + score.score + " : " + score.grade;
            }
        }
        content += "</table>";

        return createHtmlPage(content);
    }

    // Handles a login request
    public static String login(Request req) {
        // The string to be returned to the osu! client, in this case has a default
        // value of "1", to indicate the login was sucsessful
        String returnString = "1";

        // Gets the parameters for the login, in this case the username and the password
        String username = req.queryParams("username");
        String password = req.queryParams("password");

        if (!isMD5(password)) {
            return "0";
        }

        if (!isAlphaNumeric(username)) {
            return "0";
        }

        // Gets the users data of the person the client is attempting to sign in as
        Player userData = sqlHandler.checkUserData(sqlHandler.getUserId(username));

        // Checks if the user exists in the database or not
        if (userData.userExists) {
            // Checks if the password is wrong, if so, tell the client that the password was
            // incorrect
            if (!userData.userPassword.toLowerCase().equals(password))
                returnString = "0";
        } else {
            // If the user does not exist, create a new user with the specified username and
            // password
            sqlHandler.addUser(username, password);
        }

        // Returns the string to be sent to the client
        return returnString;
    }

    // Handles a request to get all the top players
    public static String getTopPlayers(Request req) {
        // The string to be returned to the osu! client, in this case has the title text
        // to be added on to
        String content = "<h1>Global Rankings</h1>";

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

        content += "<link rel=\"stylesheet\" href=\"/web/userpage.css\">";
        content += "<table border=\"1\" class=\"center\"><tr> <th>Rank</th> <th>Username</th> <th>Ranked Score</th> <th>Accuracy</th> <th>#1 Count</th> </tr>";

        int currentRank = 1;

        // Iterates through all the players
        for (int i = 0; i < allPlayers.size(); i++) {
            Player player = allPlayers.get(i);
            // Adds the player info to the string to be sent to the client
            // <p class="lead">ranking go brrrrrrrr</p>
            player.calculateOverallAccuracy();

            if (player.rankedScore > 0) {
                content += "<tr> <td style=\"text-align: center;\">" + ("#" + currentRank) + "</td> <td>"
                        + player.displayUsername + "</td> <td>" + player.rankedScore + "</td> <td>"
                        + new DecimalFormat("#.##").format(player.accuracy) + "</td> <td>" + player.amountOfNumberOnes
                        + "</td> </tr>";
                // content += "<p class\"lead\"> #" + (i + 1) + " : " + player.displayUsername +
                // ", Total Ranked Score : "
                // + player.rankedScore + ", Accuracy : " + player.accuracy + ", " +
                // player.amountOfNumberOnes
                // + " #1's</p>";
                currentRank += 1;
            }
        }

        content += "</table>";

        // Returns the string to the client
        return createHtmlPage(content);
    }

    public static String getScores(Request req) {
        String returnString = "";
        String mapHash = req.queryParams("c");

        List<Score> mapScores = sqlHandler.getMapLeaderboard(mapHash);

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
        }

        return "";
    }
}
