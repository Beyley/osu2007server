package poltixe.osu2007;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.mysql.cj.jdbc.DatabaseMetaData;

import java.util.ArrayList;

public class MySqlHandler {
    public String getVersion() {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT VERSION()";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {

                return rs.getString(1);
            }

        } catch (SQLException ex) {
            return ex.getMessage();
        }

        return "Unknown Version";
    }

    public void checkForDatabase() {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "CREATE DATABASE osu2007";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void checkForTables() {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "";

        boolean scoreListExist = false;

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

            ResultSet rs1 = md.getTables(null, null, "score_list", null);

            while (rs1.next()) {
                scoreListExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        if (!scoreListExist) {
            query = "CREATE TABLE `osu2007`.`score_list` ( `id` INT NOT NULL AUTO_INCREMENT, `maphash` VARCHAR(100) NOT NULL, `userid` INT NOT NULL, `replayhash` VARCHAR(100) NOT NULL, `hit300` INT NOT NULL, `hit100` INT NOT NULL, `hit50` INT NOT NULL, `hitgeki` INT NOT NULL, `hitkatu` INT NOT NULL, `hitmiss` INT NOT NULL, `score` INT NOT NULL, `maxcombo` INT NOT NULL, `perfect` VARCHAR(5) NOT NULL, `grade` VARCHAR(1) NOT NULL, `mods` INT NOT NULL, `pass` VARCHAR(5) NOT NULL, PRIMARY KEY (`id`));";

            try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                    Statement st = (Statement) con.createStatement()) {

                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        boolean oldScoreListExist = false;

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

            ResultSet rs1 = md.getTables(null, null, "scores", null);

            while (rs1.next()) {
                oldScoreListExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        if (oldScoreListExist) {
            // CONVERT DATABASE TO NEW FORMAT

            // Rename users table
            query = "ALTER TABLE `osu2007`.`users` RENAME TO  `osu2007`.`osu_users`;";

            try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                    Statement st = (Statement) con.createStatement()) {

                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            query = "SELECT * FROM scores";

            List<Score> scores = new ArrayList<Score>();

            try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                    Statement st = (Statement) con.createStatement();
                    ResultSet rs = st.executeQuery(query)) {

                while (rs.next()) {
                    Score currentScore = new Score(rs.getString(2), rs.getInt(1));

                    scores.add(currentScore);
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            for (Score score : scores) {
                query = "INSERT INTO score_list(id, maphash, userid, replayhash, hit300, hit100, hit50, hitgeki, hitkatu, hitmiss, score, maxcombo, perfect, grade, mods, pass) VALUES('"
                        + score.scoreId + "', '" + score.mapHash + "', '" + score.userId + "', '" + score.replayHash
                        + "', '" + score.hit300Count + "', '" + score.hit100Count + "', '" + score.hit50Count + "', '"
                        + score.hitGekiCount + "', '" + score.hitKatuCount + "', '" + score.hitMissCount + "', '"
                        + score.score + "', '" + score.maxCombo + "', '" + score.perfectCombo + "', '" + score.grade
                        + "', '" + score.mods + "', '" + score.pass + "');";

                try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                        Statement st = (Statement) con.createStatement()) {

                    st.execute(query);
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            query = "DROP TABLE `osu2007`.`scores`;";

            try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                    Statement st = (Statement) con.createStatement()) {

                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        boolean usersExist = false;

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

            ResultSet rs1 = md.getTables(null, null, "osu_users", null);

            while (rs1.next()) {
                usersExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        if (!usersExist) {
            query = "CREATE TABLE `osu2007`.`osu_users` (`id` INT NOT NULL AUTO_INCREMENT, `username` VARCHAR(50) NULL, `password` VARCHAR(500) NULL, PRIMARY KEY (`id`));";

            try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                    Statement st = (Statement) con.createStatement()) {

                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public int getRankedScoreOfUser(int userId) {
        int score = 0;

        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM score_list WHERE userid = '" + userId + "'";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Score currentScore = new Score(rs);

                if (currentScore.score < 12000000)
                    score += currentScore.score;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return score;
    }

    public List<Score> getAllScoresOfUser(int userId) {
        List<Score> scores = new ArrayList<Score>();

        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM score_list WHERE userid = '" + userId + "'";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Score currentScore = new Score(rs);

                scores.add(currentScore);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return scores;
    }

    public void addUser(String newUsersName, String newUsersPassword) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "INSERT INTO osu_users(username, password) VALUES('" + newUsersName + "', '" + newUsersPassword
                + "');";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void removeScore(Score score) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "DELETE FROM score_list WHERE id='" + score.scoreId + "';";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updateScore(int oldId, Score newScore, byte[] replayData) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "UPDATE score_list SET replayhash='" + newScore.replayHash + "', hit300='" + newScore.hit300Count
                + "', hit100='" + newScore.hit100Count + "', hit50='" + newScore.hit50Count + "', hitgeki='"
                + newScore.hitGekiCount + "', hitkatu='" + newScore.hitKatuCount + "', hitmiss='"
                + newScore.hitMissCount + "', score='" + newScore.score + "', maxcombo='" + newScore.maxCombo
                + "', perfect='" + newScore.perfectCombo + "', grade='" + newScore.grade + "', mods='" + newScore.mods
                + "', pass='" + newScore.mods + "' WHERE id='" + newScore.scoreId + "';";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        FileHandler.saveReplayToFile(newScore, replayData);
    }

    public int getUserId(String userName) {
        int userId = -1;

        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM osu_users";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                if (rs.getString(2).equals(userName)) {
                    userId = rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return userId;
    }

    public String getUsername(int userId) {
        String username = "";

        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM osu_users";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                if (rs.getInt(1) == userId) {
                    username = rs.getString(2);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return username;
    }

    public void addScore(Score score, byte[] replayData) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "INSERT INTO score_list(maphash, userid, replayhash, hit300, hit100, hit50, hitgeki, hitkatu, hitmiss, score, maxcombo, perfect, grade, mods, pass) VALUES('"
                + score.mapHash + "', '" + score.userId + "', '" + score.replayHash + "', '" + score.hit300Count
                + "', '" + score.hit100Count + "', '" + score.hit50Count + "', '" + score.hitGekiCount + "', '"
                + score.hitKatuCount + "', '" + score.hitMissCount + "', '" + score.score + "', '" + score.maxCombo
                + "', '" + score.perfectCombo + "', '" + score.grade + "', '" + score.mods + "', '" + score.pass
                + "');";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        query = "SELECT * FROM score_list";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Score currentScore = new Score(rs);

                if (currentScore.replayHash.equals(score.replayHash)) {
                    FileHandler.saveReplayToFile(currentScore, replayData);
                }
            }

            // FileHandler.saveReplayToFile(score, replayData);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<Score> getMapLeaderboard(String mapHash) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM score_list WHERE maphash = '" + mapHash + "'";

        List<Score> scores = new ArrayList<Score>();

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Score currentScore = new Score(rs);

                scores.add(currentScore);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return scores;
    }

    public List<Score> getAllScores() {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM score_list";

        List<Score> scores = new ArrayList<Score>();

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Score currentScore = new Score(rs);

                scores.add(currentScore);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return scores;
    }

    public Player checkUserData(int userId) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM osu_users";

        boolean userExist = false;
        String userPassword = "";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                if (rs.getInt(1) == userId) {
                    userExist = true;
                    userPassword = rs.getString(3);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return new Player(userId, userPassword, userExist);
    }

    public int getGlobalRankOfUser(int userId) {
        List<Score> allScores = getAllScores();

        List<Player> allPlayers = new ArrayList<Player>();

        List<BeatMap> allMaps = new ArrayList<BeatMap>();

        Player thisPlayer = new Player(userId, true);

        for (Score score : allScores) {
            boolean playerInTable = false;

            for (Player player : allPlayers) {
                if (player.userId == score.userId) {
                    playerInTable = true;
                }
            }

            if (!playerInTable) {
                allPlayers.add(new Player(userId, true));
            }

            boolean mapInList = false;

            for (BeatMap map : allMaps) {
                if (score.mapHash.equals(map.md5Hash)) {
                    mapInList = true;
                }
            }

            if (!mapInList) {
                allMaps.add(new BeatMap(score.mapHash, null));
            }

            for (Player player : allPlayers) {
                if (score.userId == player.userId) {
                    thisPlayer = player;
                    player.rankedScore += score.score;
                }
            }
        }

        allPlayers.sort(new ScoreSorter());

        return allPlayers.indexOf(thisPlayer) + 1;
    }
}