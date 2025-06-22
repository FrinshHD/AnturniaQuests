package de.frinshhd.anturniaquests.quests.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.requirements.items.ItemModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.ItemTags;
import de.frinshhd.anturniaquests.utils.LoreBuilder;
import de.frinshhd.anturniaquests.utils.SurvivalQuestSounds;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
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

public class Quest {

    @JsonProperty
    private String friendlyName = null;

    @JsonProperty
    private String description = null;

    @JsonProperty
    private String category = null;

    @Deprecated
    @JsonProperty
    private String material = null;

    @JsonProperty
    private DisplayItem displayItem = null;

    @JsonProperty
    private Boolean oneTime = null;

    @JsonProperty
    private Boolean announce = null;

    @JsonProperty
    private Integer cooldown = null;

    @JsonProperty
    private Boolean showCompletions = null;

    @JsonProperty
    private Sound completionSound = null;

    @JsonProperty
    private Sound errorSound = null;

    @JsonProperty
    private ArrayList<String> requiredQuests = new ArrayList<>();

    @JsonProperty
    private LinkedHashMap<String, ArrayList<Object>> requirements = new LinkedHashMap<>();

    @JsonProperty
    private Rewards rewards = null;

    public Quest() {
    }

    @JsonIgnore
    public String getFriendlyName() {
        if (this.friendlyName == null) {
            return "";
        }
        return this.friendlyName;
    }

    @JsonIgnore
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @JsonIgnore
    public String getCategory() {
        return this.category;
    }

    @JsonIgnore
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonIgnore
    public String getDescription() {
        if (this.description == null) {
            return "";
        }

        return this.description;
    }

    @JsonIgnore
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public ItemStack getDisplayItem() {
        if (displayItem == null) {
            return new ItemStack(getMaterial());
        }

        return displayItem.getItem();
    }

    @JsonIgnore
    public Material getMaterial() {
        if (this.material != null) {
            return Material.getMaterial(this.material);
        }

        if (getRequirement("items") != null && !getRequirement("items").isEmpty()) {
            return ((ItemModel) getRequirement("items").get(0)).getMaterial();
        }

        return Material.STONE;
    }

    @JsonIgnore
    public void setMaterial(String material) {
        if (Material.getMaterial(material.toUpperCase()) != null) {
            this.material = material;
        }
    }

    @JsonIgnore
    public String getID() {
        return Main.getQuestsManager().getQuestID(this);
    }

    @JsonIgnore
    public Long getCooldown() {
        if (this.cooldown == null) {
            return null;
        }

        return this.cooldown * 1000L;
    }

    @JsonIgnore
    public void setCooldown(Integer cooldown) {
        this.cooldown = cooldown;
    }

    @JsonIgnore
    public boolean isShowCompletions() {
        if (showCompletions == null) {
            return !isOneTimeUse();
        }

        return showCompletions;
    }

    @JsonIgnore
    public void setShowCompletions(Boolean showCompletions) {
        this.showCompletions = showCompletions;
    }

    @JsonIgnore
    public void setAnnounce(Boolean announce) {
        this.announce = announce;
    }

    @JsonIgnore
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
    public void setOneTime(Boolean oneTime) {
        this.oneTime = oneTime;
    }

    @JsonIgnore
    public boolean isOneTimeUse() {
        if (this.oneTime == null) {
            return true;
        }

        return this.oneTime;
    }

    @JsonIgnore
    public Rewards getRewards() {
        if (rewards == null) {
            return new Rewards();
        }
        return this.rewards;
    }

    @JsonIgnore
    public void setRewards(Rewards rewards) {
        this.rewards = rewards;
    }

    @JsonIgnore
    public void setRequirement(String id, ArrayList<Object> objects) {
        if (getRequirement(id) == null) {
            return;
        }

        requirements.put(id, objects);
    }

    @JsonIgnore
    public LinkedHashMap<String, ArrayList<Object>> getRequirements() {
        return this.requirements;
    }

