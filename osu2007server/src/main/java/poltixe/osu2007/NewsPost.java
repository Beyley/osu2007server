package poltixe.osu2007;

import java.sql.*;

public class NewsPost {
    public Player creator;
    public String title;
    public Date timestamp;
    public String content;

    private MySqlHandler sqlHandler = new MySqlHandler();

    NewsPost(ResultSet rs) {
        try {
            this.creator = sqlHandler.checkUserData(sqlHandler.getUserId(rs.getString(2)));
            this.timestamp = rs.getDate(3);
            this.content = rs.getString(4);
            this.title = rs.getString(5);
        } catch (SQLException e) {
        }
    }
}
