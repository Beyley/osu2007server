package poltixe.osu2007;

import java.io.*;

public class FileHandler {
    public static void saveReplayToFile(Score score, byte[] replayData) {
        try {
            File myObj = new File("replays/" + score.scoreId);
            if (myObj.createNewFile()) {
                // System.out.println("File created: " + myObj.getName());
            } else {
                // System.out.println("File already exists.");
            }
        } catch (IOException e) {
        }

        try (FileOutputStream fos = new FileOutputStream("testreplay")) {
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
