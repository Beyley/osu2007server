package poltixe.osu2007.serverpackets;

import poltixe.osu2007.*;

public class RecieveChatMessagePacket {
    public Player sender;
    public String message;

    public RecieveChatMessagePacket(String message, Player sender) {
        this.sender = sender;
        this.message = message;
    }

    public String getFinalPacket() {
        return String.format("%d|%s|%s", ServerPackets.recieveMessage, this.sender.username, this.message);
    }
}
