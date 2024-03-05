package de.frinshhd.anturniaquests.utils;

import de.frinshhd.anturniaquests.Main;
import org.bukkit.ChatColor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

public class Translator {

    public static Properties messages;

    public static void register(String path) throws IOException {
        messages = new Properties();

        //load standard configuration
        try (InputStreamReader isr = new InputStreamReader(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream("messages.properties")), StandardCharsets.UTF_8)) {
            messages.load(isr);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading messages.properties. AnturniaQuests will be disabled!");
        }

        //load probably modified file
        try (FileInputStream fis = new FileInputStream(path);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            messages.load(isr);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading messages.properties. AnturniaQuests will be disabled!");
        }

        messages.store(new FileOutputStream(path), null);
    }

    public static String build(String messageKey, TranslatorPlaceholder... translatorPlaceholders) {
        return MessageFormat.build(buildRaw(messageKey, translatorPlaceholders));
    }

    public static String buildRaw(String messageKey, TranslatorPlaceholder... translatorPlaceholders) {
        if (!messages.containsKey(messageKey)) {
            return messageKey;
        }

        String message = messages.get(messageKey).toString();

        if (message.isEmpty()) {
            return message;
        }

        for (TranslatorPlaceholder translatorPlaceholder : translatorPlaceholders) {
            if (translatorPlaceholder.key == null) {
                continue;
            }
            if (translatorPlaceholder.value == null) {
                continue;
            }

            message = message.replace("%" + translatorPlaceholder.key + "%", translatorPlaceholder.value);
        }

        return message;
    }

}

