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

    @DatabaseField
    private String cooldowns;

    public Quests() {
    }

    public void create(UUID uuid) {
        this.uuid = uuid;
        quests = hashMapToString(new HashMap<String, Integer>());
        cooldowns = hashMapToStringLong(new HashMap<String, Long>());
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

    public void setQuest(String questID, int completions) {
        HashMap<String, Integer> quests;
        if (this.quests == null || this.quests.isEmpty() || this.quests.equals("{}")) {
            quests = new HashMap<>();
        } else {
            quests = (HashMap<String, Integer>) stringToHashMap(this.quests);
        }

        if (quests.containsKey(questID)) {
            quests.put(questID, completions);
            this.quests = hashMapToString(quests);
            return;
        }

        quests.put(questID, completions);
        this.quests = hashMapToString(quests);
    }

    public void resetQuests() {
        this.quests = hashMapToString(new HashMap<String, Integer>());
    }

    public void putCooldown(String questID, long lastCompletion) {
        HashMap<String, Long> cooldown;
        if (this.cooldowns == null || this.cooldowns.isEmpty() || this.cooldowns.equals("{}")) {
            cooldown = new HashMap<>();
        } else {
            cooldown = (HashMap<String, Long>) stringToHashMapLong(this.cooldowns);
        }

        cooldown.put(questID, lastCompletion);
        this.cooldowns = hashMapToStringLong(cooldown);
    }

    public HashMap<String, Integer> getFinishedQuests() {
        return (HashMap<String, Integer>) stringToHashMap(quests);
    }

    public HashMap<String, Long> getCooldown() {
        return (HashMap<String, Long>) stringToHashMapLong(this.cooldowns);
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

    public Map<String, Long> stringToHashMapLong(String jsonString) {
        Map<String, Long> resultMap = null;
        try {
            resultMap = objectMapper.readValue(jsonString, new TypeReference<HashMap<String, Long>>() {
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

    public String hashMapToStringLong(Map<String, Long> map) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }
}
