package de.frinshhd.anturniaquests.requirements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.ResetType;

import java.util.LinkedHashMap;

public abstract class BasicRequirementModel {
    //resetType: NONE, COMPLETE, ONLY_AMOUNT
    @SerializedName("resetType")
    protected String resetType = ResetType.NONE.name();

    @Expose(serialize = false, deserialize = false)
    private String id = null;

    public BasicRequirementModel(LinkedHashMap<String, Object> map) {
    }

    public BasicRequirementModel(LinkedHashMap<String, Object> map, String id) {
        setId(id);

        if (map == null) {
            return;
        }

        if (map.containsKey("resetType")) {
            this.resetType = (String) map.get("resetType");
        }
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

    public ResetType getResetType() {
        Main.getInstance().getLogger().warning(this.resetType);

        try {
            return ResetType.valueOf(this.resetType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResetType.NONE;
        }
    }
}