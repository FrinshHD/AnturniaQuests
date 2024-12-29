package de.frinshhd.anturniaquests;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.frinshhd.anturniaquests.categories.CategoriesManager;
import de.frinshhd.anturniaquests.commands.CommandManager;
import de.frinshhd.anturniaquests.config.ConfigManager;
import de.frinshhd.anturniaquests.menusystem.PlayerMenuUtility;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.requirements.RequirementManager;
import de.frinshhd.anturniaquests.storylines.StorylinesManager;
import de.frinshhd.anturniaquests.utils.DynamicListeners;
import de.frinshhd.anturniaquests.utils.DynamicPlaceholderExpansion;
import de.frinshhd.anturniaquests.utils.SpigotMCCommunication;
import de.frinshhd.anturniaquests.utils.Translator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

public final class Main extends JavaPlugin {

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    public static String version;

    private static boolean storylinesEnabled;
    private static Main INSTANCE;
    private static Economy econ = null;
    private static QuestsManager questsManager;
    private static ConfigManager configManager;
    private static CategoriesManager categoriesManager;
    private static StorylinesManager storylinesManager;
    private static RequirementManager requirementManager;
    private static CommandManager commandManager;

    public static Main getInstance() {
        return INSTANCE;
    }

    public static QuestsManager getQuestsManager() {
        return questsManager;
    }

    public static CategoriesManager getDynamicCategories() {
        return categoriesManager;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }

    public static RequirementManager getRequirementManager() {
        return requirementManager;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static StorylinesManager getStorylinesManager() {
        return storylinesManager;
    }

    public static boolean isStorylinesEnabled() {
        return storylinesEnabled;
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) { //See if the player has a playermenuutility "saved" for them

            //This player doesn't. Make one for them add add it to the hashmap
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p); //Return the object by using the provided player
        }
    }

    public static void reload() {
        Main.getConfigManager().load();
        Main.getQuestsManager().load();
        Main.getDynamicCategories().load();

        if (isStorylinesEnabled()) {
            Main.getStorylinesManager().load();
        }
        try {
            Translator.register("plugins/AnturniaQuests/messages.properties");
        } catch (IOException e) {
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading config.yml. AnturniaQuests will be disabled!\nError " + e.getMessage());
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
        }
    }

    @Override
    public void onEnable() {
        //create files
        new File("plugins/AnturniaQuests").mkdir();

        List<String> files = new ArrayList<>();
        files.addAll(List.of("categories.yml", "config.yml", "quests.yml", "storylines.yml", "messages.properties"));

        for (String fileRaw : files) {
            File file = new File("plugins/AnturniaQuests/" + fileRaw);
            if (file.exists()) {
                continue;
            }

            InputStream link = (getResource(fileRaw));
            try {
                Files.copy(link, file.getAbsoluteFile().toPath());
            } catch (IOException e) {
                Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading " + fileRaw + ". AnturniaQuests will be disabled!\nError " + e.getMessage());
                Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
                return;
            }
        }

        setupEconomy();

        INSTANCE = this;


        SpigotMCCommunication.init();

        int pluginId = 20180;
        Metrics metrics = new Metrics(this, pluginId);

        // register messages
        try {
            Translator.register("plugins/AnturniaQuests/messages.properties");
        } catch (IOException e) {
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading messages.properties. AnturniaQuests will be disabled!\nError " + e.getMessage());
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return;
        }

        // Find plugin class names for dynamic loading
        String fullCanonicalName = Main.getInstance().getClass().getCanonicalName();
        String canonicalName = fullCanonicalName.substring(0, fullCanonicalName.lastIndexOf("."));

        Reflections reflections = new Reflections(canonicalName, new SubTypesScanner(false));
        Set<String> classNames = reflections.getAll(new SubTypesScanner(false));

        configManager = new ConfigManager();

        requirementManager = new RequirementManager(true);

        questsManager = new QuestsManager();

        categoriesManager = new CategoriesManager();

        //check if a npc plugin is installed
        if (!Main.getConfigManager().getConfig().storylinesEnabled) {
            storylinesEnabled = false;
        } else {
            if (Main.getInstance().getServer().getPluginManager().getPlugin("Citizens") != null && Main.getInstance().getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
                storylinesEnabled = true;
            } else
                storylinesEnabled = Main.getInstance().getServer().getPluginManager().getPlugin("FancyNpcs") != null && Main.getInstance().getServer().getPluginManager().getPlugin("FancyNpcs").isEnabled();
        }

        if (isStorylinesEnabled()) {
            storylinesManager = new StorylinesManager();
        }

        commandManager = new CommandManager();
        commandManager.load(classNames, canonicalName);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null && Main.getInstance().getServer().getPluginManager().getPlugin("PlaceholderAPI").isEnabled()) {
            DynamicPlaceholderExpansion.load(classNames, canonicalName);
        }

        configManager.connectToDB();

        DynamicListeners.load(classNames, canonicalName);

        //run playerJoinLogic for all online players in case of server reload
        if (!getServer().getOnlinePlayers().isEmpty()) {
            getServer().getOnlinePlayers().forEach(player -> {
                getRequirementManager().playerJoin(player);
                getQuestsManager().playerJoin(player);

                if (storylinesEnabled) {
                    getStorylinesManager().playerJoin(player);
                }
            });
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    @Override
    public void onDisable() {
        //run player quit logic on server reload
        if (!getServer().getOnlinePlayers().isEmpty()) {
            getServer().getOnlinePlayers().forEach(player -> {
                getQuestsManager().playerQuit(player);
                getRequirementManager().playerQuit(player);

                if (storylinesEnabled) {
                    getStorylinesManager().playerQuit(player);
                }
            });
        }
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>() {
                    @Override
                    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        if (json.getAsJsonPrimitive().isNumber()) {
                            return json.getAsInt();  // Ensure it's an int
                        }
                        return null;
                    }
                })
                .create();

    }

    public static Yaml getYaml() {
        LoaderOptions loaderOptions = new LoaderOptions();

        // Custom Constructor with Map.class as the root type and LoaderOptions
        Constructor customConstructor = new Constructor(Map.class, loaderOptions) {
            @Override
            protected Object constructObject(Node node) {
                if (node instanceof ScalarNode scalarNode) {
                    String value = scalarNode.getValue();
                    // Handle integers
                    if (scalarNode.getTag().equals(Tag.INT)) {
                        try {
                            return Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid integer: " + value, e);
                        }
                    }
                    // Handle floats
                    if (scalarNode.getTag().equals(Tag.FLOAT)) {
                        try {
                            return Double.parseDouble(value);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid float: " + value, e);
                        }
                    }
                }
                return super.constructObject(node);
            }
        };

        // Custom Yaml instance
        return new Yaml(customConstructor);
    }
}
