package de.frinshhd.anturniaquests.mysql.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.frinshhd.anturniaquests.Main;
import org.json.JSONObject;

import java.util.UUID;

@DatabaseTable(tableName = "Requirements")
public class Requirements {
    private static final Gson gson = Main.getGson();

    @DatabaseField(id = true)
    private UUID uuid;

    @DatabaseField
    private String requirements;

    public Requirements() {
    }

    public void create(UUID uuid) {
        this.uuid = uuid;
        requirements = new JSONObject().toString();
    }

    public UUID getUUID() {
        return uuid;
    }


    public void putStoryline(String storyline, JSONObject jsonObject) {
        JSONObject requirements;
        if (this.requirements == null || this.requirements.isEmpty() || this.requirements.equals("{}")) {
            requirements = new JSONObject();
        } else {
            requirements = new JSONObject(this.requirements);
        }

        requirements.put(storyline, jsonObject);
        this.requirements = requirements.toString();
    }

    public void putStorylines(JSONObject object) {
        this.requirements = object.toString();
    }

    public JSONObject getStoryline() {
        if (this.requirements == null || this.requirements.isEmpty() || this.requirements.equals("{}")) {
            return new JSONObject();
        }

        return new JSONObject(this.requirements);
    }
}