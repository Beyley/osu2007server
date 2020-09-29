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
        String url = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";
        String user = App.mySqlUser;
        String password = App.mySqlPass;

        String query = "SELECT VERSION()";

        try (Connection con = (Connection) DriverManager.getConnection(url, user, password);
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
}
