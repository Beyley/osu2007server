package poltixe.osu2007;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
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

        String query = "CREATE TABLE `osu2007`.`users` (`id` INT NOT NULL AUTO_INCREMENT, `username` VARCHAR(50) NULL, `password` VARCHAR(500) NULL, PRIMARY KEY (`id`));";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        query = "CREATE TABLE `osu2007`.`scores` (`id` INT NOT NULL AUTO_INCREMENT, `scoredata` VARCHAR(200) NULL, PRIMARY KEY (`id`));";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void addUser(String newUsersName, String newUsersPassword) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "INSERT INTO users(username, password) VALUES('" + newUsersName + "', '" + newUsersPassword
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

        String query = "DELETE FROM scores WHERE id='" + score.scoreId + "';";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void addScore(Score score, byte[] replayData) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "INSERT INTO scores(scoredata) VALUES('" + score.asSubmitString() + "');";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        query = "SELECT * FROM scores";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Score currentScore = new Score(rs.getString(2), rs.getInt(1));

                if (currentScore.asSubmitString().equals(score.asSubmitString())) {
                    FileHandler.saveReplayToFile(currentScore, replayData);
                }
            }

            // FileHandler.saveReplayToFile(score, replayData);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<Score> getAllMapScores(String mapHash) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM scores";

        List<Score> scores = new ArrayList<Score>();

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Score currentScore = new Score(rs.getString(2), rs.getInt(1));

                if (currentScore.osuFileHash.equals(mapHash)) {
                    scores.add(currentScore);
                }
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

        String query = "SELECT * FROM scores";

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

        return scores;
    }

    public User checkUserData(String usersName) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM users";

        boolean userExist = false;
        String userPassword = "";

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                if (rs.getString(2).equals(usersName)) {
                    userExist = true;
                    userPassword = rs.getString(3);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return new User(usersName, userPassword, userExist);
    }
}