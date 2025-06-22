package de.frinshhd.anturniaquests.requirements.money;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AddMoneyRequirementCommand extends BasicSubCommand {

    public AddMoneyRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement", new String[]{"edit", "<questID>", "add", "requirement", "money", "<amount>"});
        setDescription("Adds a money requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 6) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "requirement", "money"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quests.dontExists", new Translatable("questID", questID)));
            return true;
        }


        String amount = args[5];

        if (!isDouble(amount)) {
            ChatManager.sendMessage(sender, "false value");
            return true;
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        MoneyModel moneyModel = new MoneyModel(new LinkedHashMap<>());
        moneyModel.setAmount(Double.parseDouble(amount));

        quest.addRequirement("money", moneyModel);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quests.command.edit.add.requirement.money", new Translatable("questID", questID), new Translatable("amount", amount)));
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

    private boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
