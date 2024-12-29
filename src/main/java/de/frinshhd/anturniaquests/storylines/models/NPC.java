package de.frinshhd.anturniaquests.storylines.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NPC {
    @SerializedName("npcID")
    private String npcID = null;

    @SerializedName("friendlyName")
    private String friendlyName = null;

    @SerializedName("timeToComplete")
    private int timeToComplete = -1;

    @SerializedName("actions")
    private ArrayList<NPCAction> actions = new ArrayList<>();

    @SerializedName("group")
    private String group = null;

    public String getNpcID() {
        return this.npcID;
    }

    public ArrayList<NPCAction> getActions() {
        return this.actions;
    }

    public long getTimeToComplete() {
        if (timeToComplete == -1) {
            return -1;
        }

        return timeToComplete * 1000L;
    }

    public String getName() {
        if (this.friendlyName == null) {
            return npcID;
        }

        return this.friendlyName;
    }

    public String getGroup() {
        return group;
    }
}