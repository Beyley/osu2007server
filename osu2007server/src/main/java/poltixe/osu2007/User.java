package poltixe.osu2007;

public class User {
    public int userId;
    public String userPassword;
    public boolean userExists;

    User(int userId, String userPassword, boolean userExists) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.userExists = userExists;
    }
}
