package de.frinshhd.anturniaquests.categories.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.ItemTags;
import de.frinshhd.anturniaquests.utils.LoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Category {

    @JsonProperty
    private String friendlyName;

    @JsonProperty
    private String description;

    @JsonProperty
    private String material;

    public String getID() {
        return Main.getDynamicCategories().getCategoryID(this);
    }

    public String getFriendlyName() {
        return this.friendlyName;
    }

    public Material getMaterial() {
        return Material.getMaterial(this.material);
    }

    public ItemStack getItem(Player player) {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(ChatColor.DARK_GREEN + getFriendlyName());

        ArrayList<String> lore = new ArrayList<>();
        lore.addAll(LoreBuilder.build(description, ChatColor.GRAY));

        itemMeta.setLore(lore);

        ItemTags.tagItemMeta(itemMeta, "category_" + Main.getDynamicCategories().getCategoryID(this));

        item.setItemMeta(itemMeta);
        return item;
    }
}
