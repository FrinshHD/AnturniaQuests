package de.frinshhd.anturniaquests.storylines.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Storyline {

    @JsonProperty
    private int cooldown = -1;

    @JsonProperty
    private int maxCompletions = -1;

    @JsonProperty
    private int timeToComplete = -1;

    @JsonProperty
    private ArrayList<NPC> npcs = new ArrayList<>();

    public ArrayList<NPC> getNpcs() {
        return this.npcs;
    }

    public NPC getNPCStageID(int stageID) {
        for (NPC npc : npcs) {
            if (getNpcs().indexOf(npc) == stageID) {
                return npc;
            }
        }

        return null;
    }

    public NPC getNPC(String npcID) {
        for (NPC npc : getNpcs()) {
            if (npc.getNpcID().equals(npcID)) {
                return npc;
            }
        }

        return null;
    }

    public long getCooldown() {
        if (cooldown == -1) {
            return -1;
        }

        return this.cooldown * 1000L;
    }

    public int getMaxCompletions() {
        return this.maxCompletions;
    }

    public long getTimeToComplete() {
        if (timeToComplete == -1) {
            return -1;
        }

        return this.timeToComplete * 1000L;
    }
}
