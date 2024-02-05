package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.utils.MessageFormat;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Item {

    @JsonProperty
    private String name = null;

    @JsonProperty
    private int amount = 1;

    @JsonProperty
    private Material material = Material.AIR;

    @JsonProperty
    private List<String> nbtTags = new ArrayList<>();

    @JsonIgnore
    public String getName() {
        if (this.name != null) {
            return this.name;
        }

        return new TranslatableComponent(getMaterial().getTranslationKey()).toPlainText();
    }

    @JsonIgnore
    public int getAmount() {
        return this.amount;
    }

    @JsonIgnore
    public Material getMaterial() {
        return this.material;
    }

    @JsonIgnore
    public List<String> getNbtTags() {
        return this.nbtTags;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(getMaterial());

        ItemMeta itemMeta = item.getItemMeta();

        if (name != null) {
            itemMeta.setDisplayName(MessageFormat.build(getName()));
        }

        item.setItemMeta(itemMeta);
        return item;
    }
}
