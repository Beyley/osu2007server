package poltixe.osu2007;

import static spark.Spark.*;

import java.io.*;
import java.util.*;

public class App {
    // Global MySQL settings
    public static String mySqlServer;
    public static String mySqlPort;
    public static String mySqlUser;
    public static String mySqlPass;

    public static String httpPort;

    public static List<String> knownNames = new ArrayList<String>();

    // Creates a new MySqlHandler
    public static MySqlHandler sqlHandler = new MySqlHandler();

    public static void main(String[] args) throws IOException {

        // Gets a new properties value
        GetPropertyValues properties = new GetPropertyValues();
        // Gets the properties file
        properties.getPropValues();

        // Gets the MySQL version, and if something is wrong, print an error
        System.out.println("MySQL Server version : " + sqlHandler.getVersion());
        // Sets up the databases and tables
        sqlHandler.checkForDatabase();
        sqlHandler.checkForTables();

        for (int i = 0; i < sqlHandler.getAllPlayers().size() + 1; i++) {
            knownNames.add(null);
        }

        staticFiles.location("/statichtml"); // Static files

        // Sets the webserver port
        port(Integer.parseInt(httpPort));
        // Registers the requests
        get("/web/osu-login.php", (req, res) -> Handlers.login(req));
        get("/web/osu-getscores.php", (req, res) -> Handlers.getScores(req));
        post("/web/osu-submit.php", (req, res) -> Handlers.submit(req));
        get("/web/osu-getreplay.php", (req, res) -> Handlers.getReplay(req));
        // get("/", (req, res) -> Handlers.getTopPlayers(req));
        get("/web/top", (req, res) -> Handlers.getTopPlayers(req));
        get("/web/top/", (req, res) -> Handlers.getTopPlayers(req));
        get("/web/u", (req, res) -> Handlers.getUserPage(req));
        get("/web/u/", (req, res) -> Handlers.getUserPage(req));
    }
}