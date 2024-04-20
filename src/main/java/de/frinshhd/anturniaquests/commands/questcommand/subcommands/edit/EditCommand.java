package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class EditCommand extends BasicSubCommand {
    //Layout: /quests create <questID>

    public EditCommand() {
        super("quests", "anturniaquests.command.admin.edit", new String[]{"edit", "<questID>"});
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 2) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "edit"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getQuest(questID) == null) {
            //tell player this questID does not exist
            return true;
        }



        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("edit");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 0) {
                completions.add(completion);
                return;
            }

            if (args.length == 1 && completion.toLowerCase().startsWith(args[0])) {
                completions.add(completion);
                return;
            }
        });

        if (args.length == 2) {
            Main.getQuestsManager().questsRaw.keySet().forEach(questID -> {
                if (questID.toLowerCase().startsWith(args[1])) {
                    completions.add(questID);
                }
            });
        } else if (args.length == 3) {

            List<BasicSubCommand> basicSubCommands = Main.getCommandManager().getSubCommands(Main.getCommandManager().getCommand(getMainCommand()));

            basicSubCommands.forEach(basicSubCommand -> {
                if (basicSubCommand.getPath().length >= args.length && basicSubCommand.getPath()[0].startsWith(args[0])) {
                    completions.add(basicSubCommand.getPath()[args.length - 1]);
                }
            });
        }

        return completions;
    }
}
