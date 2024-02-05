package de.frinshhd.anturniaquests.mysql.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@DatabaseTable(tableName = "Quests")
public class Quests {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @DatabaseField(id = true)
    private UUID uuid;

    @DatabaseField
    private String quests;

    public Quests() {
    }

    public void create(UUID uuid) {
        this.uuid = uuid;
        quests = hashMapToString(new HashMap<String, Integer>());
    }

    public UUID getUUID() {
        return uuid;
    }

    public void addFinishedQuest(String questID) {
        HashMap<String, Integer> quests;
        if (this.quests == null || this.quests.isEmpty() || this.quests.equals("{}")) {
            quests = new HashMap<>();
        } else {
            quests = (HashMap<String, Integer>) stringToHashMap(this.quests);
        }

        if (quests.containsKey(questID)) {
            quests.put(questID, quests.get(questID) + 1);
            this.quests = hashMapToString(quests);
            return;
        }

        quests.put(questID, 1);
        this.quests = hashMapToString(quests);
    }

    public HashMap<String, Integer> getFinishedQuests() {
        return (HashMap<String, Integer>) stringToHashMap(quests);
    }

    public Map<String, Integer> stringToHashMap(String jsonString) {
        Map<String, Integer> resultMap = null;
        try {
            resultMap = objectMapper.readValue(jsonString, new TypeReference<HashMap<String, Integer>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    public String hashMapToString(Map<String, Integer> map) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
