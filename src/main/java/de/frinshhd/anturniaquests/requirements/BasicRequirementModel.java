package de.frinshhd.anturniaquests.requirements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class BasicRequirementModel {
    private String id = null;

    public BasicRequirementModel(LinkedHashMap<String, Object> map) {}

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public BasicRequirement getBasicRequirement() {
        return Main.getRequirementManager().getRequirement(getId());
    }
}
