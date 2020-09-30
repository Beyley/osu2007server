package poltixe.osu2007;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
        String returnString = "0:PoltixeTheDerg:766124:200:2:25:167:0:17:24:False:0\n";
        // a5b99395a42bd55bc5eb1d2411cbdf8b:PoltixeTheDerg:f07e856520cefc0b8b0c5cfe1b619e8e:167:25:2:24:17:0:766124:200:False:A:0:True
        String mapId = req.queryParams("c");

        return returnString;
    }

    public static byte[] getReplay(Request req) {
        byte[] returnString = {};

        try (FileInputStream fos = new FileInputStream("testreplay")) {
            // returnString = new String(fos.readAllBytes());
            returnString = fos.readAllBytes();
        } catch (IOException e) {
        }

        String scoreId = req.queryParams("c");

        return returnString;
    }

    public static String submit(Request req) {
        String scoreDetails = req.queryParams("score");
        String password = req.queryParams("pass");

        System.out.println(scoreDetails);

        byte[] rawBodyBytes = req.bodyAsBytes();

        Score score = new Score(scoreDetails, 0);

        // System.out.println(String.valueOf(rawBodyBytes));

        return "";
    }
}
