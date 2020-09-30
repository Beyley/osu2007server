package poltixe.osu2007;

public class Score {
    public String osuFileHash;
    public String playerUsername;
    public String scoreHash;
    public int hit300Count;
    public int hit100Count;
    public int hit50Count;
    public int hitGekiCount;
    public int hitKatuCount;
    public int hitMissCount;
    public int score;
    public int maxCombo;
    public boolean perfectCombo;
    public char grade;
    public int mods;
    public boolean pass;
    public int scoreId;

    Score(String scoreString, int scoreId) {
        String[] splitString = scoreString.split(":");

        this.osuFileHash = splitString[0];
        this.playerUsername = splitString[1];
        this.scoreHash = splitString[2];
        this.hit300Count = Integer.parseInt(splitString[3]);
        this.hit100Count = Integer.parseInt(splitString[4]);
        this.hit50Count = Integer.parseInt(splitString[5]);
        this.hitGekiCount = Integer.parseInt(splitString[6]);
        this.hitKatuCount = Integer.parseInt(splitString[7]);
        this.hitMissCount = Integer.parseInt(splitString[8]);
        this.score = Integer.parseInt(splitString[9]);
        this.maxCombo = Integer.parseInt(splitString[10]);
        this.perfectCombo = Boolean.parseBoolean(splitString[11]);
        this.grade = splitString[12].charAt(0);
        this.mods = Integer.parseInt(splitString[13]);
        this.pass = Boolean.parseBoolean(splitString[14]);

        this.scoreId = scoreId;
    }

    public String asSubmitString() {
        String combinedString = "";

        combinedString += this.osuFileHash + ":";
        combinedString += this.playerUsername + ":";
        combinedString += this.scoreHash + ":";
        combinedString += this.hit300Count + ":";
        combinedString += this.hit100Count + ":";
        combinedString += this.hit50Count + ":";
        combinedString += this.hitGekiCount + ":";
        combinedString += this.hitKatuCount + ":";
        combinedString += this.hitMissCount + ":";
        combinedString += this.score + ":";
        combinedString += this.maxCombo + ":";
        combinedString += this.perfectCombo + ":";
        combinedString += this.grade + ":";
        combinedString += this.mods + ":";
        combinedString += this.pass;

        return combinedString;
    }

    public String asGetScoresString() {
        String combinedString = "";

        combinedString += this.scoreId + ":";
        combinedString += this.playerUsername + ":";
        combinedString += this.score + ":";
        combinedString += this.maxCombo + ":";
        combinedString += this.hit50Count + ":";
        combinedString += this.hit100Count + ":";
        combinedString += this.hit300Count + ":";
        combinedString += this.hitMissCount + ":";
        combinedString += this.hitKatuCount + ":";
        combinedString += this.hitGekiCount + ":";
        combinedString += this.perfectCombo + ":";
        combinedString += this.mods + "\n";

        return combinedString;
    }
}
