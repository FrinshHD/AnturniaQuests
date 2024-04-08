package de.frinshhd.anturniaquests.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoreBuilder {

    public static List<String> build(String string, ChatColor color) {
        List<String> lines = new ArrayList<>();

        final int maxLenght = 36;

        String[] parts = string.split("\n");

        for (String part : parts) {
            int index = 0;
            int lastLineBreak = 0;

            while (part.length() > index) {
                char c = part.charAt(index);

                if (c == ' ' && index - lastLineBreak > maxLenght) {
                    if (color != null) {
                        lines.add(color + part.substring(lastLineBreak, index));
                    } else {
                        lines.add(part.substring(lastLineBreak, index));
                    }
                    lastLineBreak = index + 1;
                }

                index++;
            }

            if (color != null) {
                lines.add(color + part.substring(lastLineBreak));
            } else {
                lines.add(part.substring(lastLineBreak));
            }
        }

        return lines;
    }

    public static List<String> build(String string) {
        return build(string, null);
    }

    public static List<String> buildSimple(String string) {
        String[] parts = string.split("\n");

        return Arrays.stream(parts).toList();
    }

}
