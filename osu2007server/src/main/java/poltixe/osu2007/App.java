package poltixe.osu2007;

import static spark.Spark.*;

import java.io.*;

public class App {
    public static String mySqlServer;
    public static String mySqlPort;
    public static String mySqlUser;
    public static String mySqlPass;

    public static MySqlHandler sqlHandler = new MySqlHandler();

    public static void main(String[] args) throws IOException {
        GetPropertyValues properties = new GetPropertyValues();
        properties.getPropValues();

        System.out.println("MySQL Server version : " + sqlHandler.getVersion());
        sqlHandler.checkForDatabase();
        sqlHandler.checkForTables();

        port(80);
        get("/web/osu-login.php", (req, res) -> Handlers.login(req));
        get("/web/osu-getscores.php", (req, res) -> Handlers.getScores(req));
        post("/web/osu-submit.php", (req, res) -> Handlers.submit(req));
        get("/web/osu-getreplay.php", (req, res) -> Handlers.getReplay(req));
        get("/", (req, res) -> "Home page or whatever");
        get("/web", (req, res) -> "why are you here?");
    }
}