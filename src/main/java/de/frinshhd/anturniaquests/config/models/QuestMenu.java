package de.frinshhd.anturniaquests.config.models;

import com.google.gson.annotations.SerializedName;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.items.ItemModel;
import org.bukkit.Material;

import java.util.Objects;

public class QuestMenu {

    @SerializedName("enabled")
    private boolean enabled = true;

    @SerializedName("forwardItem")
    private ItemModel forwardItem = new ItemModel();

    @SerializedName("backwardItem")
    private ItemModel backwardItem = new ItemModel();

    public boolean isEnabled() {
        return Objects.requireNonNullElseGet(Main.getConfigManager().getConfig().questMenuEnabled, () -> enabled);
    }

    public ItemModel getForwardItem() {
        ItemModel forwardItem = this.forwardItem;

        if (forwardItem.getSlot() == -1) {
            forwardItem.setSlot(8);
        }

        if (forwardItem.getMaterial() == Material.AIR) {
            forwardItem.setMaterial(Material.GREEN_STAINED_GLASS_PANE);
        }

        if (forwardItem.getName() == null) {
            forwardItem.setName("&7Forward &a➡");
        }

        return forwardItem;
    }

    public ItemModel getBackwardItem() {
        ItemModel backwardItem = this.backwardItem;

        if (backwardItem.getSlot() == -1) {
            backwardItem.setSlot(0);
        }

        if (backwardItem.getMaterial() == Material.AIR) {
            backwardItem.setMaterial(Material.RED_STAINED_GLASS_PANE);
        }

        if (backwardItem.getName() == null) {
            backwardItem.setName("&c⬅ &7Back");
        }

        return backwardItem;
    }
}