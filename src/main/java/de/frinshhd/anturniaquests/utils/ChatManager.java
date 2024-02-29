package de.frinshhd.anturniaquests.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatManager {

    public static boolean sendMessage(CommandSender sender, String message) {
        return sendMessageRaw(sender, MessageFormat.build(message));
    }

    public static boolean sendMessageRaw(CommandSender sender, String message) {
        if (message == null) {
            return false;
        }

        if (message.isEmpty()) {
            return false;
        }

        sender.sendMessage(message);
        return true;
    }
}
