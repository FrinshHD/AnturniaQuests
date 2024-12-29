package de.frinshhd.anturniaquests.requirements.mobbreed;

import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.EntityType;

import java.util.LinkedHashMap;

public class MobBreedModel extends BasicRequirementModel {

    @SerializedName("entity")
    private EntityType entity = null;

    @SerializedName("amount")
    private int amount = 1;

    public MobBreedModel(LinkedHashMap<String, Object> map) {
        super(map, "mobBreed");

        if (map.containsKey("entity")) {
            this.entity = EntityType.valueOf((String) map.get("entity"));
        }

        if (map.containsKey("amount")) {
            this.amount = (int) map.get("amount");
        }
    }

    public EntityType getEntity() {
        return this.entity;
    }

    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    public String getName() {
        return new TranslatableComponent(getEntity().getTranslationKey()).toPlainText();
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}