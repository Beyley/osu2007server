package poltixe.osu2007;

import java.util.*;

public class BasicPacket {
    public int packetId;
    public String[] data;

    public BasicPacket(String raw) {
        String[] split = raw.split("|");

        this.packetId = Integer.parseInt(split[0]);
        this.data = Arrays.copyOfRange(split, 1, raw.length() - 1);
    }

    public static List<BasicPacket> parseRequest(String raw) {
        List<BasicPacket> parsedPackets = new ArrayList<BasicPacket>();

        String[] split = raw.split("\n");

        for (String packet : split) {
            parsedPackets.add(new BasicPacket(packet));
        }

        return parsedPackets;
    }
}
