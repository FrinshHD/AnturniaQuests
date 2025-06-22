package de.frinshhd.anturniaquests.utils.translations;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.MessageFormat;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TranslationManager {

    private static final TranslationManager INSTANCE = new TranslationManager();

    public static TranslationManager getInstance() {
        return INSTANCE;
    }

    private final File languagesDir;
    private final String defaultLang;
    private final Map<String, Map<String, String>> translations = new HashMap<>();
    private String lang;

    public TranslationManager() {
        this(new File(Main.getInstance().getDataFolder(), "languages"), "en");
    }

    public TranslationManager(File languagesDir, String defaultLang) {
        this.languagesDir = languagesDir;
        this.defaultLang = defaultLang;
        this.lang = defaultLang;

        ensureDefaultLanguageFile();
        loadLanguages();
        setLanguage(Main.getConfigManager().getConfig().getLanguage());
    }

    private void ensureDefaultLanguageFile() {
        if (!languagesDir.exists()) {
            languagesDir.mkdirs();
        }

        File defaultFile = new File(languagesDir, defaultLang + ".yml");
        if (!defaultFile.exists()) {
            try (InputStream input = Main.getInstance().getResource("languages/" + defaultLang + ".yml")) {
                if (input != null) {
                    try (OutputStream output = new FileOutputStream(defaultFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = input.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }
                    }
                }
            } catch (IOException e) {
                Main.getInstance().getLogger().severe("Failed to copy default language file: " + e.getMessage());
            }
        }
    }

    private void loadLanguages() {
        File[] files = languagesDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String langKey = file.getName().substring(0, file.getName().length() - 4); // remove .yml
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Map<String, String> map = new HashMap<>();
                Set<String> keys = config.getKeys(true);
                for (String key : keys) {
                    String value = config.getString(key);
                    if (value != null) {
                        map.put(key, value);
                    }
                }
                translations.put(langKey, map);
            }
        }
    }

    public void reload() {
        translations.clear();
        loadLanguages();
        setLanguage(Main.getConfigManager().getConfig().getLanguage());
    }

    public String build(String key, Translatable... placeholders) {
        return MessageFormat.build(get(key, placeholders));
    }

    public String get(String key, Translatable... placeholders) {
        String message = null;

        Map<String, String> currentLangMap = translations.get(lang);
        if (currentLangMap != null) {
            message = currentLangMap.get(key);
        }

        if (message == null) {
            Map<String, String> defaultLangMap = translations.get(defaultLang);
            if (defaultLangMap != null) {
                message = defaultLangMap.get(key);
            }
        }

        if (message == null) {
            try (InputStream input = Main.getInstance().getResource("languages/" + defaultLang + ".yml")) {
                if (input != null) {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(input));
                    message = config.getString(key);
                }
            } catch (IOException e) {
                Main.getInstance().getLogger().severe("Failed to load default language file: " + e.getMessage());
            }
        }

        if (message == null) {
            message = "%" + key + "%";
        }

        return replacePlaceholders(message, placeholders);
    }

    public void send(CommandSender sender, String key, Translatable... placeholders) {
        sender.sendMessage(build(key, placeholders));
    }

    private String replacePlaceholders(String message, Translatable... placeholders) {
        String result = message;
        for (Translatable placeholder : placeholders) {
            result = result.replace("%" + placeholder.key() + "%", placeholder.value());
        }
        return result;
    }

    public void setLanguage(String lang) {
        if (translations.containsKey(lang)) {
            this.lang = lang;
        } else {
            Main.getInstance().getLogger().info("Language '" + lang + "' not found, using default '" + defaultLang + "'.");
            this.lang = defaultLang;
        }
    }
}
