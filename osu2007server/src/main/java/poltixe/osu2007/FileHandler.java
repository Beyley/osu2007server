package poltixe.osu2007;

import java.io.*;
import java.util.*;

public class FileHandler {
    public static MySqlHandler sqlHandler = new MySqlHandler();

    public static void rankedDatabaseCheck() throws FileNotFoundException {
        File myObj = new File("rankeddatabase.txt");
        Scanner myReader = new Scanner(myObj);

        List<BeatMap> rankedMaps = new ArrayList<BeatMap>();

        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String[] split = data.split(":");

            String artist = split[0];
            String songName = split[1];
            String diffName = split[2];
            String creator = split[3];
            String md5 = split[4];

            rankedMaps.add(new BeatMap(artist, songName, diffName, creator, md5));
        }

        myReader.close();

        sqlHandler.checkForRankedTable();

        System.out.println("Importing Ranked Songs database");
        sqlHandler.addRankedMapsToTable(rankedMaps);
    }

    public static void saveReplayToFile(Score score, byte[] replayData) {
        String filePath = "replays/" + score.scoreId + ".osr";

        try {
            File myObj = new File(filePath);

            if (myObj.createNewFile()) {
                // System.out.println("File created: " + myObj.getName());
            } else {
                myObj.delete();
                myObj.createNewFile();
            }
        } catch (IOException e) {
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(replayData);
        } catch (IOException e) {
        }
    }

    public static byte[] parseBody(byte[] bodyData) {
        byte[] parsedBody = bodyData;

        for (int i = 135; i >= 0; i--) {
            byte[] copyArray = new byte[parsedBody.length - 1];

            // copy elements from original array from beginning till index into copyArray
            System.arraycopy(parsedBody, 0, copyArray, 0, i);

            // copy elements from original array from index+1 till end into copyArray
            System.arraycopy(parsedBody, i + 1, copyArray, i, parsedBody.length - i - 1);

            parsedBody = copyArray;
        }

        int test = parsedBody.length - 29; // 31

        for (int i = parsedBody.length - 1; i >= test; i--) {
            byte[] copyArray = new byte[parsedBody.length - 1];

            // copy elements from original array from beginning till index into copyArray
            System.arraycopy(parsedBody, 0, copyArray, 0, i);

            // copy elements from original array from index+1 till end into copyArray
            System.arraycopy(parsedBody, i + 1, copyArray, i, parsedBody.length - i - 1);

            parsedBody = copyArray;
        }

        return parsedBody;
    }
}
