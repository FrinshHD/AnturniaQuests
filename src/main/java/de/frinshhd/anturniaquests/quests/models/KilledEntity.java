package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.entity.EntityType;

public class KilledEntity {

    @JsonProperty
    private EntityType entity = EntityType.UNKNOWN;

    @JsonProperty
    private int amount = 1;

    @JsonIgnore
    public String getName() {
        return new TranslatableComponent(getEntity().getTranslationKey()).toPlainText();
    }

    @JsonIgnore
    public EntityType getEntity() {
        return this.entity;
    }

    @JsonIgnore
    public int getAmount() {
        return this.amount;
    }
}
