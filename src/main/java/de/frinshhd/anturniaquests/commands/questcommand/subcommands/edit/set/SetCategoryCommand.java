package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.set;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetCategoryCommand extends BasicSubCommand {

    public SetCategoryCommand() {
        super("quests", "anturniaquests.command.admin.quests.set.category", new String[]{"edit", "<questID>", "set", "category", "<category>"});
        setDescription("Set the description of a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length <= 4) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "edit", "<questID>", "set", "category"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        String category = args[4];

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        quest.setCategory(category);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        //Todo tell player that he changed the friendlyName of quest
        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.set.category", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("category", category)));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("category");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 3) {
                completions.add(completion);
                return;
            }

            if (args.length == 4 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
                return;
            }
        });

        if (args.length == 5) {
            Main.getDynamicCategories().categories.keySet().forEach(categoryID -> {
                if (categoryID.toLowerCase().startsWith(args[4])) {
                    completions.add(categoryID);
                }
            });
        }

        return completions;
    }
}
