package de.frinshhd.anturniaquests.utils;

import org.bukkit.ChatColor;

public class MessageFormat {

    public static String build(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

}

