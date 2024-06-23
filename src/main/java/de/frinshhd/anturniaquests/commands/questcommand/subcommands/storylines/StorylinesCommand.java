package de.frinshhd.anturniaquests.commands.questcommand.subcommands.storylines;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class StorylinesCommand extends BasicSubCommand {

    public StorylinesCommand() {
        super("quests", "anturniaquests.command.admin.storylines", new String[]{"storylines"});
        setDescription("Manage storylines.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "storylines"});
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 2) {

            List<BasicSubCommand> basicSubCommands = Main.getCommandManager().getSubCommands(Main.getCommandManager().getCommand(getMainCommand()));

            basicSubCommands.forEach(basicSubCommand -> {
                if (basicSubCommand.getPath().length >= args.length && !basicSubCommand.getPath()[0].equalsIgnoreCase(args[0])) {
                    return;
                }


                if (basicSubCommand.getPath().length >= args.length && basicSubCommand.getPath()[1].startsWith(args[1])) {
                    completions.add(basicSubCommand.getPath()[args.length - 1]);
                }
            });
        }

        return completions;
    }
}
