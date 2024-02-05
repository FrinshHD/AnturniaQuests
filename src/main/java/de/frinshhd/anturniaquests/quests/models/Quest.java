package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.utils.*;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Quest {

    @JsonProperty
    private String friendlyName;

    @JsonProperty
    private String category;

    @JsonProperty
    private String description;

    @JsonProperty
    private String material;

    @JsonProperty
    private LinkedHashMap<String, Integer> requirements;

    @JsonProperty
    private Rewards rewards;

    @JsonProperty
    private boolean oneTime;

    @JsonProperty
    private boolean announce;

    public Quest() {
        this.requirements = new LinkedHashMap<>();
        this.rewards = new Rewards();
        this.oneTime = true;
        this.announce = false;
    }

    @JsonIgnore
    public String getFriendlyName() {
        return this.friendlyName;
    }

    @JsonIgnore
    public String getCategory() {
        return this.category;
    }

    @JsonIgnore
    public String getDescription() {
        return this.description;
    }

    @JsonIgnore
    public Material getMaterial() {
        if (this.material != null) {
            return Material.getMaterial(this.material);
        }

        for (Object o : getRequirements().keySet()) {
            if (!(o instanceof ItemStack)) {
                continue;
            }

            ItemStack item = (ItemStack) o;
            return item.getType();
        }

        Main.getQuestsManager().quests.remove(Main.getQuestsManager().getQuestID(this));
        return null;
    }

    @JsonIgnore
    public boolean isOneTimeUse() {
        return this.oneTime;
    }

    public Rewards getRewards() {
        return this.rewards;
    }

    public LinkedHashMap<Object, Integer> getRequirements() {
        LinkedHashMap<Object, Integer> map = new LinkedHashMap<>();

        requirements.forEach((k, v) -> {
            // Todo: do tagged items and coins also

            try {
                Material material1 = Material.valueOf(k);

                map.put(new ItemStack(material1), v);
            } catch (IllegalArgumentException e) {
                map.put(k, v);
            }
        });

        return map;
    }

    public ItemStack getItem(Player player, HashMap<String, Integer> finishedQuests) {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(Translator.build("inventory.quest.color") + getFriendlyName());

        ArrayList<String> lore = new ArrayList<>();
        lore.addAll(LoreBuilder.build(description, ChatColor.GRAY));
        lore.add(" ");

        if (isOneTimeUse() && finishedQuests.containsKey(Main.getQuestsManager().getQuestID(this))) {
            lore.add(Translator.build("lore.alreadyCompleted"));
        } else {
            lore.add(Translator.build("lore.requirements"));

            getRequirements().forEach((key, value) -> {
                if (key instanceof ItemStack) {
                    ItemStack requirement = (ItemStack) key;
                    int amount = 0;
                    for (ItemStack content : player.getInventory().getContents()) {
                        if (content == null) {
                            continue;
                        }

                        if (content.isSimilar(requirement)) {
                            amount += content.getAmount();
                        }
                    }
                    if (amount >= value) {
                        lore.add(Translator.build("lore.requirements.items.inInventory", new TranslatorPlaceholder("amountInInv", String.valueOf(amount)), new TranslatorPlaceholder("amount", value.toString()), new TranslatorPlaceholder("itemName", new TranslatableComponent(requirement.getType().getTranslationKey()).toPlainText())));
                    } else {
                        lore.add(Translator.build("lore.requirements.items.notInInventory", new TranslatorPlaceholder("amountInInv", String.valueOf(amount)), new TranslatorPlaceholder("amount", value.toString()), new TranslatorPlaceholder("itemName", new TranslatableComponent(requirement.getType().getTranslationKey()).toPlainText())));
                    }
                }
            });

            lore.add(" ");

            lore.add(Translator.build("lore.rewards"));

            //money
            if (getRewards().getMoney() > 0.0) {
                lore.add(Translator.build("lore.rewards.money", new TranslatorPlaceholder("amount", String.valueOf(getRewards().getMoney()))));
            }

            //items
            for (Item rewardItem : getRewards().getItems()) {
                lore.add(Translator.build("lore.rewards.item", new TranslatorPlaceholder("amount", String.valueOf(rewardItem.getAmount())), new TranslatorPlaceholder("itemName", rewardItem.getName())));
            }

            //commands
            for (Command command : getRewards().getCommands()) {
                lore.add(Translator.build("lore.rewards.commands", new TranslatorPlaceholder("name", command.getName())));
            }

            if (!isOneTimeUse()) {
                lore.add(" ");
                lore.add(Translator.build("quests.tip.useMultipleTimes"));
            }

        }

        itemMeta.setLore(lore);

        ItemTags.tagItemMeta(itemMeta, "quest_" + Main.getQuestsManager().getQuestID(this));

        item.setItemMeta(itemMeta);
        return item;
    }

    public void playerClick(Player player) throws SQLException {
        if (isOneTimeUse() && MysqlManager.getQuestPlayer(player.getUniqueId()).getFinishedQuests().containsKey(Main.getQuestsManager().getQuestID(this))) {
            // Todo: say player he has already this quest
            SurvivalQuestSounds.questError(player);
            return;
        }

        for (Map.Entry<Object, Integer> entry : getRequirements().entrySet()) {
            if (entry.getKey() instanceof ItemStack) {
                ItemStack requirementItem = (ItemStack) entry.getKey();
                if (!player.getInventory().containsAtLeast(requirementItem, entry.getValue())) {
                    // ToDo: tell player that he doesn't meet the requirements
                    SurvivalQuestSounds.questError(player);
                    return;
                }
            }
        }

        for (Map.Entry<Object, Integer> entry : getRequirements().entrySet()) {
            if (entry.getKey() instanceof ItemStack) {
                ItemStack requirementItem = (ItemStack) entry.getKey();
                int index = 0;
                while (entry.getValue() > index) {
                    player.getInventory().removeItem(requirementItem);
                    index++;
                }
            }
        }

        Dao<Quests, Long> questsDao = null;
        try {
            questsDao = MysqlManager.getQuestDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Quests quest = questsDao.queryForEq("uuid", player.getUniqueId()).stream().toList().get(0);


        quest.addFinishedQuest(Main.getQuestsManager().getQuestID(this));
        questsDao.update(quest);

        claim(player);
    }

    public void claim(Player player) {

        SurvivalQuestSounds.questComplete(player);

        //money
        if (getRewards().getMoney() > 0.0) {
            Main.getEconomy().depositPlayer(player, getRewards().getMoney());
        }

        //items
        for (Item rewardItem : getRewards().getItems()) {
            QuestsManager.addItem(player, rewardItem.getItem(), rewardItem.getAmount());
        }

        //commands
        for (Command command : getRewards().getCommands()) {
            String commandString = command.getCommand();

            if (commandString.charAt(0) == '/') {
                commandString = commandString.substring(1);
            }

            commandString = commandString.replace("%player%", player.getName());

            Main.getInstance().getServer().dispatchCommand(Main.getInstance().getServer().getConsoleSender(), commandString);
        }

        if (this.announce) {
            Bukkit.getOnlinePlayers().forEach(players -> {
                players.sendMessage(Translator.build("quest.announce", new TranslatorPlaceholder("player", player.getName()), new TranslatorPlaceholder("questName", getFriendlyName())));
            });
        }
    }
}
