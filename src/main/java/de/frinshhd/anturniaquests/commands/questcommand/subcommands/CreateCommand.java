package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand extends BasicSubCommand {

    public CreateCommand() {
        super("quests", "anturniaquests.command.admin.create", new String[]{"create", "<questID>"});
        setDescription("Creates a new quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 2) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "create"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getQuest(questID) != null) {
            ChatManager.sendMessage(sender, Translator.build("quest.exists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        Main.getQuestsManager().saveQuestToYml(questID, new Quest());
        ChatManager.sendMessage(sender, Translator.build("quest.command.create", new TranslatorPlaceholder("questID", questID)));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("create");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 0) {
                completions.add(completion);
                return;
            }

            if (args.length == 1 && completion.toLowerCase().startsWith(args[0])) {
                completions.add(completion);
            }
        });

        return completions;
    }
}
