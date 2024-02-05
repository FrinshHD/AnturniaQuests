package de.frinshhd.anturniaquests.quests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.frinshhd.anturniaquests.quests.models.Quest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuestsManager {

    public LinkedHashMap<String, Quest> quests;

    public QuestsManager() {
        quests = new LinkedHashMap<>();

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
}
