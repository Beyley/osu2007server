package poltixe.osu2007;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(80);
        get("/web/osu-login.php", (req, res) -> Handlers.login(req));
        get("/web/osu-getscores.php", (req, res) -> Handlers.getScores(req));
        get("/web/osu-submit.php", (req, res) -> Handlers.submit(req));
        get("/web/osu-getreplay.php", (req, res) -> Handlers.getReplay(req));
        get("/", (req, res) -> "1");
        get("/web", (req, res) -> "1");
    }
}