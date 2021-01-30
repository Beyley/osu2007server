package poltixe.osu2007;

import java.util.*;

import poltixe.osu2007.clientpackets.SendMessagePacket;

public class MessageToSend {
    public SendMessagePacket packet;

    public List<Player> alreadySentTo = new ArrayList<Player>();

    public MessageToSend(SendMessagePacket packet) {
        this.packet = packet;
    }
}
