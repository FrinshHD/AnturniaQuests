package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.remove;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RemoveShowCompletionsCommand extends BasicSubCommand {

    public RemoveShowCompletionsCommand() {
        super("quests", "anturniaquests.command.admin.quests.remove.showcompletions", new String[]{"edit", "<questID>", "remove", "showcompletions"});
        setDescription("Remove the showCompletions of a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        quest.setShowCompletions(null);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.remove.showCompletions", new TranslatorPlaceholder("questID", questID)));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("showCompletions");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 3) {
                completions.add(completion);
                return;
            }

            if (args.length == 4 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });

        return completions;
    }
}
