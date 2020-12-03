package poltixe.osu2007;

import java.io.*;
import java.util.Properties;

public class GetPropertyValues {
    String result = "";
    InputStream inputStream;

    public String getPropValues() throws IOException {
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = new FileInputStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found");
            }

            App.httpPort = prop.getProperty("mysqlserver");

            App.mySqlServer = prop.getProperty("mysqlserver");
            App.mySqlPort = prop.getProperty("mysqlport");
            App.mySqlUser = prop.getProperty("mysqluser");
            App.mySqlPass = prop.getProperty("mysqlpass");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return result;
    }
}
