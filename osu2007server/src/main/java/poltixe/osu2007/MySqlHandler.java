package poltixe.osu2007;

import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.text.ParseException;
import java.util.List;

import com.mysql.cj.jdbc.DatabaseMetaData;

import java.util.ArrayList;

public class MySqlHandler {
    private String connectionUrl = "jdbc:mysql://" + App.mySqlServer + ":" + App.mySqlPort
            + "/?useSSL=false&autoReconnect=true";

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
        String query = "CREATE TABLE `osu2007`.`ranked_maps` ( `id` INT NOT NULL AUTO_INCREMENT, `md5` VARCHAR(100) NULL, `starrating` DOUBLE NOT NULL, `artist` VARCHAR(250) NULL, `songname` VARCHAR(250) NULL, `diffname` VARCHAR(250) NULL, `creator` VARCHAR(250) NULL, `circlesize` DOUBLE NULL, `hpdrainrate` DOUBLE NULL, `overalldifficulty` DOUBLE NULL, `slidervelocity` DOUBLE NULL, `slidertickrate` DOUBLE NULL, `bpm` DOUBLE NULL, `length` DOUBLE NULL, `draintime` DOUBLE NULL, PRIMARY KEY (`id`));";

        try (Statement st = (Statement) con.createStatement()) {

            st.execute(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        query = "";

        List<MapSet> mapSets = new ArrayList<MapSet>();

        for (BeatMap map : rankedMaps) {
            boolean isMapInAnySet = false;

            for (MapSet set : mapSets) {
                for (BeatMap mapSetMap : set.maps) {
                    if (mapSetMap.artist.equals(map.artist) && mapSetMap.title.equals(map.title)
                            && mapSetMap.creator.equals(map.creator)) {
                        isMapInAnySet = true;
                    }
                }
            }

            if (!isMapInAnySet) {
                MapSet setToAddTo = null;

                for (MapSet set : mapSets) {
                    for (BeatMap mapSetMap : set.maps) {
                        if (mapSetMap.artist.equals(map.artist) && mapSetMap.title.equals(map.title)
                                && mapSetMap.creator.equals(map.creator)) {
                            setToAddTo = set;
                        }
                    }
                }

                if (setToAddTo == null) {
                    MapSet tempSet = new MapSet();
                    tempSet.maps.add(map);

                    mapSets.add(tempSet);
                } else {
                    setToAddTo.maps.add(map);
                }
            }

            if (map.artist.equals("")) {
                map.artist = "none";
            }

            query = "INSERT INTO `osu2007`.`ranked_maps` (`md5`, `starrating`, `artist`, `songname`, `diffname`, `creator`, `circlesize`, `hpdrainrate`, `overalldifficulty`, `slidervelocity`, `slidertickrate`, `bpm`, `length`, `draintime`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            try {
                PreparedStatement stmt = con.prepareStatement(query);

                stmt.setString(1, map.md5);
                stmt.setString(2, String.valueOf(map.starRating));
                stmt.setString(3, map.artist);
                stmt.setString(4, map.title);
                stmt.setString(5, map.diffName);
                stmt.setString(6, map.creator);
                stmt.setString(7, String.valueOf(map.circleSize));
                stmt.setString(8, String.valueOf(map.hpDrainRate));
                stmt.setString(9, String.valueOf(map.overallDifficulty));
                stmt.setString(10, String.valueOf(map.sliderVelocity));
                stmt.setString(11, String.valueOf(map.sliderTickRate));
                stmt.setString(12, String.valueOf(map.bpm));
                stmt.setString(13, String.valueOf(map.length));
                stmt.setString(14, String.valueOf(map.drainTime));

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

        rankedTableExist = false;

        try {
            DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

            ResultSet rs1 = md.getTables(null, null, "mapset_list", null);

            while (rs1.next()) {
                rankedTableExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        if (rankedTableExist) {
            String query = "DROP TABLE `osu2007`.`mapset_list`";

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

        // try {
        //     DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

        //     ResultSet rs1 = md.getTables(null, null, "score_list", null);

        //     while (rs1.next()) {
        //         scoreListExist = true;
        //     }
        // } catch (SQLException ex) {
        //     System.out.println(ex.getMessage());
        // }

        if (!scoreListExist) {
            query = "CREATE TABLE `osu2007`.`score_list` ( `id` INT NOT NULL AUTO_INCREMENT, `maphash` VARCHAR(100) NOT NULL, `userid` INT NOT NULL, `replayhash` VARCHAR(100) NOT NULL, `hit300` INT NOT NULL, `hit100` INT NOT NULL, `hit50` INT NOT NULL, `hitgeki` INT NOT NULL, `hitkatu` INT NOT NULL, `hitmiss` INT NOT NULL, `score` INT NOT NULL, `maxcombo` INT NOT NULL, `perfect` VARCHAR(5) NOT NULL, `grade` VARCHAR(1) NOT NULL, `mods` INT NOT NULL, `pass` VARCHAR(5) NOT NULL, `timesubmitted` INT NULL, PRIMARY KEY (`id`));";

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        // boolean oldScoreListExist = false;

        // try {

        //     DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

        //     ResultSet rs1 = md.getTables(null, null, "scores", null);

        //     while (rs1.next()) {
        //         oldScoreListExist = true;
        //     }
        // } catch (SQLException ex) {
        //     System.out.println(ex.getMessage());
        // }

        // if (oldScoreListExist) {
        //     // CONVERT DATABASE TO NEW FORMAT

        //     // Rename users table
        //     query = "ALTER TABLE `osu2007`.`users` RENAME TO `osu2007`.`osu_users`;";

        //     try (Statement st = (Statement) con.createStatement()) {
        //         st.execute(query);
        //     } catch (SQLException ex) {
        //         // System.out.println(ex.getMessage());
        //     }

        //     query = "SELECT * FROM scores";

        //     List<Score> scores = new ArrayList<Score>();

        //     try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {

        //         while (rs.next()) {
        //             Score currentScore = new Score(rs.getString(2), rs.getInt(1));

        //             scores.add(currentScore);
        //         }
        //     } catch (SQLException | ParseException ex) {
        //         System.out.println(ex.getMessage());
        //     }

        //     PreparedStatement stmt = null;

        //     for (Score score : scores) {
        //         query = "INSERT INTO `osu2007`.`score_list`(id, maphash, userid, replayhash, hit300, hit100, hit50, hitgeki, hitkatu, hitmiss, score, maxcombo, perfect, grade, mods, pass) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        //         try {
        //             stmt = con.prepareStatement(query);
        //             stmt.setInt(1, score.scoreId);
        //             stmt.setString(2, score.mapHash);
        //             stmt.setInt(3, score.userId);
        //             stmt.setString(4, score.replayHash);
        //             stmt.setInt(5, score.hit300);
        //             stmt.setInt(6, score.hit100);
        //             stmt.setInt(7, score.hit50);
        //             stmt.setInt(8, score.hitGeki);
        //             stmt.setInt(9, score.hitKatu);
        //             stmt.setInt(10, score.hitMiss);
        //             stmt.setInt(11, score.score);
        //             stmt.setInt(12, score.maxCombo);
        //             stmt.setBoolean(13, score.perfectCombo);
        //             stmt.setString(14, Character.toString(score.grade));
        //             stmt.setInt(15, score.mods);
        //             stmt.setBoolean(16, score.pass);

        //             stmt.execute();

        //             stmt.close();
        //         } catch (SQLException ex) {
        //             System.out.println(ex.getMessage());
        //         }
        //     }

        //     query = "DROP TABLE `osu2007`.`scores`;";

        //     try (Statement st = (Statement) con.createStatement()) {
        //         st.execute(query);
        //     } catch (SQLException ex) {
        //         // System.out.println(ex.getMessage());
        //     }
        // }

        boolean usersExist = false;

        // try {
        //     DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

        //     ResultSet rs1 = md.getTables(null, null, "osu_users", null);

        //     while (rs1.next()) {
        //         usersExist = true;
        //     }
        // } catch (SQLException ex) {
        //     System.out.println(ex.getMessage());
        // }

        if (!usersExist) {
            query = "CREATE TABLE `osu2007`.`osu_users` (`id` INT NOT NULL AUTO_INCREMENT, `username` VARCHAR(50) NULL, `password` VARCHAR(500) NULL, PRIMARY KEY (`id`));";

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        boolean newsExist = false;

        // try {
        //     DatabaseMetaData md = (DatabaseMetaData) con.getMetaData();

        //     ResultSet rs1 = md.getTables(null, null, "news_posts", null);

        //     while (rs1.next()) {
        //         newsExist = true;
        //     }
        // } catch (SQLException ex) {
        //     System.out.println(ex.getMessage());
        // }

        if (!newsExist) {
            query = "CREATE TABLE `osu2007`.`news_posts` (`id` INT NOT NULL AUTO_INCREMENT, `creator` VARCHAR(100) NULL, `time` DATETIME NULL DEFAULT current_timestamp, `content` LONGTEXT NULL, `title` LONGTEXT NULL, PRIMARY KEY (`id`));";

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            // query = "INSERT INTO `osu2007`.`news_posts` (`title`, `creator`, `content`) VALUES ('Test Post', 'PoltixeTheDerg', 'This is an example post!');";

            // try (Statement st = (Statement) con.createStatement()) {
            //     st.execute(query);
            // } catch (SQLException ex) {
            //     System.out.println(ex.getMessage());
            // }
        }

        boolean playcountColumnExist = false;

        query = "SHOW COLUMNS FROM `osu2007`.`osu_users` LIKE 'playcount';";

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

        boolean timeSubmittedExist = false;

        query = "SHOW COLUMNS FROM `osu2007`.`score_list` LIKE 'timesubmitted';";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                timeSubmittedExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        if (!timeSubmittedExist) {
            query = "ALTER TABLE `osu2007`.`score_list` ADD COLUMN `timesubmitted` INT NULL AFTER `pass`";

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            Path startingDir = Paths.get("replays");
            GetAllReplays gar = new GetAllReplays();
            try {
                Files.walkFileTree(startingDir, gar);
            } catch (IOException e) {
            }
        }

        boolean ipColumnExist = false;

        query = "SHOW COLUMNS FROM `osu2007`.`osu_users` LIKE 'ip';";

        try (Statement st = (Statement) con.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                ipColumnExist = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        if (!ipColumnExist) {
            query = "ALTER TABLE `osu2007`.`osu_users` ADD COLUMN `ip` LONGTEXT NULL AFTER `playcount`;";

            try (Statement st = (Statement) con.createStatement()) {
                st.execute(query);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void setTimeOfScore(int id, long time) {
        String query = "UPDATE `osu2007`.`score_list` SET timesubmitted=? WHERE id=?";

        try (Statement st = (Statement) con.createStatement()) {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setLong(1, time);
            stmt.setInt(2, id);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<BeatMap> getAllRankedMaps() {
        List<BeatMap> maps = new ArrayList<BeatMap>();

        String query = "SELECT * FROM `osu2007`.`ranked_maps`";

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

    public List<BeatMap> getRankedMaps(int startPos, int endPos) {
        List<BeatMap> maps = new ArrayList<BeatMap>();

        String query = "SELECT * FROM `osu2007`.`ranked_maps` ORDER BY id LIMIT " + startPos + ", " + endPos;

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

    public List<BeatMap> searchRankedMaps(String searchQuery) {
        List<BeatMap> maps = new ArrayList<BeatMap>();

        String query = "SELECT * FROM `osu2007`.`ranked_maps` WHERE songname LIKE ? ORDER BY id;";

        try (Statement st = (Statement) con.createStatement()) {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, "%" + searchQuery + "%");

            ResultSet rs = stmt.executeQuery();

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

        String query = "SELECT * FROM `osu2007`.`news_posts` ";

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

        String query = "SELECT * FROM `osu2007`.`score_list` WHERE `userid`=? and pass='1' ORDER BY score DESC";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Score currentScore = new Score(rs);

                if (rankedMapMd5s.contains(currentScore.mapHash)) {
                    score += currentScore.score;
                    rankedMapMd5s.remove(rankedMapMd5s.indexOf(currentScore.mapHash));
                }
            }
        } catch (SQLException | ParseException ex) {
            System.out.println(ex.getMessage());
        }

        return score;
    }

    public int getTotalScoreOfUser(int userId) {
        int score = 0;

        String query = "SELECT * FROM `osu2007`.`score_list` WHERE userid=? and pass=?";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);
            stmt.setBoolean(2, true);

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

        String query = "SELECT * FROM `osu2007`.`score_list` WHERE userid = ? and pass=? ORDER BY score DESC";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setInt(1, userId);
            stmt.setBoolean(2, true);

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

    public void addUser(String newUsersName, String newUsersPassword, String ip) {
        String query = "INSERT INTO `osu2007`.`osu_users`(username, password, ip) VALUES(?, ?, ?);";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, newUsersName);
            stmt.setString(2, newUsersPassword);
            stmt.setString(3, ip);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void updateScore(int oldId, Score newScore, byte[] replayData) {
        String query = "UPDATE `osu2007`.`score_list` SET replayhash=?, hit300=?, hit100=?, hit50=?, hitgeki=?, hitkatu=?, hitmiss=?, score=?, maxcombo=?, perfect=?, grade=?, mods=?, pass=?, timesubmitted=? WHERE id=?;";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, newScore.replayHash);
            stmt.setInt(2, newScore.hit300);
            stmt.setInt(3, newScore.hit100);
            stmt.setInt(4, newScore.hit50);
            stmt.setInt(5, newScore.hitGeki);
            stmt.setInt(6, newScore.hitKatu);
            stmt.setInt(7, newScore.hitMiss);
            stmt.setInt(8, newScore.score);
            stmt.setInt(9, newScore.maxCombo);
            stmt.setBoolean(10, newScore.perfectCombo);
            stmt.setString(11, Character.toString(newScore.grade));
            stmt.setInt(12, newScore.mods);
            stmt.setBoolean(13, newScore.pass);
            stmt.setLong(14, newScore.timeSubmitted);
            stmt.setInt(15, oldId);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        FileHandler.saveReplayToFile(newScore.scoreId, replayData);
    }

    public void updateUserIp(String userName, String newIp) {
        String query = "UPDATE `osu2007`.`osu_users` SET ip=? WHERE username=?;";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, newIp);
            stmt.setString(2, userName);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public boolean isIpInUse(String ip) {
        String query = "SELECT * FROM `osu2007`.`osu_users` WHERE ip=?;";

        boolean used = false;

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, ip);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                used = true;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return used;
    }

    public void addToPlaycount(int userId) {
        String query = "UPDATE `osu2007`.`osu_users` SET playcount = playcount + '1' WHERE id = ?";

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

        String query = "SELECT * FROM `osu2007`.`osu_users` WHERE username=?";

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

        String query = "SELECT * FROM `osu2007`.`osu_users` WHERE id=?";

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

        String query = "SELECT * FROM `osu2007`.`osu_users` WHERE id=?";

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
        String query = "INSERT INTO `osu2007`.`score_list`(maphash, userid, replayhash, hit300, hit100, hit50, hitgeki, hitkatu, hitmiss, score, maxcombo, perfect, grade, mods, pass, timesubmitted)"
                + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, score.mapHash);
            stmt.setInt(2, score.userId);
            stmt.setString(3, score.replayHash);
            stmt.setInt(4, score.hit300);
            stmt.setInt(5, score.hit100);
            stmt.setInt(6, score.hit50);
            stmt.setInt(7, score.hitGeki);
            stmt.setInt(8, score.hitKatu);
            stmt.setInt(9, score.hitMiss);
            stmt.setInt(10, score.score);
            stmt.setInt(11, score.maxCombo);
            stmt.setBoolean(12, score.perfectCombo);
            stmt.setString(13, Character.toString(score.grade));
            stmt.setInt(14, score.mods);
            stmt.setBoolean(15, score.pass);
            stmt.setLong(16, score.timeSubmitted);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        query = "SELECT * FROM `osu2007`.`score_list` WHERE replayhash=?";

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

    public void addFailedScore(Score score) {
        String query = "INSERT INTO `osu2007`.`score_list`(maphash, userid, replayhash, hit300, hit100, hit50, hitgeki, hitkatu, hitmiss, score, maxcombo, perfect, grade, mods, pass, timesubmitted)"
                + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, score.mapHash);
            stmt.setInt(2, score.userId);
            stmt.setString(3, score.replayHash);
            stmt.setInt(4, score.hit300);
            stmt.setInt(5, score.hit100);
            stmt.setInt(6, score.hit50);
            stmt.setInt(7, score.hitGeki);
            stmt.setInt(8, score.hitKatu);
            stmt.setInt(9, score.hitMiss);
            stmt.setInt(10, score.score);
            stmt.setInt(11, score.maxCombo);
            stmt.setBoolean(12, score.perfectCombo);
            stmt.setString(13, Character.toString(score.grade));
            stmt.setInt(14, score.mods);
            stmt.setBoolean(15, score.pass);
            stmt.setLong(16, score.timeSubmitted);

            stmt.execute();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<Score> getMapLeaderboard(String mapHash) {
        String query = "SELECT * FROM `osu2007`.`score_list` WHERE maphash = ? and pass=? ORDER BY score DESC LIMIT 50";

        List<Score> scores = new ArrayList<Score>();

        try {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setString(1, mapHash);
            stmt.setBoolean(2, true);

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

    public double getMapSuccessRate(String mapHash) {
        String query = "SELECT * FROM `osu2007`.`score_list` WHERE maphash = ? ORDER BY score DESC";

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

        double total = 0;

        for (Score score : scores) {
            total += (score.pass ? 1 : 0);
        }

        double average = (double) total / (double) scores.size();

        return average * (double) 100.0;
    }

    public double getMapTotalTries(String mapHash) {
        String query = "SELECT * FROM `osu2007`.`score_list` WHERE maphash = ? ORDER BY score DESC";

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

        return scores.size();
    }

    public double getMapTotalPasses(String mapHash) {
        String query = "SELECT * FROM `osu2007`.`score_list` WHERE maphash = ? ORDER BY score DESC";

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

        double total = 0;

        for (Score score : scores) {
            total += (score.pass ? 1 : 0);
        }

        return total;
    }

    public List<Score> getAllScores() {
        String query = "SELECT * FROM `osu2007`.`score_list` WHERE pass=? ORDER BY score DESC";

        List<Score> scores = new ArrayList<Score>();

        try (Statement st = (Statement) con.createStatement();) {
            PreparedStatement stmt = con.prepareStatement(query);

            stmt.setBoolean(1, true);

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

    public List<Integer> getAllPlayers() {
        String query = "SELECT * FROM `osu2007`.`osu_users`";

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
        String query = "SELECT * FROM `osu2007`.`osu_users` WHERE id=?";

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
        for (int i = 0; i < 1000000; i++) {
            App.knownNames.add(null);
        }
    }
}