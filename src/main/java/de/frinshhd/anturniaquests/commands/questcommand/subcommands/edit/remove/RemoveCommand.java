package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.remove;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RemoveCommand extends BasicSubCommand {

    public RemoveCommand() {
        super("quests", "anturniaquests.command.admin.quests.remove", new String[]{"edit", "<questID>", "remove"});
        setDescription("");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "edit", "<questID>", "remove"});
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
                if (basicSubCommand.getPath().length >= args.length && basicSubCommand.getPath()[2].startsWith(args[2])) {
                    completions.add(basicSubCommand.getPath()[args.length - 1]);
                }
            });
        }

        return completions;

    }
}
