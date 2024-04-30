package de.frinshhd.anturniaquests.requirements.killedentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

public class KilledEntityModel extends BasicRequirementModel {

    @JsonProperty
    private EntityType entity = null;

    @JsonProperty
    private int amount = 1;

    @JsonIgnore
    public KilledEntityModel(LinkedHashMap<String, Object> map) {
        super(map, "killedEntities");
        if (map.containsKey("entity")) {
            this.entity = EntityType.valueOf((String) map.get("entity"));
        }

        if (map.containsKey("amount")) {
            this.amount = (int) map.get("amount");
        }
    }

    @JsonIgnore
    public String getName() {
        return new TranslatableComponent(getEntity().getTranslationKey()).toPlainText();
    }

    @JsonIgnore
    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    @JsonIgnore
    public EntityType getEntity() {
        if (entity == null) {
            return EntityType.UNKNOWN;
        }

        return this.entity;
    }

    @JsonIgnore
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @JsonIgnore
    public int getAmount() {
        return this.amount;
    }
}
