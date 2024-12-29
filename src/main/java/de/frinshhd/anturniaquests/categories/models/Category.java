package de.frinshhd.anturniaquests.categories.models;

import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.ItemTags;
import de.frinshhd.anturniaquests.utils.LoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Category {

    @SerializedName("friendlyName")
    private String friendlyName = null;

    @SerializedName("description")
    private String description = null;

    @SerializedName("material")
    private String material = Material.STONE.toString();

    @SerializedName("dailyQuestsCategory")
    private boolean dailyQuestsCategory = false;

    @SerializedName("questsPerDay")
    private int questsPerDay = 3;

    @SerializedName("timesToReset")
    private ArrayList<String> timesToReset = new ArrayList<>(List.of("24:00"));

    public String getID() {
        return Main.getDynamicCategories().getCategoryID(this);
    }

    public String getFriendlyName() {
        if (this.friendlyName == null) {
            return getID();
        }

        return this.friendlyName;
    }

    public String getDescription() {
        return description;
    }

    public Material getMaterial() {
        return Material.getMaterial(this.material);
    }

    public ItemStack getItem(Player player) {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(ChatColor.DARK_GREEN + getFriendlyName());

        ArrayList<String> lore = new ArrayList<>();

        if (getDescription() != null) {
            lore.addAll(LoreBuilder.build(getDescription(), ChatColor.GRAY));
        }

        itemMeta.setLore(lore);

        ItemTags.tagItemMeta(itemMeta, "category_" + Main.getDynamicCategories().getCategoryID(this));

        item.setItemMeta(itemMeta);
        return item;
    }
}