package de.frinshhd.anturniaquests.mysql.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@DatabaseTable(tableName = "KilledEntities")
public class KilledEntities {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @DatabaseField(id = true)
    private UUID uuid;

    @DatabaseField
    private String killedEntities;

    public KilledEntities() {
    }

    public void create(UUID uuid) {
        this.uuid = uuid;
        killedEntities = hashMapToString(new HashMap<>());
    }

    public UUID getUUID() {
        return uuid;
    }


    public void putKilledEntity(String killedEntity, int amount) {
        HashMap<String, Integer> killedEntities;
        if (this.killedEntities == null || this.killedEntities.isEmpty() || this.killedEntities.equals("{}")) {
            killedEntities = new HashMap<>();
        } else {
            killedEntities = (HashMap<String, Integer>) stringToHashMap(this.killedEntities);
        }

        if (killedEntities.containsKey(killedEntity)) {
            killedEntities.put(killedEntity, amount);
            this.killedEntities = hashMapToString(killedEntities);
            return;
        }

        killedEntities.put(killedEntity, amount);
        this.killedEntities = hashMapToString(killedEntities);
    }

    public HashMap<String, Integer> getKilledEntities() {
        if (this.killedEntities == null || this.killedEntities.equals("{}") || this.killedEntities.isEmpty()) {
            return new HashMap<>();
        }

        return (HashMap<String, Integer>) stringToHashMap(this.killedEntities);
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
