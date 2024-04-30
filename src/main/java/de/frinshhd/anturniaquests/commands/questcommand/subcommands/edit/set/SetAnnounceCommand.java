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

public class SetAnnounceCommand extends BasicSubCommand {

    public SetAnnounceCommand() {
        super("quests", "anturniaquests.command.admin.quests.set.announce", new String[]{"edit", "<questID>", "set", "announce", "<true, false>"});
        setDescription("Set whether to announce the all online players that a player has completed this quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length <= 4) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "edit", "<questID>", "set", "announce" });
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        String announce = args[4];

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        quest.setAnnounce(Boolean.parseBoolean(announce));

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.set.announce", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("announce", String.valueOf(Boolean.parseBoolean(announce)))));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("announce");

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
            ArrayList<String> possible = new ArrayList<>(List.of("true", "false"));
            possible.forEach(completion -> {
                if (completion.startsWith(args[4].toLowerCase())) {
                    completions.add(completion);
                }
            });
        }

        return completions;
    }
}
