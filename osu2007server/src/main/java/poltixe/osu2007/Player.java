package poltixe.osu2007;

public class Player {
    public int userId;
    public String username;
    public int score;
    public int amountOfNumberOnes;

    private static MySqlHandler sqlHandler = new MySqlHandler();

    Player(int userId, int score, int amountOfNumberOnes) {
        this.userId = userId;
        this.username = sqlHandler.getUsername(this.userId);
        this.score = score;
        this.amountOfNumberOnes = amountOfNumberOnes;
    }
}
