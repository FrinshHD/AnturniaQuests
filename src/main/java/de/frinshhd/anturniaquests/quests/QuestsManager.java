package de.frinshhd.anturniaquests.quests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.KilledEntities;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.PlayerHashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class QuestsManager {

    public LinkedHashMap<String, Quest> quests;
    public PlayerHashMap<UUID, HashMap<String, Integer>> playerKilledEntities;

    public QuestsManager() {
        quests = new LinkedHashMap<>();
        playerKilledEntities = new PlayerHashMap<>();

        this.load();
    }

    public static boolean addItem(Player player, ItemStack item, int amount) {
        int index = 0;
        while (amount > index) {
            player.getInventory().addItem(item);
            index++;
        }

        return true;
    }

    /**
     * Search and register quests
     */
    public void load() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        TypeFactory typeFactory = mapper.getTypeFactory();
        MapType mapTypeQuests = typeFactory.constructMapType(LinkedHashMap.class, String.class, Quest.class);

        try {
            this.quests = mapper.readValue(new FileInputStream("plugins/AnturniaQuests/quests.yml"), mapTypeQuests);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading config.yml. AnturniaQuests will be disabled!\nError " + e.getMessage());
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return;
        }
    }

    public Quest getQuest(String questID) {
        return this.quests.get(questID);
    }

    public String getQuestID(Quest quest) {

        for (Map.Entry<String, Quest> stringQuestEntry : quests.entrySet()) {
            if (stringQuestEntry.getValue().equals(quest)) {
                return stringQuestEntry.getKey();
            }
        }

        return null;
    }

    public void addKilledEntity(Player player, EntityType killedEntity) {
        UUID uuid = player.getUniqueId();

        if (!playerKilledEntities.containsKey(uuid)) {
            playerKilledEntities.put(uuid, new HashMap<>());
        }

        HashMap<String, Integer> killedEntities = playerKilledEntities.get(uuid);
        assert killedEntities != null;

        if (!killedEntities.containsKey(killedEntity.toString())) {
            killedEntities.put(killedEntity.toString(), 1);
        } else {
            killedEntities.put(killedEntity.toString(), killedEntities.get(killedEntity.toString()) + 1);
        }
        playerKilledEntities.put(uuid, killedEntities);
    }

    public int getKilledEntityAmount(Player player, EntityType entityType) {
        HashMap<String, Integer> map = playerKilledEntities.get(player.getUniqueId());

        if (!map.containsKey(entityType.toString())) {
            return 0;
        }

        return map.get(entityType.toString());
    }

    public void playerJoin(Player player) {
        putPlayerKilledEntitiesToMap(player);
    }

    public void playerQuit(Player player) {
        savePlayerKilledEntitiesToDB(player);
    }

    public void putPlayerKilledEntitiesToMap(Player player) {
        KilledEntities killedEntities = MysqlManager.getKilledEntitiesPlayer(player.getUniqueId());
        assert killedEntities != null;

        Main.getQuestsManager().playerKilledEntities.put(player.getUniqueId(), killedEntities.getKilledEntities());
    }

    public void savePlayerKilledEntitiesToDB(Player player) {
        Dao<KilledEntities, Long> killedEntitiesDao = null;
        try {
            killedEntitiesDao = MysqlManager.getKilledEntityDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        KilledEntities killedEntities = null;
        try {
            killedEntities = killedEntitiesDao.queryForEq("uuid", player.getUniqueId()).stream().toList().get(0);

            HashMap<String, Integer> map = Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId());

            for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
                killedEntities.putKilledEntity(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
            }


            killedEntitiesDao.update(killedEntities);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPlayerQuestCompletions(Player player, String questID) {
        return getPlayerQuestCompletions(player.getUniqueId(), questID);
    }

    public int getPlayerQuestCompletions(UUID playerUUID, String questID) {
        Quests playerQuest = MysqlManager.getQuestPlayer(playerUUID);

        if (playerQuest == null) {
            return 0;
        }

        if (!playerQuest.getFinishedQuests().containsKey(questID)) {
            return 0;
        }

        return playerQuest.getFinishedQuests().get(questID);
    }

    public int getQuestsCompletedCounter(UUID playerUUID) {
        Quests playerQuest = MysqlManager.getQuestPlayer(playerUUID);

        if (playerQuest == null) {
            return 0;
        }

        int counter = 0;
        for (Integer value : playerQuest.getFinishedQuests().values()) {
            counter += value;
        }

        return counter;
    }

    public int getKilledEntitesCounter(UUID playerUUID) {
        KilledEntities killedEntities = MysqlManager.getKilledEntitiesPlayer(playerUUID);

        if (killedEntities == null) {
            return 0;
        }

        int counter = 0;
        for (Integer value : killedEntities.getKilledEntities().values()) {
            counter += value;
        }

        return counter;
    }
}
