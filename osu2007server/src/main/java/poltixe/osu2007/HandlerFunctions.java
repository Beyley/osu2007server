package poltixe.osu2007;

import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class HandlerFunctions {
    public static boolean isMD5(String s) {
        return s.matches("^[a-fA-F0-9]{32}$");
    }

    public static boolean isAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9\\- ]*$");
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Path {
        String verb()

        default "get";

        String path();
    }

    public static String toHexString(byte[] bytes) {
        char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v / 16];
            hexChars[j * 2 + 1] = hexArray[v % 16];
        }

        return new String(hexChars);
    }

    public static String getNavbar() {
        String returnString = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/navbar.html");

        try {
            returnString = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return returnString.toString();
    }

    public static String getStandardHeader() {
        String returnString = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/header.html");

        try {
            returnString = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return returnString.toString();
    }

    public static String getFooter() {
        String returnString = "";

        InputStream is = App.class.getClassLoader().getResourceAsStream("htmltemplates/footer.html");

        try {
            returnString = new String(is.readAllBytes());
        } catch (IOException e) {
        }

        return returnString.toString();
    }

    public static String createHtmlPage(String content) {
        StringBuilder returnString = new StringBuilder();

        returnString.append(getStandardHeader() + "\n");

        returnString.append(content);

        returnString.append(getFooter());

        return returnString.toString();
    }

    public static String createStarPattern(int filled, int max) {
        String returnString = "";

        returnString += "<div class=\"starfield\" style=\"width:" + (max * 14) + "px;\">";
        returnString += "<div class=\"active\" style=\"width:"
                + ((double) ((double) filled / (double) max) * (double) 100.0) + "%;\"></div>";
        returnString += "</div>";

        return returnString;
    }

    public static String getSecondsFixed(double seconds) {
        double year = 31540000;
        double month = 2628000; // assume 30 days in a month
        double day = 86400;
        double hour = 3600;
        double minute = 60;

        long years = (long) Math.floor(seconds / year);
        long months = (long) Math.floor((seconds - years * year) / month);
        long days = (long) Math.floor(((seconds - years * year) - months * month) / day);
        long hours = (long) Math.floor((((seconds - years * year) - months * month) - days * day) / hour);
        long minutes = (long) Math
                .floor(((((seconds - years * year) - months * month) - days * day) - hours * hour) / minute);
        // long seconds2 = (long) Math
        // .floor(((((seconds - years * year) - months * month) - days * day) - hours *
        // hour) - minutes * minute);

        String str = "";
        if (years != 0)
            str += String.format("%s Year%s, ", years, years > 1 ? "s" : "");
        if (months != 0)
            str += String.format("%s Month%s, ", months, months > 1 ? "s" : "");
        if (days != 0)
            str += String.format("%s Day%s, ", days, days > 1 ? "s" : "");
        if (hours != 0)
            str += String.format("%s Hour%s, ", hours, hours > 1 ? "s" : "");
        if (minutes != 0)
            str += String.format("%s Minute%s, ", minutes, minutes > 1 ? "s" : "");
        // if (seconds2 != 0)
        // str += String.format("%s Second%s, ", seconds2, seconds2 > 1 ? "s" : "");

        return str.substring(0, str.length() - 2);
    }
}