    @JsonIgnore
    public ArrayList<Object> getRequirement(String id) {
        return getRequirements().get(id);
    }

    @JsonIgnore
    public ItemStack getItem(Player player, HashMap<String, Integer> finishedQuests) {
        ItemStack item = getDisplayItem();
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(TranslationManager.getInstance().build("inventory.quest.color") + getFriendlyName());

        ArrayList<String> lore = new ArrayList<>();
        if (!getDescription().isEmpty()) {
            lore.addAll(LoreBuilder.build(getDescription(), ChatColor.GRAY));
            lore.add(" ");
        }

        if (isOneTimeUse() && finishedQuests.containsKey(Main.getQuestsManager().getQuestID(this)) && finishedQuests.get(Main.getQuestsManager().getQuestID(this)) > 0) {
            lore.add(TranslationManager.getInstance().build("lore.alreadyCompleted"));
        } else if (getCooldown() != null &&
                MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().containsKey(Main.getQuestsManager().getQuestID(this)) &&
                MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().get(Main.getQuestsManager().getQuestID(this)) + getCooldown() >= System.currentTimeMillis()) {
            lore.addAll(LoreBuilder.build(TranslationManager.getInstance().build("lore.cooldown", new Translatable("cooldown", String.valueOf((MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().get(Main.getQuestsManager().getQuestID(this)) + getCooldown() - System.currentTimeMillis()) / 1000))), ChatColor.GRAY));

            if (isShowCompletions()) {
                lore.add(" ");
                lore.add(TranslationManager.getInstance().build("lore.completions", new Translatable("completions", String.valueOf(Main.getQuestsManager().getPlayerQuestCompletions(player, Main.getQuestsManager().getQuestID(this))))));
            }
        } else if (!checkCanCompleteQuest(player)) {
            lore.addAll(LoreBuilder.build(TranslationManager.getInstance().build("lore.requiredQuests.base"), ChatColor.RED));

            for (Quest quest : getQuestsToCompletePlayer(player)) {
                lore.add(TranslationManager.getInstance().build("lore.requiredQuests.quest", new Translatable("questName", quest.getFriendlyName())));
            }
        } else {
            lore.add(TranslationManager.getInstance().build("lore.requirements.header"));

            getRequirements().keySet().forEach(id -> {
                ArrayList<String> loreRequirements = Main.getRequirementManager().getRequirement(id).getLore(player, getRequirements().get(id));
                ArrayList<String> loreRequirementsNew = new ArrayList<>();

                loreRequirements.forEach(loreRequirement -> {
                    loreRequirementsNew.addAll(LoreBuilder.buildSimple(loreRequirement));
                });

                lore.addAll(loreRequirementsNew);
            });

            lore.add(" ");

            lore.add(TranslationManager.getInstance().build("lore.rewards.header"));

            //money
            if (getRewards().getMoney() > 0.0) {
                lore.add(TranslationManager.getInstance().build("lore.rewards.money", new Translatable("amount", String.valueOf(getRewards().getMoney()))));
            }

            //items
            for (ItemModel rewardItemModel : getRewards().getItems()) {
                lore.add(TranslationManager.getInstance().build("lore.rewards.item", new Translatable("amount", String.valueOf(rewardItemModel.getAmount())), new Translatable("itemName", rewardItemModel.getDisplayName())));
            }

            //commands
            for (Command command : getRewards().getCommands()) {
                lore.add(TranslationManager.getInstance().build("lore.rewards.commands", new Translatable("name", command.getName())));
            }

            if (isShowCompletions()) {
                lore.add(" ");
                lore.add(TranslationManager.getInstance().build("lore.completions", new Translatable("completions", String.valueOf(Main.getQuestsManager().getPlayerQuestCompletions(player, Main.getQuestsManager().getQuestID(this))))));
            }

            if (!isOneTimeUse()) {
                lore.add(" ");
                lore.add(TranslationManager.getInstance().build("quests.tip.useMultipleTimes"));
            }

        }

        itemMeta.setLore(lore);

        ItemTags.tagItemMeta(itemMeta, "quest_" + Main.getQuestsManager().getQuestID(this));

        item.setItemMeta(itemMeta);
        return item;
    }

