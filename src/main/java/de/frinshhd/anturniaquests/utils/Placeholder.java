package de.frinshhd.anturniaquests.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Placeholder {

    public static ItemStack Placeholder() {
        ItemStack Placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta PlaceholderMeta = Placeholder.getItemMeta();

        PlaceholderMeta.setDisplayName(" ");
        ArrayList<String> LoreList = new ArrayList<String>();
        LoreList.clear();

        PlaceholderMeta.setLore(LoreList);
        PlaceholderMeta.removeItemFlags();

        ItemTags.tagItemMeta(PlaceholderMeta, "placeholder");

        Placeholder.setItemMeta(PlaceholderMeta);

        return Placeholder;
    }

    public static ItemStack PlaceholderLight() {
        ItemStack Placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta PlaceholderMeta = Placeholder.getItemMeta();

        PlaceholderMeta.setDisplayName(" ");
        ArrayList<String> LoreList = new ArrayList<String>();
        LoreList.clear();

        PlaceholderMeta.setLore(LoreList);
        PlaceholderMeta.removeItemFlags();

        ItemTags.tagItemMeta(PlaceholderMeta, "placeholder");

        Placeholder.setItemMeta(PlaceholderMeta);

        return Placeholder;
    }

}
