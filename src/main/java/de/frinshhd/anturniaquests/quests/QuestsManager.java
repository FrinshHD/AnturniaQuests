package de.frinshhd.anturniaquests.quests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.PlayerHashMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.FileInputStream;
import java.io.IOException;
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
            throw new RuntimeException(e);
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

        System.out.println(playerKilledEntities.get(uuid).keySet().toString());
    }

    public int getKilledEntityAmount(Player player, EntityType entityType) {
        HashMap<String, Integer> map = playerKilledEntities.get(player.getUniqueId());

        if (!map.containsKey(entityType.toString())) {
            return 0;
        }

        return map.get(entityType.toString());
    }
}
