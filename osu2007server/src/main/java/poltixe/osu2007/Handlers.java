package poltixe.osu2007;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
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

    public static String getNameChangePage(Request req) {
        String returnString = "";

        returnString += "<form action=\"/web/namechange\"> <label for=\"oldusername\">Old username:</label> <input type=\"text\" id=\"oldusername\" name=\"oldusername\"><br><br> <label for=\"newusername\">New username:</label><input type=\"text\" id=\"newusername\" name=\"newusername\"><br><br> <label for=\"password\">Password:</label><input type=\"password\" id=\"password\" name=\"password\"><br><br> <input type=\"submit\" value=\"Submit\"> </form>";

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
                returnString += "<br> Username changed!";
                return returnString;
            } else {
                returnString += "<br> That password is incorrect!";
                return returnString;
            }
        }
        // }

        return returnString;
    }

    public static String getUserPage(Request req) {
        String returnString = "";

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

        returnString += thisPlayer.displayUsername + " (#" + thisPlayer.globalRank + ")<br>";
        returnString += "Ranked Score : " + thisPlayer.rankedScore + "<br>";

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
                returnString += "<br>" + score.mapHash + " : " + score.score + " : " + score.grade + "<br>";
            } else {
                returnString += "<br>" + thisMap.artist + " - " + thisMap.songName + " (" + thisMap.creator + ") ["
                        + thisMap.diffName + "] : " + score.score + " : " + score.grade + "<br>";
            }
        }

        return returnString;
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
        String returnString = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><meta name=\"description\" content=\"\"><meta name=\"author\" content=\"\"><title>osu!2007 Private server</title><link href=\"bootstrap/css/bootstrap.css\" rel=\"stylesheet\"><link href=\"starter-template.css\" rel=\"stylesheet\"> <!--[if lt IE 9]> <script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script> <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script> <![endif]--></head><body> <nav class=\"navbar navbar-inverse navbar-fixed-top\"><div class=\"container\"><div class=\"navbar-header\"> <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#navbar\" aria-expanded=\"false\" aria-controls=\"navbar\"> <span class=\"sr-only\">Toggle navigation</span> <span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span> </button> <a class=\"navbar-brand\" href=\"#\">Poltixe's osu!2007 Private server</a></div><div id=\"navbar\" class=\"collapse navbar-collapse\"><ul class=\"nav navbar-nav\"><li> <a href=\"/web/\">Home</a></li><li class=\"active\"> <a href=\"/web/top\">Rankings</a></li><li></li></ul></div></div> </nav><div class=\"container\"><div class=\"starter-template\"><h1>Global Rankings</h1>";

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
            // Adds the player info to the string to be sent to the client
            // <p class="lead">ranking go brrrrrrrr</p>
            returnString += "<p class\"lead\"> #" + (i + 1) + " : " + player.displayUsername + ", Total Ranked Score : "
                    + player.rankedScore + ", " + player.amountOfNumberOnes + " #1's</p>";
        }

        returnString += "</div></div> <script src=\"assets/js/jquery.min.js\"></script> <script src=\"bootstrap/js/bootstrap.min.js\"></script> <script src=\"assets/js/ie10-viewport-bug-workaround.js\"></script> </body></html>";

        // Returns the string to the client
        return returnString;
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
