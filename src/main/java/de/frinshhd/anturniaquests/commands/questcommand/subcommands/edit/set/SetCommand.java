package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.set;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetCommand extends BasicSubCommand {

    public SetCommand() {
        super("quests", "anturniaquests.command.admin.quests.set", new String[]{"edit", "<questID>", "set"});
        setDescription("");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "storylines"});
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();
        if (args.length == 4) {

            List<BasicSubCommand> basicSubCommands = Main.getCommandManager().getSubCommands(Main.getCommandManager().getCommand(getMainCommand()));

            basicSubCommands.forEach(basicSubCommand -> {
                if (basicSubCommand.getPath().length >= args.length && basicSubCommand.getPath()[0].startsWith(args[0])) {
                    completions.add(basicSubCommand.getPath()[args.length - 1]);
                }
            });
        } else {
            BasicSubCommand subCommand = Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), args);

            if (subCommand != null) {
                completions.addAll(subCommand.tabComplete(sender, args));
            }
        }

        /*BasicSubCommand subCommand = Main.getCommandManager().getSubCommand(this, args);

        System.out.println(subCommand);

        if (subCommand != null) {
            completions.addAll(subCommand.tabComplete(sender, args));
        } */

        // first layer logic comes here

        return completions;

    }
}
