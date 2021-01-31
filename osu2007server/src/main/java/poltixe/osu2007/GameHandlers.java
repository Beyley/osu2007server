package poltixe.osu2007;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

import poltixe.osu2007.HandlerFunctions.*;
import poltixe.osu2007.clientpackets.SendMessagePacket;
import poltixe.osu2007.serverpackets.RecieveChatMessagePacket;
import spark.Request;

public class GameHandlers {
    // Gets a new instance of the MySQL handler
    public MySqlHandler sqlHandler = null;

    Random r = new Random();

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
        Date today = Calendar.getInstance().getTime();
        long currentEpochTime = today.getTime();

        List<Player> tempOnlinePlayers = new ArrayList<Player>(App.onlinePlayers);
        for (Player player : tempOnlinePlayers)
            if (currentEpochTime - player.lastPing > 15000) {
                System.out.println(player.username + " logged off!");
                App.onlinePlayers.remove(player);
            }

        // #region CHECK IF PROPER USERNAME
        // Gets the parameters for the login, in this case the username and the password
        String username = req.queryParams("username");
        String password = req.queryParams("password");

        if (username == null || password == null)
            return "0";

        // #region GENERATE TOKEN
        long randomNumber = r.nextInt(1000000000);
        String randomSentence = RandomHelper.getRandomEsperantoWords();

        String returnString = username + " estas " + randomSentence + String.valueOf(randomNumber);
        // #endregion

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
        // #endregion

        // #region CHECK IF PASSWORD CORRECT AND THIS IS FIRST ACCOUNT
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
        // #endregion

        // #region ADD USER TO ONLINE USERS
        Player tempPlayer = new Player(sqlHandler.getUserId(username));

        tempPlayer.lastPing = currentEpochTime;
        tempPlayer.token = returnString;

        tempOnlinePlayers = new ArrayList<Player>(App.onlinePlayers);
        for (Player player : tempOnlinePlayers) {
            if (player.username.equals(username)) {
                int id = App.onlinePlayers.indexOf(player);

                App.onlinePlayers.remove(id);
            }
        }

        App.onlinePlayers.add(tempPlayer);

        List<MessageToSend> tempOnlineChat = new ArrayList<MessageToSend>(App.onlineChat);
        for (MessageToSend message : tempOnlineChat) {
            MessageToSend tempMessage = message;

            tempMessage.alreadySentTo.add(App.onlinePlayers.get(App.onlinePlayers.indexOf(tempPlayer)));

            App.onlineChat.set(tempOnlineChat.indexOf(tempMessage), tempMessage);
        }
        // #endregion

        // Returns the string to be sent to the client
        return returnString;
    }

    @Path(path = "/osu-getreplay.php")
    public byte[] getReplay(Request req) {
        byte[] returnData = {};

        String scoreId = req.queryParams("c");

        try (FileInputStream fos = new FileInputStream("replays/" + scoreId + ".osr")) {
            returnData = fos.readAllBytes();
        } catch (IOException e) {
        }

        return returnData;
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

    @Path(path = "/osu-getuserinfo.php")
    public String getUserInfo(Request req) {
        StringBuilder returnData = new StringBuilder();
        String username = req.queryParams("username");

        Player userInfo = sqlHandler.checkUserData(sqlHandler.getUserId(username));

        DecimalFormat df = new DecimalFormat("#.00");

        if (userInfo.userExists) {
            userInfo.calculateUserRank();
            userInfo.calculateOverallAccuracy();

            returnData.append(userInfo.username);
            returnData.append("|");
            returnData.append(userInfo.globalRank);
            returnData.append("|");
            returnData.append(userInfo.playcount);
            returnData.append("|");
            returnData.append(userInfo.rankedScore);
            returnData.append("|");
            returnData.append(df.format(userInfo.accuracy));
            returnData.append("|");
            returnData.append(userInfo.userId);
        }

        return returnData.toString();
    }

    @Path(path = "/osu-getonlineusers.php")
    public String getOnlineUsers(Request req) {
        StringBuilder returnData = new StringBuilder();

        DecimalFormat df = new DecimalFormat("#.00");

        List<Player> tempOnlinePlayers = new ArrayList<Player>(App.onlinePlayers);
        for (Player player : tempOnlinePlayers) {
            player.calculateOverallAccuracy();
            player.calculateUserRank();

            returnData.append(player.username);
            returnData.append("|");
            returnData.append(player.globalRank);
            returnData.append("|");
            returnData.append(player.playcount);
            returnData.append("|");
            returnData.append(player.rankedScore);
            returnData.append("|");
            returnData.append(df.format(player.accuracy));
            returnData.append("|");
            returnData.append(player.userId);
            returnData.append("\n");
        }

        if (returnData.length() > 0)
            returnData.deleteCharAt(returnData.lastIndexOf("\n"));

        return returnData.toString();
    }

    @Path(path = "/osu-onlineuserping.php")
    public String onlineUserPing(Request req) {
        StringBuilder returnData = new StringBuilder();

        String token = req.queryParams("token");

        Date today = Calendar.getInstance().getTime();
        long currentEpochTime = today.getTime();

        List<Player> tempOnlinePlayers = new ArrayList<Player>(App.onlinePlayers);
        for (Player player : tempOnlinePlayers)
            if (currentEpochTime - player.lastPing > 15000) {
                System.out.println(player.username + " logged off!");
                App.onlinePlayers.remove(player);
            }

        if (token == null)
            return "NO TOKEN PROVIDED";

        Player thisPlayer = null;

        tempOnlinePlayers = new ArrayList<Player>(App.onlinePlayers);
        for (Player player : tempOnlinePlayers) {
            if (player.token.equals(token)) {
                thisPlayer = player;

                thisPlayer.lastPing = currentEpochTime;

                App.onlinePlayers.set(tempOnlinePlayers.indexOf(player), thisPlayer);

                break;
            }
        }

        if (thisPlayer == null)
            return "PLAYER NOT FOUND!";

        // #region PARSE CLIENT PACKETS
        List<BasicPacket> requestPackets = BasicPacket.parseRequest(req.headers("packet-data"));

        for (BasicPacket packet : requestPackets) {
            switch (packet.packetId) {
                case ClientPackets.sendMessage: {
                    SendMessagePacket parsedPacket = new SendMessagePacket(packet.data, thisPlayer);

                    MessageToSend tempMessage = new MessageToSend(parsedPacket);

                    tempMessage.alreadySentTo.add(thisPlayer);

                    App.onlineChat.add(tempMessage);

                    System.out
                            .println("IRC: " + tempMessage.packet.sender.username + " : " + tempMessage.packet.message);
                }
            }
        }
        // #endregion

        // #region SEND NEW MESSAGES TO CLIENT
        List<MessageToSend> tempOnlineChat = new ArrayList<MessageToSend>(App.onlineChat);

        for (MessageToSend message : tempOnlineChat) {
            if (message.alreadySentTo.contains(thisPlayer)) {
                continue;
            } else {
                returnData.append(
                        new RecieveChatMessagePacket(message.packet.message, message.packet.sender).getFinalPacket()
                                + "\n");

                message.alreadySentTo.add(thisPlayer);
            }
        }

        if (returnData.length() > 0)
            returnData.deleteCharAt(returnData.lastIndexOf("\n"));
        // #endregion

        return returnData.toString();
    }
}
