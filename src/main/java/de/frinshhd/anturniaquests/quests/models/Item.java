package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class Item {

    @JsonProperty("name")
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
    public ArrayList<String> getLore() {
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

                String displayName = content.getItemMeta().getDisplayName();
                displayName = displayName.replace('§', '&');

                if (!displayName.equals(getName())) {
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

                ArrayList<String> loreRaw = new ArrayList<>(Objects.requireNonNull(content.getItemMeta().getLore()));
                ArrayList<String> lore = new ArrayList<>();
                loreRaw.forEach(string -> {
                    string = string.replace('§', '&');
                    lore.add(string);
                });

                System.out.println(lore);
                System.out.println(getLore());

                if (!lore.equals(getLore())) {
                    continue;
                }

                System.out.println(name);

                if (getName() != null) {
                    if (!content.getItemMeta().hasDisplayName()) {
                        continue;
                    }

                    String displayName = content.getItemMeta().getDisplayName();
                    displayName = displayName.replace('§', '&');

                    System.out.println(displayName);
                    System.out.println(getName());

                    if (!displayName.equals(getName())) {
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

                String displayName = content.getItemMeta().getDisplayName();
                displayName = displayName.replace('§', '&');

                if (!displayName.equals(getName())) {
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

                ArrayList<String> loreRaw = new ArrayList<>(Objects.requireNonNull(content.getItemMeta().getLore()));
                ArrayList<String> lore = new ArrayList<>();
                loreRaw.forEach(string -> {
                    string = string.replace('§', '&');
                    lore.add(string);
                });

                if (!lore.equals(getLore())) {
                    continue;
                }

                if (getName() != null) {
                    if (!content.getItemMeta().hasDisplayName()) {
                        continue;
                    }

                    String displayName = content.getItemMeta().getDisplayName();
                    displayName = displayName.replace('§', '&');

                    if (!displayName.equals(getName())) {
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
