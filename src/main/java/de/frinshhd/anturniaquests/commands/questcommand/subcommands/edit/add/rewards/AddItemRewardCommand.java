package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.add.rewards;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.quests.models.Rewards;
import de.frinshhd.anturniaquests.requirements.items.ItemModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AddItemRewardCommand extends BasicSubCommand {

    public AddItemRewardCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.reward", new String[]{"edit", "<questID>", "add", "reward", "item", "<material>", "<amount>", "[name]"});
        setDescription("Add a item reward to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 7) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "reward", "item"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        String material = args[5];

        if (Material.getMaterial(material.toUpperCase()) == null) {
            ChatManager.sendMessage(sender, "material" + material + " does not exist");
            return true;
        }

        String amount = args[6];

        if (!isInteger(amount)) {
            ChatManager.sendMessage(sender, "false value");
            return true;
        }

        String name = null;

        if (args.length >= 8) {
            name = args[7];
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        Rewards rewards = quest.getRewards();

        ItemModel itemModel = new ItemModel();
        itemModel.setMaterial(Material.valueOf(material.toUpperCase()));
        itemModel.setAmount(Integer.parseInt(amount));

        if (name != null) {
            itemModel.setName(name);
        }

        rewards.addItem(itemModel);

        quest.setRewards(rewards);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.add.reward.item", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("itemName", itemModel.getDisplayName())));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("item");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });

        if (args.length == 6) {
            for (Material material : Material.values()) {
                String materialName = material.name();
                if (materialName.toLowerCase().startsWith(args[5].toLowerCase())) {
                    completions.add(materialName.toLowerCase());
                }
            }
        }

        return completions;
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
