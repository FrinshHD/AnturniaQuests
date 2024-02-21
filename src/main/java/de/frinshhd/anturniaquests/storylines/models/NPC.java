package de.frinshhd.anturniaquests.storylines.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.models.Quest;

import java.util.ArrayList;

public class NPC {
    @JsonProperty
    private String npcID = null;

    @JsonProperty
    private String quest = null;

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

}
