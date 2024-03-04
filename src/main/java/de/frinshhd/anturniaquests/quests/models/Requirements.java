package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;

public class Requirements {

    @JsonProperty
    private ArrayList<Item> items = new ArrayList<>();

    @JsonProperty
    private ArrayList<KilledEntity> killedEntities = new ArrayList<>();


    public ArrayList<Item> getItems() {
        return this.items;
    }

    @JsonIgnore
    public ArrayList<KilledEntity> getKilledEntities() {
        return this.killedEntities;
    }

    public boolean checkItems(Player player) {
        for (Item item : getItems()) {
            System.out.println("hi " + item.getName());

            if (item.getName() != null) {
                int amountInInv = 0;

                for (ItemStack content : player.getInventory().getContents()) {
                    if (content == null || content.getType().equals(Material.AIR) || !content.getType().equals(item.getMaterial())) {
                        System.out.println(1);
                        continue;
                    }

                    if (!content.hasItemMeta() || !Objects.requireNonNull(content.getItemMeta()).hasDisplayName()) {
                        System.out.println(2);
                        continue;
                    }

                    System.out.println(content.getItemMeta().getDisplayName());

                    if (!Objects.requireNonNull(content.getItemMeta()).getDisplayName().equals(item.getName())) {
                        System.out.println(3);
                        continue;
                    }

                    amountInInv += content.getAmount();

                    if (amountInInv < item.getAmount()) {
                        System.out.println(4);
                        continue;
                    }

                    break;
                }

                if (amountInInv < item.getAmount()) {
                    System.out.println(5);
                    return false;
                }

                if (item.getLore().isEmpty()) {
                    System.out.println(6);
                    continue;
                }
            }

            if (!item.getLore().isEmpty()) {
                int amountInInv = 0;

                for (ItemStack content : player.getInventory().getContents()) {
                    if (content == null || content.getType().equals(Material.AIR) || !content.getType().equals(item.getMaterial())) {
                        continue;
                    }

                    if (!content.hasItemMeta() || !Objects.requireNonNull(content.getItemMeta()).hasLore()) {
                        continue;
                    }

                    if (!Objects.equals(content.getItemMeta().getLore(), item.getLore())) {
                        continue;
                    }

                    if (item.getName() != null) {
                        if (!content.getItemMeta().hasDisplayName()) {
                            continue;
                        }

                        if (!content.getItemMeta().getDisplayName().equals(item.getName())) {
                            continue;
                        }
                    }

                    amountInInv += content.getAmount();

                    if (amountInInv < item.getAmount()) {
                        continue;
                    }

                    break;
                }

                if (amountInInv < item.getAmount()) {
                    return false;
                }

                continue;
            }

            if (!player.getInventory().containsAtLeast(item.getItem(), item.getAmount())) {
                return false;
            }
        }

        return true;
    }

    public boolean checkKilledEntities(Player player) {
        for (KilledEntity killedEntity : killedEntities) {
            if (Main.getQuestsManager().getKilledEntityAmount(player, killedEntity.getEntity()) < killedEntity.getAmount()) {
                return false;
            }
        }

        return true;
    }

    public boolean check(Player player) {
        if (!checkItems(player)) {
            return false;
        }

        if (!checkKilledEntities(player)) {
            return false;
        }

        return true;
    }

    public void removeItems(Player player) {
        for (Item item : getItems()) {
            int index = 0;
            while (item.getAmount() > index) {
                player.getInventory().removeItem(item.getItem());
                index++;
            }
        }
    }
}
