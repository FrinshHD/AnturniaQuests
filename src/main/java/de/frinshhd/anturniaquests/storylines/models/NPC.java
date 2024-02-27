package de.frinshhd.anturniaquests.storylines.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.models.Quest;

import java.util.ArrayList;

public class NPC {
    @JsonProperty
    private String npcID = null;

    @JsonProperty
    private String friendlyName = null;

    @JsonProperty
    private String quest = null;

    @JsonProperty
    private int timeToComplete = -1;

    @JsonProperty
    private ArrayList<String> messages = new ArrayList<>();

    public String getNpcID() {
        return this.npcID;
    }

    public Quest getQuest() {
        if (Main.getQuestsManager().getQuest(this.quest) == null) {
            return null;
        }

        return Main.getQuestsManager().getQuest(this.quest);
    }

    public ArrayList<String> getMessages() {
        return this.messages;
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
