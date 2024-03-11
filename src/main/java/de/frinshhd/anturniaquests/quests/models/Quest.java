package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Quest {

    @JsonProperty
    private String friendlyName = "";

    @JsonProperty
    private String category = null;

    @JsonProperty
    private String description = "";

    @JsonProperty
    private String material = null;

    @JsonProperty
    private Requirements requirements = new Requirements();

    @JsonProperty
    private Rewards rewards = new Rewards();

    @JsonProperty
    private boolean oneTime = false;

    @JsonProperty
    private boolean announce = false;

    @JsonProperty
    private Integer cooldown = null;

    @JsonProperty
    private Boolean showCompletions = null;

    @JsonProperty
    private ArrayList<String> requiredQuests = new ArrayList<>();

    @JsonProperty
    private Sound completionSound = null;

    @JsonProperty
    private Sound errorSound = null;

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

    public Long getCooldown() {
        if (this.cooldown == null) {
            return null;
        }

        return this.cooldown * 1000L;
    }

    public boolean isShowCompletions() {
        if (showCompletions == null) {
            return !isOneTimeUse();
        }

        return showCompletions;
    }

    public ArrayList<Quest> getRequiredQuests() {
        ArrayList<Quest> quests = new ArrayList<>();

        for (String requiredQuest : this.requiredQuests) {
            Quest quest = Main.getQuestsManager().getQuest(requiredQuest);

            if (quest != null) {
                quests.add(quest);
            }
        }

        return quests;
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
        lore.addAll(LoreBuilder.build(getDescription(), ChatColor.GRAY));
        lore.add(" ");

        if (isOneTimeUse() && finishedQuests.containsKey(Main.getQuestsManager().getQuestID(this))) {
            lore.add(Translator.build("lore.alreadyCompleted"));
        } else if (getCooldown() != null &&
                MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().containsKey(Main.getQuestsManager().getQuestID(this)) &&
                MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().get(Main.getQuestsManager().getQuestID(this)) + getCooldown() >= System.currentTimeMillis()) {
            lore.addAll(LoreBuilder.build(Translator.build("lore.cooldown", new TranslatorPlaceholder("cooldown", String.valueOf((MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().get(Main.getQuestsManager().getQuestID(this)) + getCooldown() - System.currentTimeMillis()) / 1000))), ChatColor.GRAY));

            if (isShowCompletions()) {
                lore.add(" ");
                lore.add(Translator.build("lore.completions", new TranslatorPlaceholder("completions", String.valueOf(Main.getQuestsManager().getPlayerQuestCompletions(player, Main.getQuestsManager().getQuestID(this))))));
            }
        } else if (!checkCanCompleteQuest(player)) {
            lore.addAll(LoreBuilder.build(Translator.build("lore.requiredQuests"), ChatColor.RED));

            for (Quest quest : getQuestsToCompletePlayer(player)) {
                lore.add(Translator.build("lore.requiredQuests.quest", new TranslatorPlaceholder("questName", quest.getFriendlyName())));
            }
        } else {
            lore.add(Translator.build("lore.requirements"));

            //items
            for (Item items : getRequirements().getItems()) {
                int amountInInv = items.getAmountInInventory(player);

                if (amountInInv >= items.getAmount()) {
                    lore.add(Translator.build("lore.requirements.items.inInventory", new TranslatorPlaceholder("amountInInv", String.valueOf(amountInInv)), new TranslatorPlaceholder("amount", String.valueOf(items.getAmount())), new TranslatorPlaceholder("itemName", items.getDisplayName())));
                } else {
                    lore.add(Translator.build("lore.requirements.items.notInInventory", new TranslatorPlaceholder("amountInInv", String.valueOf(amountInInv)), new TranslatorPlaceholder("amount", String.valueOf(items.getAmount())), new TranslatorPlaceholder("itemName", items.getDisplayName())));
                }
            }

            //killedEntities
            for (KilledEntity killedEntity : getRequirements().getKilledEntities()) {
                int amount;

                if (Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()) == null) {
                    amount = 0;
                } else if (Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntity.getEntity().toString()) == null) {
                    amount = 0;
                } else {
                    amount = Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntity.getEntity().toString());
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
                lore.add(Translator.build("lore.rewards.item", new TranslatorPlaceholder("amount", String.valueOf(rewardItem.getAmount())), new TranslatorPlaceholder("itemName", rewardItem.getDisplayName())));
            }

            //commands
            for (Command command : getRewards().getCommands()) {
                lore.add(Translator.build("lore.rewards.commands", new TranslatorPlaceholder("name", command.getName())));
            }

            if (isShowCompletions()) {
                lore.add(" ");
                lore.add(Translator.build("lore.completions", new TranslatorPlaceholder("completions", String.valueOf(Main.getQuestsManager().getPlayerQuestCompletions(player, Main.getQuestsManager().getQuestID(this))))));
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

    public boolean checkCanCompleteQuest(Player player) {
        if (getQuestsToCompletePlayer(player).isEmpty()) {
            return true;
        }

        return false;
    }

    public ArrayList<Quest> getQuestsToCompletePlayer(Player player) {
        ArrayList<Quest> quests = new ArrayList<>();

        Quests playerQuests = MysqlManager.getQuestPlayer(player.getUniqueId());

        if (playerQuests == null) {
            return getRequiredQuests();
        }

        for (Quest requiredQuest : getRequiredQuests()) {
            String questID = Main.getQuestsManager().getQuestID(requiredQuest);

            if (!playerQuests.getFinishedQuests().containsKey(questID) || playerQuests.getFinishedQuests().get(questID) < 1) {
                quests.add(requiredQuest);
            }
        }

        return quests;
    }

    public void playerClick(Player player) throws SQLException {
        playerClick(player, false);
    }

    public boolean playerClick(Player player, boolean message) throws SQLException {
        if (isOneTimeUse() && MysqlManager.getQuestPlayer(player.getUniqueId()).getFinishedQuests().containsKey(Main.getQuestsManager().getQuestID(this))) {

            if (message) {
                ChatManager.sendMessage(player, Translator.build("quest.alreadyCompleted"));
            }

            SurvivalQuestSounds.questError(player);
            return false;
        }

        //check if player has a cooldown for this quest
        if (getCooldown() != null &&
                MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().containsKey(Main.getQuestsManager().getQuestID(this)) &&
                MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().get(Main.getQuestsManager().getQuestID(this)) + getCooldown() >= System.currentTimeMillis()) {

            if (message) {
                ChatManager.sendMessage(player, Translator.build("quest.cooldown", new TranslatorPlaceholder("cooldown", String.valueOf((MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().get(Main.getQuestsManager().getQuestID(this)) + getCooldown() - System.currentTimeMillis()) / 1000))));
            }

            SurvivalQuestSounds.questError(player);
            return false;
        }


        //check if player needs to complete other quests before he can complete this one
        if (!checkCanCompleteQuest(player)) {

            if (message) {
                ChatManager.sendMessage(player, Translator.build("quest.requiredQuests"));

                for (Quest quest : getQuestsToCompletePlayer(player)) {
                    ChatManager.sendMessage(player, Translator.build("quest.requiredQuests.quest", new TranslatorPlaceholder("questName", quest.getFriendlyName())));
                }
            }

            SurvivalQuestSounds.questError(player);
            return false;
        }

        if (!getRequirements().check(player)) {
            if (message) {
                ChatManager.sendMessage(player, Translator.build("quest.missingRequirements"));

                //items
                for (Item items : getRequirements().getItems()) {
                    int amountInInv = items.getAmountInInventory(player);

                    if (amountInInv < items.getAmount()) {
                        ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.item", new TranslatorPlaceholder("amountInInv", String.valueOf(amountInInv)), new TranslatorPlaceholder("amount", String.valueOf(items.getAmount())), new TranslatorPlaceholder("itemName", items.getDisplayName())));
                    }
                }

                //killedEntities
                for (KilledEntity killedEntity : getRequirements().getKilledEntities()) {
                    int amount;

                    if (Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntity.getEntity().toString()) == null) {
                        amount = 0;
                    } else {
                        amount = Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntity.getEntity().toString());
                    }

                    if (amount < killedEntity.getAmount()) {
                        ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.killedEntity", new TranslatorPlaceholder("amountKilled", String.valueOf(amount)), new TranslatorPlaceholder("amount", String.valueOf(killedEntity.getAmount())), new TranslatorPlaceholder("entityName", killedEntity.getName())));
                    }
                }
            }
            SurvivalQuestSounds.questError(player);
            return false;
        }

        getRequirements().removeItems(player);

        Dao<Quests, Long> questsDao;
        try {
            questsDao = MysqlManager.getQuestDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Quests quest = questsDao.queryForEq("uuid", player.getUniqueId()).stream().toList().get(0);


        quest.addFinishedQuest(Main.getQuestsManager().getQuestID(this));

        if (getCooldown() != null) {
            quest.putCooldown(Main.getQuestsManager().getQuestID(this), System.currentTimeMillis());
        }

        questsDao.update(quest);

        claim(player, message);

        return true;
    }

    public void claim(Player player, boolean message) {

        if (message) {
            ChatManager.sendMessage(player, Translator.build("quest.complete", new TranslatorPlaceholder("questName", getFriendlyName())));
        }

        SurvivalQuestSounds.questComplete(player);

        //money
        if (getRewards().getMoney() > 0.0) {
            if (Main.getEconomy() != null) {
                Main.getEconomy().depositPlayer(player, getRewards().getMoney());

                if (message) {
                    ChatManager.sendMessage(player, Translator.build("quest.addMoney", new TranslatorPlaceholder("amount", String.valueOf(getRewards().getMoney()))));
                }
            }
        }

        //items
        for (Item rewardItem : getRewards().getItems()) {
            QuestsManager.addItem(player, rewardItem.getItem(), rewardItem.getAmount());

            if (message) {
                ChatManager.sendMessage(player, Translator.build("quest.addItem", new TranslatorPlaceholder("amount", String.valueOf(rewardItem.getAmount())), new TranslatorPlaceholder("itemName", rewardItem.getDisplayName())));
            }
        }

        //commands
        for (Command command : getRewards().getCommands()) {
            String commandString = command.getCommand();

            if (commandString.charAt(0) == '/') {
                commandString = commandString.substring(1);
            }

            commandString = commandString.replace("%player%", player.getName());

            Main.getInstance().getServer().dispatchCommand(Main.getInstance().getServer().getConsoleSender(), commandString);

            if (message) {
                ChatManager.sendMessage(player, Translator.build("quest.addCommand", new TranslatorPlaceholder("commandName", command.getName())));
            }
        }

        if (this.announce) {
            Bukkit.getOnlinePlayers().forEach(players -> {
                ChatManager.sendMessage(player, Translator.build("quest.announce", new TranslatorPlaceholder("player", player.getName()), new TranslatorPlaceholder("questName", getFriendlyName())));
            });
        }
    }
}
