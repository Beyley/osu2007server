package poltixe.osu2007;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

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

            boolean rs = st.execute(query);
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

            boolean rs = st.execute(query);
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

            boolean rs = st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public User checkForUser(String usersName) {
        String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT * FROM users";

        boolean userExist = false;

        try (Connection con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
                Statement st = (Statement) con.createStatement();
                ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                if (rs.getString(2).equals(usersName)) {
                    userExist = true;
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return new User(usersName, userExist);
    }
}