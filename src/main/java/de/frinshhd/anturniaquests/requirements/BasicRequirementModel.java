package de.frinshhd.anturniaquests.requirements;

import de.frinshhd.anturniaquests.Main;

import java.util.LinkedHashMap;

public abstract class BasicRequirementModel {
    private String id = null;

    public BasicRequirementModel(LinkedHashMap<String, Object> map) {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BasicRequirement getBasicRequirement() {
        return Main.getRequirementManager().getRequirement(getId());
    }
}
