package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.set;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetOneTimeUseCommand extends BasicSubCommand {

    public SetOneTimeUseCommand() {
        super("quests", "anturniaquests.command.admin.quests.set.onetimeuse", new String[]{"edit", "<questID>", "set", "onetimeuse", "<true, false>"});
        setDescription("Set the material of a quest's item.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length <= 4) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "edit", "<questID>", "set", "onetimeuse"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quest.dontExists", new Translatable("questID", questID)));
            return true;
        }

        String oneTimeUse = args[4];

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        quest.setOneTime(Boolean.parseBoolean(oneTimeUse));

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quest.command.edit.set.oneTimeUse", new Translatable("questID", questID), new Translatable("oneTimeUse", String.valueOf(Boolean.parseBoolean(oneTimeUse)))));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("onetimeuse");

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
