package poltixe.osu2007;

import java.io.Console;
import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;

import spark.Request;

public class Handlers {
    public static String login(Request req) {
        String returnString = "1";

        String username = req.queryParams("username");
        String password = req.queryParams("password");

        return returnString;
    }

    public static String getScores(Request req) {
        String returnString = "0:Eevee:500000:600:0:15:575:0:0:0:True:0\n0:NotEevee:500000:600:0:15:575:0:0:0:True:0";

        String mapId = req.queryParams("c");

        return returnString;
    }

    public static String getReplay(Request req) {
        String returnString = "INSERT-REPLAY-FILE";

        String scoreId = req.queryParams("c");

        return returnString;
    }

    public static String submit(Request req) {
        String scoreDetails = req.queryParams("score");
        String password = req.queryParams("pass");

        System.out.println(scoreDetails);

        System.out.println(req.contentType()); // What type of data am I sending?
        System.out.println(req.params()); // What are the params sent?
        System.out.println("old : \n" + req.body());

        String newBody = req.body();

        String[] splitNewBody = newBody.split("");

        for (int i = 135; i >= 0; i--)
            splitNewBody[i] = "";

        newBody = "";
        for (int i = 0; i < splitNewBody.length; i++)
            newBody = newBody + splitNewBody[i];

        System.out.println("new : \n" + newBody);

        return "";
    }
}
