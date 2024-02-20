package de.frinshhd.anturniaquests.storylines.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.quests.models.Quest;

import java.util.ArrayList;

public class NPC {

    @JsonProperty
    private String id = null;

    @JsonProperty
    private String npcID = null;

    @JsonProperty
    private Quest quest = null;

    @JsonProperty
    private ArrayList<String> messages = new ArrayList<>();

    public String getNpcID() {
        return this.npcID;
    }

    public Quest getQuest() {
        return this.quest;
    }

    public ArrayList<String> getMessages() {
        return this.messages;
    }

}
