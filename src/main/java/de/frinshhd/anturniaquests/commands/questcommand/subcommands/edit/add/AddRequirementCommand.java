package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.add;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AddRequirementCommand extends BasicSubCommand {

    public AddRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement", new String[]{"edit", "<questID>", "add", "requirement"});
        setDescription("Add a requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "edit", "<questID>", "add", "requirement"});
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();
        if (args.length == 5) {

            List<BasicSubCommand> basicSubCommands = Main.getCommandManager().getSubCommands(Main.getCommandManager().getCommand(getMainCommand()));

            basicSubCommands.forEach(basicSubCommand -> {
                if (basicSubCommand.getPath().length >= args.length && basicSubCommand.getPath()[3].startsWith(args[3])) {
                    completions.add(basicSubCommand.getPath()[args.length - 1]);
                }
            });
        }

        return completions;
    }
}
