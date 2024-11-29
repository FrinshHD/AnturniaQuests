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

public class SetFriendlyNameCommand extends BasicSubCommand {

    public SetFriendlyNameCommand() {
        super("quests", "anturniaquests.command.admin.quests.set.friendlyname", new String[]{"edit", "<questID>", "set", "friendlyName", "<friendlyName>"});
        setDescription("Set the friendly name of a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length <= 4) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "edit", "<questID>", "set", "friendlyName"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        StringBuilder friendlyName = new StringBuilder();
        for (int i = 4; i < args.length; i++) {
            friendlyName.append(args[i]).append(" ");
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        quest.setFriendlyName(friendlyName.toString());

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        //Todo tell player that he changed the friendlyName of quest
        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.set.displayName", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("friendlyName", friendlyName.toString())));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("friendlyName");

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
