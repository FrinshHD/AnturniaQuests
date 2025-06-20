package de.frinshhd.anturniaquests.requirements.permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;

import java.util.LinkedHashMap;

public class PermissionModel extends BasicRequirementModel {

    @JsonProperty
    private String permission = "";

    @JsonIgnore
    public PermissionModel(LinkedHashMap<String, Object> map) {
        super(map, "permissions");

        if (map.containsKey("permission")) {
            this.permission = (String) map.get("permission");
        }
    }

    @JsonIgnore
    public String getPermission() {
        return this.permission;
    }

    @JsonIgnore
    public void setPermission(String permission) {
        this.permission = permission;
    }
}
