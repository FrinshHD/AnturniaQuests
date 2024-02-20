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
    private Requirements requirements;

    @JsonProperty
    private Rewards rewards;

    @JsonProperty
    private boolean oneTime;

    @JsonProperty
    private boolean announce;

    public Quest() {
        this.requirements = new Requirements();
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

        if (!getRequirements().getItems().isEmpty()) {
            return getRequirements().getItems().get(0).getMaterial();
        }

        return Material.STONE;
    }

    @JsonIgnore
    public boolean isOneTimeUse() {
        return this.oneTime;
    }

    public Rewards getRewards() {
        return this.rewards;
    }

    public Requirements getRequirements() {
        return this.requirements;
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

            //items
            for (Item items : getRequirements().getItems()) {
                int amount = 0;
                for (ItemStack content : player.getInventory().getContents()) {
                    if (content == null) {
                        continue;
                    }

                    if (content.isSimilar(items.getItem())) {
                        amount += content.getAmount();
                    }
                }
                if (amount >= items.getAmount()) {
                    lore.add(Translator.build("lore.requirements.items.inInventory", new TranslatorPlaceholder("amountInInv", String.valueOf(amount)), new TranslatorPlaceholder("amount", String.valueOf(items.getAmount())), new TranslatorPlaceholder("itemName", items.getName())));
                } else {
                    lore.add(Translator.build("lore.requirements.items.notInInventory", new TranslatorPlaceholder("amountInInv", String.valueOf(amount)), new TranslatorPlaceholder("amount", String.valueOf(items.getAmount())), new TranslatorPlaceholder("itemName", items.getName())));
                }
            }

            //killedEntities
            for (KilledEntity killedEntity : getRequirements().getKilledEntities()) {
                Integer amount = Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntity.toString());
                if (amount == null) {
                    amount = 0;
                }

                if (amount >= killedEntity.getAmount()) {
                    lore.add(Translator.build("lore.requirements.killedEntities.fulfilled", new TranslatorPlaceholder("amountKilled", String.valueOf(amount)), new TranslatorPlaceholder("amount", String.valueOf(killedEntity.getAmount())), new TranslatorPlaceholder("entityName", killedEntity.getName())));
                } else {
                    lore.add(Translator.build("lore.requirements.killedEntities.notFulfilled", new TranslatorPlaceholder("amountKilled", String.valueOf(amount)), new TranslatorPlaceholder("amount", String.valueOf(killedEntity.getAmount())), new TranslatorPlaceholder("entityName", killedEntity.getName())));
                }
            }

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



        if (!getRequirements().check(player)) {
            //Todo: tell player that he doesn't meet the requirements
            SurvivalQuestSounds.questError(player);
            return;
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
