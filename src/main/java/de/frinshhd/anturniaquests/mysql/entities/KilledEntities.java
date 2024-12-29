package de.frinshhd.anturniaquests.mysql.entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.frinshhd.anturniaquests.Main;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@DatabaseTable(tableName = "KilledEntities")
public class KilledEntities {
    private static final Gson gson = Main.getGson();

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
        Type type = new TypeToken<HashMap<String, Integer>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    public String hashMapToString(Map<String, Integer> map) {
        return gson.toJson(map);
    }

    public void clearKilledEntities() {
        killedEntities = hashMapToString(new HashMap<>());
    }
}