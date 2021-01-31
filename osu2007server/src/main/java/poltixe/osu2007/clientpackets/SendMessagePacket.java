package poltixe.osu2007.clientpackets;

import poltixe.osu2007.Player;

public class SendMessagePacket {
    public String message;
    public Player sender;

    public SendMessagePacket(String[] data, Player sender) {
        message = data[0];
        this.sender = sender;
    }
}
