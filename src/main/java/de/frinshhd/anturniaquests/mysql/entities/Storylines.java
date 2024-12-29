package de.frinshhd.anturniaquests.mysql.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.frinshhd.anturniaquests.Main;
import org.json.JSONObject;

import java.util.UUID;

@DatabaseTable(tableName = "Storylines")
public class Storylines {
    private static final Gson gson = Main.getGson();

    @DatabaseField(id = true)
    private UUID uuid;

    @DatabaseField
    private String storylines;

    public Storylines() {
    }

    public void create(UUID uuid) {
        this.uuid = uuid;
        storylines = new JSONObject().toString();
    }

    public UUID getUUID() {
        return uuid;
    }


    public void putStoryline(String storyline, JSONObject jsonObject) {
        JSONObject storylines;
        if (this.storylines == null || this.storylines.isEmpty() || this.storylines.equals("{}")) {
            storylines = new JSONObject();
        } else {
            storylines = new JSONObject(this.storylines);
        }

        storylines.put(storyline, jsonObject);
        this.storylines = storylines.toString();
    }

    public void putStorylines(JSONObject object) {
        this.storylines = object.toString();
    }

    public JSONObject getStoryline() {
        if (this.storylines == null || this.storylines.isEmpty() || this.storylines.equals("{}")) {
            return new JSONObject();
        }

        return new JSONObject(this.storylines);
    }
}