package poltixe.osu2007;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class GetPropertyValues {
    String result = "";
    InputStream inputStream;

    public String getPropValues() throws IOException {
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

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
