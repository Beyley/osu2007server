package poltixe.osu2007;

public class Player {
    public int userId;
    public String username;
    public String displayUsername;
    public int rankedScore;
    public int amountOfNumberOnes;
    public String userPassword;
    public boolean userExists;
    public int globalRank;

    private static MySqlHandler sqlHandler = new MySqlHandler();

    Player(int userId, int amountOfNumberOnes) {
        this.userId = userId;
        this.username = sqlHandler.getUsername(this.userId);
        this.displayUsername = "<a href=\"/web/u?id=" + this.userId + "\">" + this.username + "</a>";
        this.rankedScore = sqlHandler.getRankedScoreOfUser(this.userId);
        this.amountOfNumberOnes = amountOfNumberOnes;
    }

    Player(int userId, String userPassword, boolean userExists) {
        this.userId = userId;
        this.username = sqlHandler.getUsername(this.userId);
        this.displayUsername = "<a href=\"/web/u?id=" + this.userId + "\">" + this.username + "</a>";
        this.userPassword = userPassword;
        this.userExists = userExists;
        this.rankedScore = sqlHandler.getRankedScoreOfUser(this.userId);
    }
}
