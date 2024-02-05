package de.frinshhd.anturniaquests;

import de.frinshhd.anturniaquests.utils.ItemTags;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {

    public static ItemStack getCategoriesForwardItem() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName("§7Forward §a➡");

        ItemTags.tagItemMeta(itemMeta, "categories_forward");

        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getCategoriesBackwardItem() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName("§c⬅ §7Back ");

        ItemTags.tagItemMeta(itemMeta, "categories_backward");

        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getQuestsForwardItem() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName("§7Forward §a➡");

        ItemTags.tagItemMeta(itemMeta, "quests_forward");

        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getQuestsBackwardItem() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName("§c⬅ §7Back ");

        ItemTags.tagItemMeta(itemMeta, "quests_backward");

        item.setItemMeta(itemMeta);
        return item;
    }
}
