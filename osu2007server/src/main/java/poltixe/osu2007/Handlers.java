package poltixe.osu2007;

import java.io.Console;

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
        String password = req.queryParams("score");

        System.out.println(req.contentType());
        System.out.println(req.params());

        return "";
    }
}
