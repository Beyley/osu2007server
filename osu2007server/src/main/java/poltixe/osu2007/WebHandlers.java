package poltixe.osu2007;

import java.io.*;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import poltixe.osu2007.Html.Image;
import spark.Request;

public class WebHandlers {
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

    public static String getFooter() {
        String returnString = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/footer.html");

        try {
            returnString = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return returnString.toString();
    }

    public static String createHtmlPage(String content) {
        StringBuilder returnString = new StringBuilder();

        returnString.append(getStandardHeader() + "\n");

        returnString.append(content);

        returnString.append(getFooter());

        return returnString.toString();
    }

    public static String newsPage(Request req) {
        String content = "";

        for (NewsPost post : sqlHandler.getAllNewsPosts()) {
            content += "<div id=\"article\"> <span class=\"subject\">" + post.title
                    + "<br></span> <span class=\"byline\"><img height=\"32\" width=\"32\" src=\"./testavatar.png\" alt=\"\">"
                    + post.creator.displayUsername + "<br>" + post.timestamp.toLocaleString()
                    + "<br><br></span>  <span class=\"message\">" + post.content + "</span></div>";
        }

        return createHtmlPage(content);
    }

    public static String aboutPage(Request req) {
        String content = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/aboutpagecontent.html");

        try {
            content = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return createHtmlPage(content);
    }

    public static String changelogPage(Request req) {
        String content = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/changelogpagecontent.html");

        try {
            content = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return createHtmlPage(content);
    }

    public static String downloadPage(Request req) {
        String content = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/downloadpagecontent.html");

        try {
            content = new String(is.readAllBytes());

            content = content.replace("%SERVERIP%", "osu2007.faithy3.moe");
        } catch (IOException e) {
        }

        return createHtmlPage(content);
    }

    public static String faqPage(Request req) {
        String content = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/faqpagecontent.html");

        try {
            content = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return createHtmlPage(content);
    }

    public static String maplistingPage(Request req) {
        String content = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/maplistingpagecontent.html");

        try {
            content = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return createHtmlPage(content);
    }

    private static String createStarPattern(int filled, int max) {
        String returnString = "";

        int numberUnfilled = max - filled;

        for (int i = 1; i <= filled; i++) {
            returnString += "<img height=\"14\" width=\"14\" src=\"/web/globalfiles/star.png\">";
        }

        for (int i = 1; i <= numberUnfilled; i++) {
            returnString += "<img height=\"14\" width=\"14\" src=\"/web/globalfiles/starn.png\">";
        }

        return returnString;
    }

    public static String mapPage(Request req) {
        String content = "";

        String mapHash = req.queryParams("map");

        Image xRank = new Image("/web/globalfiles/X.png");
        Image sRank = new Image("/web/globalfiles/S.png");
        Image aRank = new Image("/web/globalfiles/A.png");
        Image bRank = new Image("/web/globalfiles/B.png");
        Image cRank = new Image("/web/globalfiles/C.png");
        Image dRank = new Image("/web/globalfiles/D.png");

        Image xRankSmall = new Image("/web/globalfiles/X_small.png");
        Image sRankSmall = new Image("/web/globalfiles/S_small.png");
        Image aRankSmall = new Image("/web/globalfiles/A_small.png");
        Image bRankSmall = new Image("/web/globalfiles/B_small.png");
        Image cRankSmall = new Image("/web/globalfiles/C_small.png");
        Image dRankSmall = new Image("/web/globalfiles/D_small.png");

        List<BeatMap> rankedMaps = sqlHandler.getAllRankedMaps();

        BeatMap map = null;

        for (BeatMap currentMap : rankedMaps) {
            if (currentMap.md5.equals(mapHash)) {
                map = currentMap;
            }
        }

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/mappagepagecontent.html");

        try {
            content = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        content = content.replace("%ARTIST%", map.artist);
        content = content.replace("%TITLE%", map.title);
        content = content.replace("%CREATOR%", map.creator);
        content = content.replace("%STARRATING%", new DecimalFormat("0.00").format(map.starRating));
        content = content.replace("%ALLDIFFS%",
                "<li><a class=\"active\" href=web/mappage?map=" + map.md5 + ">" + map.diffName + "</a></li>");
        content = content.replace("%LENGTH%", new DecimalFormat("0").format(map.length / 1000));
        content = content.replace("%DRAINTIME%", new DecimalFormat("0").format(map.drainTime / 1000));
        content = content.replace("%BPM%", new DecimalFormat("0").format(map.bpm));

        List<Score> allMapScores = sqlHandler.getMapLeaderboard(mapHash);

        Score topScore = allMapScores.get(0);

        content = content.replace("%NUMBERONENAME%", sqlHandler.checkUserData(topScore.userId).displayUsername);
        content = content.replace("%NUMBERONESCORE%", String.valueOf(topScore.score));
        content = content.replace("%NUMBERONEACCURACY%", new DecimalFormat("0.00").format(topScore.accuracy));
        content = content.replace("%NUMBERONEMAXCOMBO%", String.valueOf(topScore.maxCombo));
        content = content.replace("%NUMBERONE50%", String.valueOf(topScore.hit50));
        content = content.replace("%NUMBERONE100%", String.valueOf(topScore.hit100));
        content = content.replace("%NUMBERONE300%", String.valueOf(topScore.hit300));
        content = content.replace("%NUMBERONEMISS%", String.valueOf(topScore.hitMiss));
        content = content.replace("%NUMBERONEGEKI%", String.valueOf(topScore.hitGeki));
        content = content.replace("%NUMBERONEKATU%", String.valueOf(topScore.hitKatu));
        content = content.replace("%NUMBERONEMODS%", String.valueOf(topScore.mods));

        content = content.replace("%CSSTARS%", createStarPattern((int) Math.round(map.circleSize), 10));
        content = content.replace("%HPSTARS%", createStarPattern((int) Math.round(map.hpDrainRate), 10));
        content = content.replace("%ODSTARS%", createStarPattern((int) Math.round(map.overallDifficulty), 10));

        content = content.replace("%SRSTARS%", createStarPattern((int) Math.round(map.starRating), 5));

        switch (topScore.grade) {
            case 'S':
                if (topScore.accuracy != 100) {
                    content = content.replace("%RANKIMAGE%", sRank.getAsHtml());
                } else {
                    content = content.replace("%RANKIMAGE%", xRank.getAsHtml());
                }
                break;
            case 'A':
                content = content.replace("%RANKIMAGE%", aRank.getAsHtml());
                break;
            case 'B':
                content = content.replace("%RANKIMAGE%", bRank.getAsHtml());
                break;
            case 'C':
                content = content.replace("%RANKIMAGE%", cRank.getAsHtml());
                break;
            case 'D':
                content = content.replace("%RANKIMAGE%", dRank.getAsHtml());
                break;
        }

        if (topScore.perfectCombo) {
            content = content.replace("%ISPERFECT%", "Perfect!");
        } else {
            content = content.replace("%ISPERFECT%", "");
        }

        return createHtmlPage(content);
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

        content += "<link rel=\"stylesheet\" href=\"/web/userpage.css\">";

        Html.Image rankedMap = new Html.Image("/web/selection-ranked.png");
        Html.Image approvedMap = new Html.Image("/web/selection-approved.png");
        Html.Image avatar = new Html.Image("/web/testavatar.png");

        avatar.style = "text-align: left; vertical-align: middle;";

        content += "<div id=\"content\"><table><tr><td>" + avatar.getAsHtml() + "</td>\n";

        content += "<td style=\"vertical-align: middle;\">"
                + Html.header1(thisPlayer.displayUsername + Html.bold(" (#" + thisPlayer.globalRank + ")\n", ""), "");
        content += Html.header2("Ranked Score : " + thisPlayer.rankedScore, "")
                + Html.header3("Overall Accuracy: " + new DecimalFormat("#.00").format(thisPlayer.accuracy) + "%", "")
                + "</td></table></div>";

        // content += "<table><tr><td>";
        // content += "<table border=\"1\"><tr> <th>Status</th> <th>Song</th>
        // <th>Score</th> <th>WP</th> <th>Accuracy</th> <th>Grade</th> </tr>";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/userpagestart.html");

        try {
            content += new String(is.readAllBytes());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        List<Score> scores = sqlHandler.getAllScoresOfUser(userId);

        List<BeatMap> rankedMaps = sqlHandler.getAllRankedMaps();

        int currentRank = 1;

        for (Score score : scores) {
            BeatMap thisMap = null;

            for (BeatMap map : rankedMaps) {
                if (map.md5.equals(score.mapHash)) {
                    thisMap = map;
                }
            }

            if ((currentRank | 1) > currentRank) {
                if (thisMap == null) {
                    content += "<tr padding=\"1\"> <td style=\"text-align: center;\">" + approvedMap.getAsHtml()
                            + "</td> <td>" + score.mapHash + "</td> <td>" + score.score + "</td> <td>" + score.accuracy
                            + "</td> <td>" + score.grade + "</td> </tr>";
                } else {
                    content += "<tr padding=\"1\"> <td style=\"text-align: center;\">" + rankedMap.getAsHtml()
                            + "</td> <td>" + thisMap.artist + " - " + thisMap.title + " (" + thisMap.creator + ") ["
                            + thisMap.diffName + "]" + "</td> <td>" + score.score + "</td> <td>" + score.accuracy
                            + "</td> <td>" + score.grade + "</td> </tr>";
                }
            } else {
                if (thisMap == null) {
                    content += "<tr class=\"odd\" padding=\"1\"> <td style=\"text-align: center;\">"
                            + approvedMap.getAsHtml() + "</td> <td>" + score.mapHash + "</td> <td>" + score.score
                            + "</td> <td>" + score.accuracy + "</td> <td>" + score.grade + "</td> </tr>";
                } else {
                    content += "<tr class=\"odd\" padding=\"1\"> <td style=\"text-align: center;\">"
                            + rankedMap.getAsHtml() + "</td> <td>" + thisMap.artist + " - " + thisMap.title + " ("
                            + thisMap.creator + ") [" + thisMap.diffName + "]" + "</td> <td>" + score.score
                            + "</td> <td>" + score.accuracy + "</td> <td>" + score.grade + "</td> </tr>";
                }
            }

            currentRank++;
        }

        is = App.class.getClassLoader().getResourceAsStream("htmltemplates/userpageend.html");

        try {
            content += new String(is.readAllBytes());
        } catch (IOException e) {
        }

        // content += "</table></table></tr></td>";

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

        int page = 1;

        if (req.queryParams("page") != null) {
            page = Integer.parseInt(req.queryParams("page"));
        }

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

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/rankingsstart.html");

        try {
            content = new String(is.readAllBytes());

            content = content.replace("%CURRENTPAGEMIN%", String.valueOf(((page - 1) * 50) + 1));

            if ((page + 1) * 50 < allPlayers.size()) {
                content = content.replace("%CURRENTPAGEMAX%", String.valueOf(allPlayers.size()));
            } else {
                content = content.replace("%CURRENTPAGEMAX%", String.valueOf((page) * 50));
            }

            content = content.replace("%RANKEDPLAYERCOUNT%", String.valueOf(allPlayers.size()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        int currentRank = 1;

        // Iterates through all the players
        for (int i = 0; i < allPlayers.size(); i++) {
            Player player = allPlayers.get(i);

            player.calculateOverallAccuracy();

            if (player.rankedScore > 0) {
                DecimalFormat scoreFormat = new DecimalFormat("#");
                scoreFormat.setGroupingUsed(true);
                scoreFormat.setGroupingSize(3);

                if ((currentRank | 1) > currentRank) {
                    content += "<tr padding=\"0\"> <td><b>#" + currentRank + "</b></td> <td>" + player.displayUsername
                            + "</td> <td>" + new DecimalFormat("#.00").format(player.accuracy) + "%</td> <td>"
                            + player.playcount + "</td> <td>" + scoreFormat.format(player.totalScore) + "</td> <td><b>"
                            + scoreFormat.format(player.rankedScore) + "</b></td> </tr>";
                } else {
                    content += "<tr class=\"odd\" padding=\"0\"> <td><b>#" + currentRank + "</b></td> <td>"
                            + player.displayUsername + "</td> <td>" + new DecimalFormat("#.00").format(player.accuracy)
                            + "%</td> <td>" + player.playcount + "</td> <td>" + scoreFormat.format(player.totalScore)
                            + "</td> <td><b>" + scoreFormat.format(player.rankedScore) + "</b></td> </tr>";
                }

                currentRank++;
            }
        }

        is = App.class.getClassLoader().getResourceAsStream("htmltemplates/rankingsend.html");

        try {
            content += new String(is.readAllBytes());
        } catch (IOException e) {
        }

        // Returns the string to the client
        return createHtmlPage(content);
    }

    public static String getScores(Request req) {
        String returnString = "";
        String mapHash = req.queryParams("c");

        List<Score> mapScores = sqlHandler.getMapLeaderboard(mapHash);

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
        }

        return "";
    }
}
