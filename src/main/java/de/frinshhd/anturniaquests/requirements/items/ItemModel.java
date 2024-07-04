package de.frinshhd.anturniaquests.requirements.items;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;

public class ItemModel extends BasicRequirementModel {

    @JsonProperty
    private String name = null;

    @JsonProperty
    private int amount = 1;

    @JsonProperty
    private Material material = Material.AIR;

    @JsonProperty
    private ArrayList<String> lore = new ArrayList<>();


    public ItemModel() {
        super(null, null);
    }

    @JsonIgnore
    public ItemModel(LinkedHashMap<String, Object> map) {
        super(map, "items");

        if (map.containsKey("name")) {
            this.name = (String) map.get("name");
        }

        if (map.containsKey("amount")) {
            this.amount = (int) map.get("amount");
        }

        if (map.containsKey("material")) {
            this.material = Material.valueOf((String) map.get("material"));
        }

        if (map.containsKey("lore")) {
            String loreRaw = (String) map.get("lore");
            loreRaw = loreRaw.substring(1, loreRaw.length() - 1);
            ArrayList<String> lore = new ArrayList<String>(Arrays.asList(loreRaw.split(",")));
            this.lore = lore;
        }
    }

    @JsonIgnore
    public String getName() {
        if (this.name != null) {
            return this.name;
        }

        return null;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
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
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @JsonIgnore
    public Material getMaterial() {
        return this.material;
    }

    @JsonIgnore
    public void setMaterial(Material material) {
        this.material = material;
    }

    @JsonIgnore
    public ArrayList<String> getLore() {
        return this.lore;
    }

    @JsonIgnore
    public void setLore(ArrayList<String> lore) {
        this.lore = lore;
    }

    @JsonIgnore
    public ItemStack getItem() {
        return new ItemStack(getMaterial());
    }

    @JsonIgnore
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

    @JsonIgnore
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

            itemStack.setAmount(itemStack.getAmount() - 1);

            index++;
        }
    }

    @JsonIgnore
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
