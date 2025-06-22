package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.add.rewards;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.quests.models.Rewards;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AddMoneyRewardCommand extends BasicSubCommand {

    public AddMoneyRewardCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.reward", new String[]{"edit", "<questID>", "add", "reward", "money", "<amount>"});
        setDescription("Add a money reward to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 6) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "reward", "money"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quest.dontExists", new Translatable("questID", questID)));
            return true;
        }

        String money = args[5];

        if (!isInteger(money)) {
            ChatManager.sendMessage(sender, "false value");
            return true;
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        Rewards rewards = quest.getRewards();

        rewards.setMoney(Integer.parseInt(money));

        quest.setRewards(rewards);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quest.command.edit.add.reward.money", new Translatable("questID", questID), new Translatable("amount", String.valueOf(Integer.parseInt(money)))));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("money");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });


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
