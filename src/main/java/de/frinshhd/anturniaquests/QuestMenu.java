package de.frinshhd.anturniaquests;

import de.frinshhd.anturniaquests.categories.models.Category;
import de.frinshhd.anturniaquests.menusystem.Menu;
import de.frinshhd.anturniaquests.menusystem.PlayerMenuUtility;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ItemTags;
import de.frinshhd.anturniaquests.utils.Placeholder;
import de.frinshhd.anturniaquests.utils.Sounds;
import de.frinshhd.anturniaquests.utils.Translator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static de.frinshhd.anturniaquests.Main.getConfigManager;

public class QuestMenu extends Menu {

    private final Category category;
    private final int categoriesPage;
    private final int questsPage;


    public QuestMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);

        this.category = Main.getDynamicCategories().getCategory(Main.getDynamicCategories().categories.keySet().stream().toList().get(0));
        this.questsPage = 0;

        int index = 0;
        for (Category c : Main.getDynamicCategories().categories.values()) {

            if (c.equals(category)) {
                break;
            }

            index++;
        }

        double pageNotUse = (double) index / 7.0;
        this.categoriesPage = (int) Math.floor(pageNotUse);
    }

    public QuestMenu(PlayerMenuUtility playerMenuUtility, String categoryID) {
        super(playerMenuUtility);

        this.category = Main.getDynamicCategories().getCategory(categoryID);
        this.questsPage = 0;

        int index = 0;
        for (Category c : Main.getDynamicCategories().categories.values()) {

            if (c.equals(category)) {
                break;
            }

            index++;
        }

        double pageNotUse = (double) index / 7.0;
        this.categoriesPage = (int) Math.floor(pageNotUse);
    }

    public QuestMenu(PlayerMenuUtility playerMenuUtility, String categoryID, int questsPage) {
        super(playerMenuUtility);

        this.category = Main.getDynamicCategories().getCategory(categoryID);
        this.questsPage = questsPage;

        int index = 0;
        for (Category c : Main.getDynamicCategories().categories.values()) {

            if (c.equals(category)) {
                break;
            }

            index++;
        }

        double pageNotUse = (double) index / 7.0;
        this.categoriesPage = (int) Math.floor(pageNotUse);
    }

    public QuestMenu(PlayerMenuUtility playerMenuUtility, String categoryID, int questsPage, int categoriesPage) {
        super(playerMenuUtility);

        this.category = Main.getDynamicCategories().getCategory(categoryID);
        this.questsPage = questsPage;
        this.categoriesPage = categoriesPage;
    }


    @Override
    public String getMenuName() {
        return Translator.build("inventory.heading.color") + category.getFriendlyName() + " Quests";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();

        if (event.getClickedInventory() == null) {
            return;
        } else if (!event.getClickedInventory().equals(this.inventory)) {
            return;
        } else if (event.getCurrentItem() == null) {
            return;
        }

        ItemMeta itemMeta = item.getItemMeta();

        Material material = item.getType();

        if (material.equals(Material.AIR) || ItemTags.extractItemId(itemMeta).equals("placeholder")) {
            return;
        }

        // Sounds.itemClick(player);

        if (item.equals(Items.getQuestsBackwardItem())) {
            Sounds.itemClick(player);
            new QuestMenu(Main.getPlayerMenuUtility(player), category.getID(), questsPage - 1).open(player);
            return;
        }

        if (item.equals(Items.getQuestsForwardItem())) {
            Sounds.itemClick(player);
            new QuestMenu(Main.getPlayerMenuUtility(player), category.getID(), questsPage + 1).open(player);
            return;
        }

        if (item.equals(Items.getCategoriesBackwardItem())) {
            Sounds.itemClick(player);
            new QuestMenu(Main.getPlayerMenuUtility(player), category.getID(), questsPage, categoriesPage - 1).open(player);
            return;
        }

        if (item.equals(Items.getCategoriesForwardItem())) {
            Sounds.itemClick(player);
            new QuestMenu(Main.getPlayerMenuUtility(player), category.getID(), questsPage, categoriesPage + 1).open(player);
            return;
        }

        String itemId = ItemTags.extractItemId(itemMeta);

        if (itemId == null) {
            return;
        }

        if (itemId.contains("category_")) {
            String categoryID = itemId.substring(9);

            Sounds.itemClick(player);
            new QuestMenu(Main.getPlayerMenuUtility(player), categoryID).open(player);
            return;
        }

        if (itemId.contains("quest_")) {
            String questID = itemId.substring(6);

            try {
                Main.getQuestsManager().getQuest(questID).playerClick(player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            new QuestMenu(Main.getPlayerMenuUtility(player), category.getID(), questsPage, categoriesPage).open(player);
        }
    }

    @Override
    public void setMenuItems(Player player) {
        // Fills Inventory so it looks better
        // left
        {
            int index = 0;
            while (index < 54) {
                inventory.setItem(index, Placeholder.Placeholder());
                index += 9;
            }
        }

        //right
        {
            int index = 8;
            while (index < 54) {
                inventory.setItem(index, Placeholder.Placeholder());
                index += 9;
            }
        }

        // placeholder line
        {
            int index = 9;
            while (index < 18) {
                inventory.setItem(index, Placeholder.Placeholder());
                index++;
            }
        }

        // add categories
        {
            ArrayList<Category> categories = new ArrayList<>(Main.getDynamicCategories().categories.values());

            int index = this.categoriesPage * 7;
            int position = 0;

            while (position < 7) {
                try {
                    Category category = categories.get(index);
                    ItemStack item = category.getItem(player);

                    if (this.category.equals(category)) {
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.addEnchant(Enchantment.MENDING, 1, true);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        item.setItemMeta(itemMeta);

                        ItemStack placeholder = Placeholder.Placeholder();
                        ItemMeta placeholderMeta = item.getItemMeta();
                        placeholderMeta.addEnchant(Enchantment.MENDING, 1, true);
                        placeholderMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        placeholder.setItemMeta(placeholderMeta);

                        inventory.setItem(position + 10, placeholder);
                    }

                    inventory.addItem(item);
                } catch (IndexOutOfBoundsException e) {
                    break;
                }

                position++;
                index++;
            }

            if (this.categoriesPage > 0) {
                inventory.setItem(getConfigManager().getConfig().getQuestMenu().getBackwardItem().getSlot(), ItemTags.tagItem(getConfigManager().getConfig().getQuestMenu().getBackwardItem().getItem(), "categories_backward"));
            }

            if (categories.size() > this.categoriesPage * 7 + 7 - 1) {
                inventory.setItem(getConfigManager().getConfig().getQuestMenu().getForwardItem().getSlot(), ItemTags.tagItem(getConfigManager().getConfig().getQuestMenu().getForwardItem().getItem(), "categories_forward"));
            }
        }

        // quests
        {
            ArrayList<Quest> quests = new ArrayList<>();
            Main.getQuestsManager().quests.values().forEach(quest -> {
                if (quest.getCategory() != null && quest.getCategory().equals(this.category.getID())) {
                    quests.add(quest);
                }
            });

            int index = this.questsPage * 28;
            int position = 0;
            HashMap<String, Integer> finishedQuestsFormated = null;
            finishedQuestsFormated = MysqlManager.getQuestPlayer(player.getUniqueId()).getFinishedQuests();

            while (position < 28) {

                int x = position % 7;

                int slot;

                if (position >= 21) {
                    slot = 46 + x;
                } else if (position >= 14) {
                    slot = 37 + x;
                } else if (position >= 7) {
                    slot = 28 + x;
                } else {
                    slot = 19 + x;
                }

                try {
                    Quest quest = quests.get(index);
                    inventory.setItem(slot, quest.getItem(player, finishedQuestsFormated));
                } catch (IndexOutOfBoundsException e) {
                    break;
                }

                position++;
                index++;
            }

            if (this.questsPage != 0) {
                inventory.setItem(45, Items.getQuestsBackwardItem());
            }

            if (quests.size() > this.questsPage * 28 + 28 - 1) {
                inventory.setItem(53, Items.getQuestsForwardItem());
            }
        }

    }


}
