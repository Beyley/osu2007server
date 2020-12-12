package poltixe.osu2007;

import java.sql.*;
import java.text.ParseException;
import java.util.List;

import com.mysql.cj.jdbc.DatabaseMetaData;

import java.util.ArrayList;

public class MySqlHandler {
    private String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort + "/osu2007?useSSL=false";

    private String user = App.mySqlUser;
    private String password = App.mySqlPass;

    private Connection con = null;

    public MySqlHandler() {
        try {
            con = (Connection) DriverManager.getConnection(connectionUrl, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getVersion() {

        String query = "SELECT VERSION()";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {

                return rs.getString(1);
            }

        } catch (SQLException ex) {
            return ex.getMessage();
        }

        return "Unknown Version";
    }

    public void checkForDatabase() {
        String query = "CREATE DATABASE osu2007";

        try (Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void addRankedMapsToTable(List<BeatMap> rankedMaps) {
        String query = "CREATE TABLE `osu2007`.`ranked_maps` ( `id` INT NOT NULL AUTO_INCREMENT, `md5` VARCHAR(100) NULL, `starrating` DOUBLE NOT NULL, `artist` VARCHAR(250) NULL, `songname` VARCHAR(250) NULL, `diffname` VARCHAR(250) NULL, `creator` VARCHAR(250) NULL, PRIMARY KEY (`id`));";

        try (Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        query = "";

        for (BeatMap map : rankedMaps) {
            if (map.artist.equals("")) {
                map.artist = "none";
            }

            query = "INSERT INTO `osu2007`.`ranked_maps` (`md5`, `starrating`, `artist`, `songname`, `diffname`, `creator`) VALUES (?, ?, ?, ?, ?, ?);";

            try {
                PreparedStatement stmt = con.prepareStatement(query);

                stmt.setString(1, map.md5);
                stmt.setDouble(2, map.starRating);
                stmt.setString(3, map.artist);
                stmt.setString(4, map.songName);
                stmt.setString(5, map.diffName);
                stmt.setString(6, map.creator);

                stmt.execute();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void checkForRankedTable() {

        boolean rankedTableExist = false;

        try {
            DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

            ResultSet rs1 = md.getTables(null, null, "ranked_maps", null);

            while (rs1.next()) {
                rankedTableExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        if (rankedTableExist) {
            String query = "DROP TABLE `osu2007`.`ranked_maps`";

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void checkForTables() {
        String query = "";

        boolean scoreListExist = false;

        try {
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

            try (Statement st = (Statement) con.createStatement()) {

                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        boolean oldScoreListExist = false;

        try {

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
            query = "ALTER TABLE `osu2007`.`users` RENAME TO `osu2007`.`osu_users`;";

            try (Statement st = (Statement) con.createStatement()) {

                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            query = "SELECT * FROM scores";

            List<Score> scores = new ArrayList<Score>();

            try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

                while (rs.next()) {
                    Score currentScore = new Score(rs.getString(2), rs.getInt(1));

                    scores.add(currentScore);
                }
            } catch (SQLException | ParseException ex) {
                System.out.println(ex.getMessage());
            }

            PreparedStatement stmt = null;

            for (Score score : scores) {
                query = "INSERT INTO score_list(id, maphash, userid, replayhash, hit300, hit100, hit50, hitgeki, hitkatu, hitmiss, score, maxcombo, perfect, grade, mods, pass) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try {
                    stmt = con.prepareStatement(query);
                    stmt.setInt(1, score.scoreId);
                    stmt.setString(2, score.mapHash);
                    stmt.setInt(3, score.userId);
                    stmt.setString(4, score.replayHash);
                    stmt.setInt(5, score.hit300Count);
                    stmt.setInt(6, score.hit100Count);
                    stmt.setInt(7, score.hit50Count);
                    stmt.setInt(8, score.hitGekiCount);
                    stmt.setInt(9, score.hitKatuCount);
                    stmt.setInt(10, score.hitMissCount);
                    stmt.setInt(11, score.score);
                    stmt.setInt(12, score.maxCombo);
                    stmt.setBoolean(13, score.perfectCombo);
                    stmt.setString(14, Character.toString(score.grade));
                    stmt.setInt(15, score.mods);
                    stmt.setBoolean(16, score.pass);

                    stmt.execute();

                    stmt.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            query = "DROP TABLE `osu2007`.`scores`;";

            try (Statement st = (Statement) con.createStatement()) {

                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        boolean usersExist = false;

        try {
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

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        boolean newsExist = false;

        try {
            DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

            ResultSet rs1 = md.getTables(null, null, "news_posts", null);

            while (rs1.next()) {
                newsExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        if (!newsExist) {
            query = "CREATE TABLE `osu2007`.`news_posts` (`id` INT NOT NULL AUTO_INCREMENT, `creator` VARCHAR(100) NULL, `time` DATETIME NULL DEFAULT current_timestamp, `content` LONGTEXT NULL, `title` LONGTEXT NULL, PRIMARY KEY (`id`));";

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            query = "INSERT INTO `osu2007`.`news_posts` (`title`, `creator`, `content`) VALUES ('Test Post', 'PoltixeTheDerg', 'This is an example post!');";

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        boolean playcountColumnExist = false;

        query = "SHOW COLUMNS FROM `osu_users` LIKE 'playcount';";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                playcountColumnExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        if (!playcountColumnExist) {
            query = "ALTER TABLE `osu2007`.`osu_users` ADD COLUMN `playcount` INT NULL DEFAULT 0 AFTER `password`;";

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public List<BeatMap> getAllRankedMaps() {
        List<BeatMap> maps = new ArrayList<BeatMap>();

        String query = "SELECT * FROM ranked_maps";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                BeatMap currentMap = new BeatMap(rs);

                maps.add(currentMap);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return maps;
    }

    public List<NewsPost> getAllNewsPosts() {
        List<NewsPost> posts = new ArrayList<NewsPost>();

        String query = "SELECT * FROM news_posts ";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                NewsPost currentMap = new NewsPost(rs);

                posts.add(currentMap);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return posts;
    }

    public int getRankedScoreOfUser(int userId) {
        int score = 0;

        List<BeatMap> rankedMaps = getAllRankedMaps();

        List<String> rankedMapMd5s = new ArrayList<String>();

        for (BeatMap map : rankedMaps) {
            rankedMapMd5s.add(map.md5);
        }

        String query = "SELECT * FROM score_list WHERE `userid`=?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Score currentScore = new Score(rs);

                if (rankedMapMd5s.contains(currentScore.mapHash))
                    score += currentScore.score;
            }
        } catch (SQLException | ParseException ex) {
            System.out.println(ex.getMessage());
        }

        return score;
    }

    public int getTotalScoreOfUser(int userId) {
        int score = 0;

        String query = "SELECT * FROM score_list WHERE `userid`=?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Score currentScore = new Score(rs);

                score += currentScore.score;
            }
        } catch (SQLException | ParseException ex) {
            System.out.println(ex.getMessage());
        }

        return score;
    }

    public List<Score> getAllScoresOfUser(int userId) {
        List<Score> scores = new ArrayList<Score>();

        String query = "SELECT * FROM score_list WHERE userid = ? ORDER BY score DESC";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Score currentScore = new Score(rs);

                scores.add(currentScore);
            }
        } catch (SQLException | ParseException ex) {
            System.out.println(ex.getMessage());
        }

        return scores;
    }

    public void addUser(String newUsersName, String newUsersPassword) {
        String query = "INSERT INTO osu_users(username, password) VALUES(?, ?);";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, newUsersName);
            stmt.setString(2, newUsersPassword);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updateScore(int oldId, Score newScore, byte[] replayData) {
        String query = "UPDATE score_list SET replayhash=?, hit300=?, hit100=?, hit50=?, hitgeki=?, hitkatu=?, hitmiss=?, score=?, maxcombo=?, perfect=?, grade=?, mods=?, pass=? WHERE id=?;";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, newScore.replayHash);
            stmt.setInt(2, newScore.hit300Count);
            stmt.setInt(3, newScore.hit100Count);
            stmt.setInt(4, newScore.hit50Count);
            stmt.setInt(5, newScore.hitGekiCount);
            stmt.setInt(6, newScore.hitKatuCount);
            stmt.setInt(7, newScore.hitMissCount);
            stmt.setInt(8, newScore.score);
            stmt.setInt(9, newScore.maxCombo);
            stmt.setBoolean(10, newScore.perfectCombo);
            stmt.setString(11, Character.toString(newScore.grade));
            stmt.setInt(12, newScore.mods);
            stmt.setBoolean(13, newScore.pass);
            stmt.setInt(14, oldId);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        FileHandler.saveReplayToFile(newScore.scoreId, replayData);
    }

    public void addToPlaycount(int userId) {
        String query = "UPDATE `osu_users` SET playcount = playcount + '1' WHERE id = ?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int getUserId(String userName) {
        int userId = -1;

        String query = "SELECT * FROM osu_users WHERE username=?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, userName);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                userId = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return userId;
    }

    public String getUsername(int userId) {
        String username = "";

        if (App.knownNames.get(userId) != null) {
            username = App.knownNames.get(userId);
            return username;
        }

        String query = "SELECT * FROM osu_users WHERE id=?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                username = rs.getString(2);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        if (App.knownNames.get(userId) == null) {
            App.knownNames.set(userId, username);
        }

        return username;
    }

    public int getPlaycountOfUser(int userId) {
        int playcount = 0;

        String query = "SELECT * FROM osu_users WHERE id=?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                playcount = rs.getInt(4);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return playcount;
    }

    public void addScore(Score score, byte[] replayData) {
        String query = "INSERT INTO score_list(maphash, userid, replayhash, hit300, hit100, hit50, hitgeki, hitkatu, hitmiss, score, maxcombo, perfect, grade, mods, pass)"
                + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, score.mapHash);
            stmt.setInt(2, score.userId);
            stmt.setString(3, score.replayHash);
            stmt.setInt(4, score.hit300Count);
            stmt.setInt(5, score.hit100Count);
            stmt.setInt(6, score.hit50Count);
            stmt.setInt(7, score.hitGekiCount);
            stmt.setInt(8, score.hitKatuCount);
            stmt.setInt(9, score.hitMissCount);
            stmt.setInt(10, score.score);
            stmt.setInt(11, score.maxCombo);
            stmt.setBoolean(12, score.perfectCombo);
            stmt.setString(13, Character.toString(score.grade));
            stmt.setInt(14, score.mods);
            stmt.setBoolean(15, score.pass);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        query = "SELECT * FROM score_list WHERE replayhash=?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, score.replayHash);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FileHandler.saveReplayToFile(rs.getInt(1), replayData);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<Score> getMapLeaderboard(String mapHash) {

        String query = "SELECT * FROM score_list WHERE maphash = ? ORDER BY score DESC";

        List<Score> scores = new ArrayList<Score>();

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, mapHash);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Score currentScore = new Score(rs);

                scores.add(currentScore);
            }
        } catch (SQLException | ParseException ex) {
            System.out.println(ex.getMessage());
        }

        return scores;
    }

    public List<Score> getAllScores() {
        String query = "SELECT * FROM score_list ORDER BY score DESC";

        List<Score> scores = new ArrayList<Score>();

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Score currentScore = new Score(rs);

                scores.add(currentScore);
            }
        } catch (SQLException | ParseException ex) {
            System.out.println(ex.getMessage());
        }

        return scores;
    }

    public List<Integer> getAllPlayers() {
        String query = "SELECT * FROM osu_users";

        List<Integer> allPlayers = new ArrayList<Integer>();

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                allPlayers.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return allPlayers;
    }

    public Player checkUserData(int userId) {
        String query = "SELECT * FROM osu_users WHERE id=?";

        boolean userExist = false;
        String userPassword = "";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                userExist = true;
                userPassword = rs.getString(3);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return new Player(userId, userPassword, userExist);
    }

    public void changeUsername(int userId, String newUsername) {
        String query = "UPDATE `osu2007`.`osu_users` SET username=? WHERE id=?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, newUsername);
            stmt.setInt(2, userId);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        App.knownNames.clear();
        for (int i = 0; i < getAllPlayers().size() + 1; i++) {
            App.knownNames.add(null);
        }
    }
}