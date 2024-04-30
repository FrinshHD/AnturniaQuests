package de.frinshhd.anturniaquests.requirements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.frinshhd.anturniaquests.Main;

import java.util.LinkedHashMap;

public abstract class BasicRequirementModel {
    @JsonIgnore
    private String id = null;

    @JsonIgnore
    public BasicRequirementModel(LinkedHashMap<String, Object> map) {
    }

    @JsonIgnore
    public BasicRequirementModel(LinkedHashMap<String, Object> map, String id) {
        setId(id);
    }

    @JsonIgnore
    public String getId() {
        return this.id;
    }

    @JsonIgnore
    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public BasicRequirement getBasicRequirement() {
        return Main.getRequirementManager().getRequirement(getId());
    }
}
