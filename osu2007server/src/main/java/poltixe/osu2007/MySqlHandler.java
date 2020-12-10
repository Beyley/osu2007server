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

        String query = "CREATE TABLE `osu2007`.`ranked_maps` ( `id` INT NOT NULL AUTO_INCREMENT, `md5` VARCHAR(100) NULL, `artist` VARCHAR(250) NULL, `songname` VARCHAR(250) NULL, `diffname` VARCHAR(250) NULL, `creator` VARCHAR(250) NULL, PRIMARY KEY (`id`));";

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

            query = "INSERT INTO `osu2007`.`ranked_maps` (`md5`, `artist`, `songname`, `diffname`, `creator`) VALUES (?, ?, ?, ?, ?);";

            try {
                Statement st = (Statement) con.createStatement();

                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void checkForRankedTable() {

        boolean rankedTableExist = false;

        try (Statement st = (Statement) con.createStatement()) {

            DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

            ResultSet rs1 = md.getTables(null, null, "ranked_maps", null);

            while (rs1.next()) {
                rankedTableExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // DROP TABLE `osu2007`.`ranked_maps`
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

        try (Statement st = (Statement) con.createStatement()) {

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

        try (Statement st = (Statement) con.createStatement()) {

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
                    stmt.setInt(14, score.grade);
                    stmt.setInt(15, score.mods);
                    stmt.setBoolean(16, score.pass);

                    stmt.execute(query);

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

        try (Statement st = (Statement) con.createStatement()) {

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

    public int getRankedScoreOfUser(int userId) {
        int score = 0;

        List<BeatMap> rankedMaps = getAllRankedMaps();

        List<String> rankedMapMd5s = new ArrayList<String>();

        for (BeatMap map : rankedMaps) {
            rankedMapMd5s.add(map.md5);
        }

        String query = "SELECT * FROM score_list WHERE userid = '" + userId + "'";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

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

        String query = "SELECT * FROM score_list WHERE userid = '" + userId + "'";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

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

        String query = "SELECT * FROM score_list WHERE userid = '" + userId + "'";

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

    public void addUser(String newUsersName, String newUsersPassword) {

        String query = "INSERT INTO osu_users(username, password) VALUES('" + newUsersName + "', '" + newUsersPassword
                + "');";

        try (Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void removeScore(Score score) {

        String query = "DELETE FROM score_list WHERE id='" + score.scoreId + "';";

        try (Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updateScore(int oldId, Score newScore, byte[] replayData) {

        String query = "UPDATE score_list SET replayhash='" + newScore.replayHash + "', hit300='" + newScore.hit300Count
                + "', hit100='" + newScore.hit100Count + "', hit50='" + newScore.hit50Count + "', hitgeki='"
                + newScore.hitGekiCount + "', hitkatu='" + newScore.hitKatuCount + "', hitmiss='"
                + newScore.hitMissCount + "', score='" + newScore.score + "', maxcombo='" + newScore.maxCombo
                + "', perfect='" + newScore.perfectCombo + "', grade='" + newScore.grade + "', mods='" + newScore.mods
                + "', pass='" + newScore.mods + "' WHERE id='" + newScore.scoreId + "';";

        try (Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        FileHandler.saveReplayToFile(newScore, replayData);
    }

    public void addToPlaycount(int userId) {

        String query = "UPDATE `osu_users` SET playcount = playcount + '1' WHERE id = '" + userId + "'";

        try (Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int getUserId(String userName) {
        int userId = -1;

        String query = "SELECT * FROM osu_users";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

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

        if (App.knownNames.get(userId) != null) {
            username = App.knownNames.get(userId);
            return username;
        }

        String query = "SELECT * FROM osu_users";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                if (rs.getInt(1) == userId) {
                    username = rs.getString(2);
                }
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

        String query = "SELECT * FROM osu_users WHERE id='" + userId + "'";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                playcount = rs.getInt(4);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return playcount;
    }

    public void addScore(Score score, byte[] replayData) {
        String query = "INSERT INTO score_list(maphash, userid, replayhash, hit300, hit100, hit50, hitgeki, hitkatu, hitmiss, score, maxcombo, perfect, grade, mods, pass) VALUES('"
                + score.mapHash + "', '" + score.userId + "', '" + score.replayHash + "', '" + score.hit300Count
                + "', '" + score.hit100Count + "', '" + score.hit50Count + "', '" + score.hitGekiCount + "', '"
                + score.hitKatuCount + "', '" + score.hitMissCount + "', '" + score.score + "', '" + score.maxCombo
                + "', '" + score.perfectCombo + "', '" + score.grade + "', '" + score.mods + "', '" + score.pass
                + "');";

        try (Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        query = "SELECT * FROM score_list";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Score currentScore = new Score(rs);

                if (currentScore.replayHash.equals(score.replayHash)) {
                    FileHandler.saveReplayToFile(currentScore, replayData);
                }
            }

            // FileHandler.saveReplayToFile(score, replayData);
        } catch (SQLException | ParseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<Score> getMapLeaderboard(String mapHash) {

        String query = "SELECT * FROM score_list WHERE maphash = '" + mapHash + "'";

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

    public List<Score> getAllScores() {

        String query = "SELECT * FROM score_list";

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

        String query = "SELECT * FROM osu_users";

        boolean userExist = false;
        String userPassword = "";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

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

    public void changeUsername(int userId, String newUsername) {
        String query = "UPDATE `osu2007`.`osu_users` SET username='" + newUsername + "' WHERE id='" + userId + "'";

        try (Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        App.knownNames.clear();
        for (int i = 0; i < getAllPlayers().size() + 1; i++) {
            App.knownNames.add(null);
        }
    }
}