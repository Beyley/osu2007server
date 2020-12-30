package poltixe.osu2007;

import static spark.Spark.*;

import java.lang.reflect.*;
import java.util.*;

import poltixe.osu2007.HandlerFunctions.Path;

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

        // Sets amount of threads for the webserver to use
        int maxThreads = 8;
        int minThreads = 2;
        int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);

        GameHandlers gameHandlers = new GameHandlers();
        Method[] gameHandlersMethods = gameHandlers.getClass().getMethods();
        SiteHandlers siteHandlers = new SiteHandlers();
        Method[] siteHandlersMethods = siteHandlers.getClass().getMethods();

        path("/web", () -> {
            // Registers the user site requests
            for (Method method : siteHandlersMethods) {
                Path annos = method.getAnnotation(Path.class);
                if (annos != null) {
                    try {
                        switch (annos.verb()) {
                            case "get":
                                get(annos.path(), (req, res) -> method.invoke(siteHandlers, req));
                                break;
                            case "post":
                                post(annos.path(), (req, res) -> method.invoke(siteHandlers, req));
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        path("/web", () -> {
            // Regsiters the game requests
            for (Method method : gameHandlersMethods) {
                Path annos = method.getAnnotation(Path.class);

                if (annos != null) {
                    try {
                        switch (annos.verb()) {
                            case "get":
                                get(annos.path(), (req, res) -> method.invoke(gameHandlers, req));
                                break;
                            case "post":
                                post(annos.path(), (req, res) -> method.invoke(gameHandlers, req));
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}