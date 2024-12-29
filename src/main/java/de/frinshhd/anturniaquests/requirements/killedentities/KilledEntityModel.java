package de.frinshhd.anturniaquests.requirements.killedentities;

import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

public class KilledEntityModel extends BasicRequirementModel {

    @SerializedName("entity")
    private EntityType entity = null;

    @SerializedName("amount")
    private int amount = 1;

    public KilledEntityModel(LinkedHashMap<String, Object> map) {
        super(map, "killedEntities");
        if (map.containsKey("entity")) {
            this.entity = EntityType.valueOf((String) map.get("entity"));
        }

        if (map.containsKey("amount")) {
            this.amount = (int) map.get("amount");
        }
    }

    public String getName() {
        return new TranslatableComponent(getEntity().getTranslationKey()).toPlainText();
    }

    public EntityType getEntity() {
        if (entity == null) {
            return EntityType.UNKNOWN;
        }

        return this.entity;
    }

    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}