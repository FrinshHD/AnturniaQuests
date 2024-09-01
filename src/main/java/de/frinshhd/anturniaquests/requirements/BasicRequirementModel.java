package de.frinshhd.anturniaquests.requirements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.ResetType;

import java.util.LinkedHashMap;

public abstract class BasicRequirementModel {
    @JsonIgnore
    private String id = null;

    //resetType: NONE, COMPLETE, ONLY_AMOUNT
    @JsonProperty
    protected String resetType = ResetType.NONE.name();

    @JsonIgnore
    public BasicRequirementModel(LinkedHashMap<String, Object> map) {
    }

    @JsonIgnore
    public BasicRequirementModel(LinkedHashMap<String, Object> map, String id) {
        setId(id);

        if (map == null) {
            return;
        }

        if (map.containsKey("resetType")) {
            this.resetType = (String) map.get("resetType");
        }
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

    @JsonIgnore
    public ResetType getResetType() {
        Main.getInstance().getLogger().warning(this.resetType);

        try {
            return ResetType.valueOf(this.resetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResetType.NONE;
        }
    }
}
