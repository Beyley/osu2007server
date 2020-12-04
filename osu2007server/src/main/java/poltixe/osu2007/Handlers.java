package poltixe.osu2007;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spark.Request;

public class Handlers {
    // Gets a new instance of the MySQL handler
    public static MySqlHandler sqlHandler = new MySqlHandler();

    // Handles a login request
    public static String login(Request req) {
        // The string to be returned to the osu! client, in this case has a default
        // value of "1", to indicate the login was sucsessful
        String returnString = "1";

        // Gets the parameters for the login, in this case the username and the password
        String username = req.queryParams("username");
        String password = req.queryParams("password");

        // Gets the users data of the person the client is attempting to sign in as
        User userData = sqlHandler.checkUserData(username);

        // Checks if the user exists in the database or not
        if (userData.userExists) {
            // Checks if the password is wrong, if so, tell the client that the password was
            // incorrect
            if (!userData.userPassword.equals(password))
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
        String returnString = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><meta name=\"description\" content=\"\"><meta name=\"author\" content=\"\"><title>osu!2007 Private server</title><link href=\"bootstrap/css/bootstrap.css\" rel=\"stylesheet\"><link href=\"starter-template.css\" rel=\"stylesheet\"> <!--[if lt IE 9]> <script src=\"https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js\"></script> <script src=\"https://oss.maxcdn.com/respond/1.4.2/respond.min.js\"></script> <![endif]--></head><body> <nav class=\"navbar navbar-inverse navbar-fixed-top\"><div class=\"container\"><div class=\"navbar-header\"> <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#navbar\" aria-expanded=\"false\" aria-controls=\"navbar\"> <span class=\"sr-only\">Toggle navigation</span> <span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span> </button> <a class=\"navbar-brand\" href=\"#\">Poltixe's osu!2007 Private server</a></div><div id=\"navbar\" class=\"collapse navbar-collapse\"><ul class=\"nav navbar-nav\"><li> <a href=\"/web/\">Home</a></li><li class=\"active\"> <a href=\"/top\">Rankings</a></li><li></li></ul></div></div> </nav><div class=\"container\"><div class=\"starter-template\"><h1>Global Rankings</h1>";

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
                if (score.playerUsername.equals(player.username)) {
                    // Sets the variable to show that the player is in fact inside of the playerlist
                    playerInList = true;
                }
            }

            // Checks if the player is or is not in the list
            if (!playerInList) {
                // If they are not add them to the list with the score of the current score we
                // are iterating on
                allPlayers.add(new Player(score.playerUsername, score.score, 0));
            } else {
                // If they are in the list, iterate through all the known players
                for (int playerI = 0; playerI < allPlayers.size(); playerI++) {
                    // Gets the player we are currently iterating on
                    Player player = allPlayers.get(playerI);
                    // Checks if the username in the score we are currently iterating on matches the
                    // playername of the player we are currently interating on
                    if (score.playerUsername.equals(player.username)) {
                        // Adds the score of the play we are iterating on to the player we are currently
                        // iterating on
                        if (score.score < 12000000) {
                            allPlayers.set(playerI,
                                    new Player(player.username, player.score + score.score, player.amountOfNumberOnes));
                        }
                    }
                }
            }
        }

        for (int i = 0; i < allScores.size(); i++) {
            Score score = allScores.get(i);
            boolean mapInList = false;
            // Loops through all players
            for (BeatMap map : allMaps) {
                // Checks if the current player we are iterating on is equal to the playername
                // in the score
                if (score.osuFileHash.equals(map.hash)) {
                    // Sets the variable to show that the player is in fact inside of the playerlist
                    mapInList = true;
                }
            }

            if (mapInList) {
                for (int mapI = 0; mapI < allMaps.size(); mapI++) {
                    BeatMap map = allMaps.get(mapI);

                    if (map.topScore.score < score.score && map.hash.equals(score.osuFileHash)) {
                        BeatMap oldTop = allMaps.get(mapI);
                        allMaps.set(mapI, new BeatMap(map.hash, score));

                        for (int playerI = 0; playerI < allPlayers.size(); playerI++) {
                            Player player = allPlayers.get(playerI);
                            if (score.playerUsername.equals(player.username)) {
                                allPlayers.set(playerI,
                                        new Player(player.username, player.score, player.amountOfNumberOnes + 1));
                            }

                            if (oldTop.topScore.playerUsername.equals(player.username)) {
                                allPlayers.set(playerI,
                                        new Player(player.username, player.score, player.amountOfNumberOnes - 1));
                            }
                        }
                    }
                }
            } else {
                allMaps.add(new BeatMap(score.osuFileHash, score));
                for (int playerI = 0; playerI < allPlayers.size(); playerI++) {
                    Player player = allPlayers.get(playerI);
                    if (score.playerUsername.equals(player.username)) {
                        allPlayers.set(playerI,
                                new Player(player.username, player.score, player.amountOfNumberOnes + 1));
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
            returnString += "<p class\"lead\"> #" + (i + 1) + " : " + player.username + ", Total Ranked Score : "
                    + player.score + ", " + player.amountOfNumberOnes + " #1's</p>";
        }

        returnString += "</div></div> <script src=\"assets/js/jquery.min.js\"></script> <script src=\"bootstrap/js/bootstrap.min.js\"></script> <script src=\"assets/js/ie10-viewport-bug-workaround.js\"></script> </body></html>";

        // Returns the string to the client
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
