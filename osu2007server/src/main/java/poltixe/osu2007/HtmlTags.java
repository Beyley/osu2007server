package poltixe.osu2007;

public class HtmlTags {
    private static String header1Tag = "h1";
    private static String header2Tag = "h2";
    private static String boldTag = "b";

    public static String header1(String content) {
        String returnString = "<" + header1Tag + ">";

        returnString += content + "</" + header1Tag + ">";

        return returnString;
    }

    public static String header2(String content) {
        String returnString = "<" + header2Tag + ">";

        returnString += content + "</" + header2Tag + ">";

        return returnString;
    }

    public static String bold(String content) {
        String returnString = "<" + boldTag + ">";

        returnString += content + "</" + boldTag + ">";

        return returnString;
    }
}
