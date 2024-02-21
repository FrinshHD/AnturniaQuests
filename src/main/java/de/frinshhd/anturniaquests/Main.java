package de.frinshhd.anturniaquests;

import de.frinshhd.anturniaquests.categories.CategoriesManager;
import de.frinshhd.anturniaquests.commands.QuestCommand;
import de.frinshhd.anturniaquests.config.ConfigManager;
import de.frinshhd.anturniaquests.menusystem.PlayerMenuUtility;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.storylines.StorylinesManager;
import de.frinshhd.anturniaquests.storylines.listener.CitizensNpcsListener;
import de.frinshhd.anturniaquests.storylines.listener.FancyNpcsListener;
import de.frinshhd.anturniaquests.utils.SpigotMCCommunication;
import de.frinshhd.anturniaquests.utils.Translator;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

public final class Main extends JavaPlugin {

    //Todo: implement storylinesEnabled bool
    private static boolean storylinesEnabled;

    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();
    public static String version;
    private static Main INSTANCE;
    private static Economy econ = null;
    private static QuestsManager questsManager;
    private static ConfigManager configManager;
    private static CategoriesManager categoriesManager;
    private static StorylinesManager storylinesManager;
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
        getConfigManager().load();
        getQuestsManager().load();
        getDynamicCategories().load();
        getStorylinesManager().load();
        try {
            Translator.register("plugins/AnturniaQuests/messages.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }
        }

        setupEconomy();

        INSTANCE = this;

        //check if a npc plugin is installed
        if (Main.getInstance().getServer().getPluginManager().getPlugin("Citizens") != null && Main.getInstance().getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
            storylinesEnabled = true;
        } else if (Main.getInstance().getServer().getPluginManager().getPlugin("FancyNpcs") != null && Main.getInstance().getServer().getPluginManager().getPlugin("FancyNpcs").isEnabled()) {
            storylinesEnabled = true;
        } else  {
            storylinesEnabled = false;
        }


        SpigotMCCommunication.init();

        int pluginId = 20180;
        Metrics metrics = new Metrics(this, pluginId);

        // register messages
        try {
            Translator.register("plugins/AnturniaQuests/messages.properties");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Find plugin class names for dynamic loading
        String fullCanonicalName = Main.getInstance().getClass().getCanonicalName();
        String canonicalName = fullCanonicalName.substring(0, fullCanonicalName.lastIndexOf("."));

        Reflections reflections = new Reflections(canonicalName, new SubTypesScanner(false));
        Set<String> classNames = reflections.getAll(new SubTypesScanner(false));

        Iterator<String> classNamesIterator = classNames.iterator();

        questsManager = new QuestsManager();

        categoriesManager = new CategoriesManager();

        configManager = new ConfigManager();

        if (isStorylinesEnabled()) {
            storylinesManager = new StorylinesManager();
        }

        DynamicListeners.load(classNamesIterator, canonicalName);

        this.registerCommands();

        //run playerJoinEvent for all online players in case of server reload
        if (!getServer().getOnlinePlayers().isEmpty()) {
            getServer().getOnlinePlayers().forEach(player -> {
                getServer().getPluginManager().callEvent(new PlayerJoinEvent(player, ""));
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
        return econ != null;
    }

    @Override
    public void onDisable() {
        if (!getServer().getOnlinePlayers().isEmpty()) {
            getServer().getOnlinePlayers().forEach(player -> {
                getServer().getPluginManager().callEvent(new PlayerQuitEvent(player, ""));
            });
        }
    }

    public void registerCommands() {
        Objects.requireNonNull(Bukkit.getPluginCommand("quests")).setExecutor(new QuestCommand());
    }
}
