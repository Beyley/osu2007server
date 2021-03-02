package poltixe.osu2007;

import java.util.*;

public class Html {
    private static String header1Tag = "h1";
    private static String header2Tag = "h2";
    private static String header3Tag = "h3";
    private static String boldTag = "b";

    public static String header1(String content, String style) {
        String returnString = "";

        if (style == "") {
            returnString += "<" + header1Tag + ">";
        } else {
            returnString += "<" + header1Tag + " style=\"" + style + "\">";
        }

        returnString += content + "</" + header1Tag + ">";

        return returnString;
    }

    public static String header2(String content, String style) {
        String returnString = "";

        if (style == "") {
            returnString += "<" + header2Tag + ">";
        } else {
            returnString += "<" + header2Tag + " style=\"" + style + "\">";
        }

        returnString += content + "</" + header2Tag + ">";

        return returnString;
    }

    public static String header3(String content, String style) {
        String returnString = "";

        if (style == "") {
            returnString += "<" + header3Tag + ">";
        } else {
            returnString += "<" + header3Tag + " style=\"" + style + "\">";
        }

        returnString += content + "</" + header3Tag + ">";

        return returnString;
    }

    public static String bold(String content, String style) {
        String returnString = "";

        if (style == "") {
            returnString += "<" + boldTag + ">";
        } else {
            returnString += "<" + boldTag + " style=\"" + style + "\">";
        }

        returnString += content + "</" + boldTag + ">";

        return returnString;
    }

    public static String createTable(List<List<String>> tableContents) {
        String returnString = "<table style=\"width:50%\">\n";

        for (List<String> row : tableContents) {
            returnString += "<tr>\n";

            for (String column : row) {
                returnString += "<td>" + column + "</td>\n";
            }

            returnString += "</tr>\n";
        }

        return returnString;
    }

    public static class HtmlTag {
        public String tag;
        public String content;
        public String style = "";

        HtmlTag(String tag, String content) {
            this.tag = tag;
            this.content = content;
        }

        HtmlTag(String tag, String content, String style) {
            this.tag = tag;
            this.content = content;
            this.style = style;
        }

        public String getAsHtml() {
            if (style == "") {
                return "<" + this.tag + ">" + this.content + "</" + this.tag + ">";
            } else {
                return "<" + this.tag + " style=\"" + style + "\">" + this.content + "</" + this.tag + ">";
            }
        }
    }

    public static class Image {
        String src = "";
        String altText = "Could not load image";

        String style = "";

        Image(String src) {
            this.src = src;
        }

        Image(String src, String altText) {
            this.src = src;
            this.altText = altText;
        }

        public String getAsHtml() {
            if (style == "") {
                return "<img src=\"" + src + "\" alt=\"" + altText + "\">";
            } else {
                return "<img src=\"" + src + "\" alt=\"" + altText + "\" style=\"" + style + "\">";
            }
        }
    }
}
