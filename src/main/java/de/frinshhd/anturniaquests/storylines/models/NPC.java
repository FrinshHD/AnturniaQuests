package de.frinshhd.anturniaquests.storylines.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class NPC {
    @JsonProperty
    private String npcID = null;

    @JsonProperty
    private String friendlyName = null;

    @JsonProperty
    private int timeToComplete = -1;

    @JsonProperty
    private ArrayList<NPCAction> actions = new ArrayList<>();

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
}
