package de.frinshhd.anturniaquests.utils;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

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

        List<TranslatorPlaceholder> placeholders = new ArrayList<>(List.of(
                new TranslatorPlaceholder("player", sender.getName())
        ));

        for (TranslatorPlaceholder placeholder : placeholders) {
            if (placeholder.key == null) {
                continue;
            }
            if (placeholder.value == null) {
                continue;
            }

            message = message.replace("%" + placeholder.key + "%", placeholder.value);
        }

        sender.sendMessage(message);
        return true;
    }
}
