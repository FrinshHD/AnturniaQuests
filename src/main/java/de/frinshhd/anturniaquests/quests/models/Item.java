package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.MessageFormat;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Item {

    @JsonProperty
    private String name = null;

    @JsonProperty
    private int amount = 1;

    @JsonProperty
    private Material material = Material.AIR;

    @JsonProperty
    private ArrayList<String> lore = new ArrayList<>();

    @JsonIgnore
    public String getName() {
        if (this.name != null) {
            return this.name;
        }

        return null;
    }

    public String getDisplayName() {
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
    public ArrayList<String> getLore(){
        return this.lore;
    }

    public ItemStack getItem() {
        return new ItemStack(getMaterial());
    }

    public int getAmountInInventory(Player player) {
        if (getName() != null && getLore().isEmpty()) {
            int amountInInv = 0;

            for (ItemStack content : player.getInventory().getContents()) {
                if (content == null || content.getType().equals(Material.AIR) || !content.getType().equals(getMaterial())) {
                    continue;
                }

                if (!content.hasItemMeta() || !Objects.requireNonNull(content.getItemMeta()).hasDisplayName()) {
                    continue;
                }

                if (!Objects.requireNonNull(content.getItemMeta()).getDisplayName().equals(getName())) {
                    continue;
                }

                amountInInv += content.getAmount();
            }

            return amountInInv;
        }

        if (!getLore().isEmpty()) {
            int amountInInv = 0;

            for (ItemStack content : player.getInventory().getContents()) {
                if (content == null || content.getType().equals(Material.AIR) || !content.getType().equals(getMaterial())) {
                    continue;
                }

                if (!content.hasItemMeta() || !Objects.requireNonNull(content.getItemMeta()).hasLore()) {
                    continue;
                }

                if (!Objects.equals(content.getItemMeta().getLore(), getLore())) {
                    continue;
                }

                if (getName() != null) {
                    if (!content.getItemMeta().hasDisplayName()) {
                        continue;
                    }

                    if (!content.getItemMeta().getDisplayName().equals(getName())) {
                        continue;
                    }
                }

                amountInInv += content.getAmount();
            }

            return amountInInv;
        }

        int amountInInv = 0;

        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null) {
                continue;
            }

            if (content.isSimilar(getItem())) {
                amountInInv += content.getAmount();
            }
        }

        return amountInInv;
    }

    public void removeItemFromInventory(Player player) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();

        if (getName() != null && getLore().isEmpty()) {
            for (ItemStack content : player.getInventory().getContents()) {
                if (content == null || content.getType().equals(Material.AIR) || !content.getType().equals(getMaterial())) {
                    continue;
                }

                if (!content.hasItemMeta() || !Objects.requireNonNull(content.getItemMeta()).hasDisplayName()) {
                    continue;
                }

                if (!Objects.requireNonNull(content.getItemMeta()).getDisplayName().equals(getName())) {
                    continue;
                }

                if (!itemStacks.contains(content)) {
                    itemStacks.add(content);
                }
            }
        } else if (!getLore().isEmpty()) {
            for (ItemStack content : player.getInventory().getContents()) {
                if (content == null || content.getType().equals(Material.AIR) || !content.getType().equals(getMaterial())) {
                    continue;
                }

                if (!content.hasItemMeta() || !Objects.requireNonNull(content.getItemMeta()).hasLore()) {
                    continue;
                }

                if (!Objects.equals(content.getItemMeta().getLore(), getLore())) {
                    continue;
                }

                if (getName() != null) {
                    if (!content.getItemMeta().hasDisplayName()) {
                        continue;
                    }

                    if (!content.getItemMeta().getDisplayName().equals(getName())) {
                        continue;
                    }
                }

                if (!itemStacks.contains(content)) {
                    itemStacks.add(content);
                }
            }
        } else {
            for (ItemStack content : player.getInventory().getContents()) {
                if (content == null) {
                    continue;
                }

                if (content.isSimilar(getItem())) {
                    if (!itemStacks.contains(content)) {
                        itemStacks.add(content);
                    }
                }
            }
        }


        int index = 0;
        while (getAmount() > index) {
            Main.getInstance().getLogger().info(index + getDisplayName());
            ItemStack itemStack = getFittingItemStack(itemStacks, 1);

            if (itemStack.getAmount() == 1) {
                player.getInventory().remove(itemStack);
            } else {
                itemStack.setAmount(itemStack.getAmount() - 1);
            }

            index++;
        }
    }

    private ItemStack getFittingItemStack(ArrayList<ItemStack> items, int minAmount) {
        ItemStack itemStack = null;

        for (ItemStack item : items) {
            if (item.getAmount() > minAmount - 1) {
                itemStack = item;
                break;
            }
        }

        return itemStack;
    }
}
