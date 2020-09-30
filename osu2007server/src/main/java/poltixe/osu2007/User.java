package poltixe.osu2007;

public class User {
    public String userName;
    public String userPassword;
    public boolean userExists;

    User(String userName, String userPassword, boolean userExists) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userExists = userExists;
    }
}
