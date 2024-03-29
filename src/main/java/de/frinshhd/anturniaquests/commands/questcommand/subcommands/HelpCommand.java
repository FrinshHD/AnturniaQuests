package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import org.bukkit.command.CommandSender;

public class HelpCommand extends BasicSubCommand {
    public HelpCommand() {
        super("quest", new String[]{"help"});
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return false;
    }
}