    @JsonIgnore
    public boolean checkCanCompleteQuest(Player player) {
        return getQuestsToCompletePlayer(player).isEmpty();
    }

    @JsonIgnore
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

    @JsonIgnore
    public void playerClick(Player player) throws SQLException {
        playerClick(player, false);
    }

    @JsonIgnore
    public boolean playerClick(Player player, boolean message) throws SQLException {
        if (isOneTimeUse() && MysqlManager.getQuestPlayer(player.getUniqueId()).getFinishedQuests().containsKey(Main.getQuestsManager().getQuestID(this)) && MysqlManager.getQuestPlayer(player.getUniqueId()).getFinishedQuests().get(Main.getQuestsManager().getQuestID(this)) > 0) {

            if (message) {
                ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.alreadyCompleted"));
            }

            SurvivalQuestSounds.questError(player);
            return false;
        }

        //check if player has a cooldown for this quest
        if (getCooldown() != null &&
                MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().containsKey(Main.getQuestsManager().getQuestID(this)) &&
                MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().get(Main.getQuestsManager().getQuestID(this)) + getCooldown() >= System.currentTimeMillis()) {

            if (message) {
                ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.cooldown", new Translatable("cooldown", String.valueOf((MysqlManager.getQuestPlayer(player.getUniqueId()).getCooldown().get(Main.getQuestsManager().getQuestID(this)) + getCooldown() - System.currentTimeMillis()) / 1000))));
            }

            SurvivalQuestSounds.questError(player);
            return false;
        }


        //check if player needs to complete other quests before he can complete this one
        if (!checkCanCompleteQuest(player)) {

            if (message) {
                ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.requiredQuests.base"));

                for (Quest quest : getQuestsToCompletePlayer(player)) {
                    ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.requiredQuests.quest", new Translatable("questName", quest.getFriendlyName())));
                }
            }

            SurvivalQuestSounds.questError(player);
            return false;
        }

        if (!Main.getRequirementManager().check(player, getID())) {
            if (message) {
                ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.missingRequirements"));

                Main.getRequirementManager().sendPlayerMissing(player, getID());
            }
            SurvivalQuestSounds.questError(player);
            return false;
        }

        Main.getRequirementManager().complete(player, getID());

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

    @JsonIgnore
    public void claim(Player player, boolean message) {

        if (message) {
            ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.complete", new Translatable("questName", getFriendlyName())));
        }

        SurvivalQuestSounds.questComplete(player);

        //money
        if (getRewards().getMoney() > 0.0) {
            if (Main.getEconomy() != null) {
                Main.getEconomy().depositPlayer(player, getRewards().getMoney());

                if (message) {
                    ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.addMoney", new Translatable("amount", String.valueOf(getRewards().getMoney()))));
                }
            }
        }

        //items
        for (ItemModel rewardItemModel : getRewards().getItems()) {
            QuestsManager.addItem(player, rewardItemModel.getItem(), rewardItemModel.getAmount());

            if (message) {
                ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.addItem", new Translatable("amount", String.valueOf(rewardItemModel.getAmount())), new Translatable("itemName", rewardItemModel.getDisplayName())));
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
                ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.addCommand", new Translatable("commandName", command.getName())));
            }
        }

        if (this.announce != null && this.announce) {
            Bukkit.getOnlinePlayers().forEach(players -> {
                ChatManager.sendMessage(players, TranslationManager.getInstance().build("quests.announce", new Translatable("player", player.getName()), new Translatable("questName", getFriendlyName())));
            });
        }
    }

    public void addRequirement(String requirementID, BasicRequirementModel requirementModel) {
        ArrayList<Object> objects = new ArrayList<>();

        if (getRequirement(requirementID) != null) {
            objects = getRequirement(requirementID);
        }

        objects.add(requirementModel);

        requirements.put(requirementID, objects);
    }
}
