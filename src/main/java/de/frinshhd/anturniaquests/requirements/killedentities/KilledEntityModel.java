package de.frinshhd.anturniaquests.requirements.killedentities;

import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

public class KilledEntityModel extends BasicRequirementModel {

    private EntityType entity = EntityType.UNKNOWN;

    private int amount = 1;

    public KilledEntityModel(LinkedHashMap<String, Object> map) {
        super(map);
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
        return this.entity;
    }

    public int getAmount() {
        return this.amount;
    }
}
