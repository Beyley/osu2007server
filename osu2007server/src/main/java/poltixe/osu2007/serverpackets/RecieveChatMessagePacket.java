package poltixe.osu2007.serverpackets;

import java.text.*;
import java.util.*;

import poltixe.osu2007.Player;

public class RecieveChatMessagePacket {
    public Player sender;
    public String message;

    public RecieveChatMessagePacket(String message, Player sender) {
        this.sender = sender;
        this.message = message;
    }

    public String getFinalPacket() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date currentDate = new Date();
        String currentDateString = dateFormat.format(currentDate);

        return String.format("%s <%s> %s", currentDateString, this.sender.username, this.message);
    }
}
