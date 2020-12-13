package poltixe.osu2007;

import static spark.Spark.*;

import java.util.*;

public class App {
    // Global MySQL settings
    public static String mySqlServer;
    public static String mySqlPort;
    public static String mySqlUser;
    public static String mySqlPass;

    public static String httpPort;

    public static List<String> knownNames = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        // Gets a new properties value
        GetPropertyValues properties = new GetPropertyValues();
        // Gets the properties file
        properties.getPropValues();

        MySqlHandler sqlHandler = new MySqlHandler();

        // Gets the MySQL version, and if something is wrong, print an error
        System.out.println("MySQL Server version : " + sqlHandler.getVersion());
        sqlHandler.checkForTables();

        FileHandler.rankedDatabaseCheck();

        for (int i = 0; i < sqlHandler.getAllPlayers().size() + 100; i++) {
            knownNames.add(null);
        }

        staticFiles.location("/statichtml"); // Static files

        // Sets the webserver port
        port(Integer.parseInt(httpPort));

        // Registers the user site requests
        get("/web/", (req, res) -> WebHandlers.newsPage(req));
        get("/web/about", (req, res) -> WebHandlers.aboutPage(req));
        get("/web/changelog", (req, res) -> WebHandlers.changelogPage(req));
        get("/web/download", (req, res) -> WebHandlers.downloadPage(req));
        get("/web/faq", (req, res) -> WebHandlers.faqPage(req));
        get("/web/maplisting", (req, res) -> WebHandlers.maplistingPage(req));
        get("/web/mappage", (req, res) -> WebHandlers.mapPage(req));
        get("/web/top", (req, res) -> WebHandlers.getTopPlayers(req));
        get("/web/u", (req, res) -> WebHandlers.getUserPage(req));
        get("/web/namechange", (req, res) -> WebHandlers.getNameChangePage(req));

        // Regsiters the game requests
        get("/web/osu-login.php", (req, res) -> WebHandlers.login(req));
        get("/web/osu-getscores.php", (req, res) -> WebHandlers.getScores(req));
        post("/web/osu-submit.php", (req, res) -> WebHandlers.submit(req));
        get("/web/osu-getreplay.php", (req, res) -> WebHandlers.getReplay(req));
    }
}