package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class DevCommand extends BasicSubCommand {
    public DevCommand() {
        super("quests", "anturniaquests.command.admin.dev", new String[]{"dev"});
        setDescription("Only for development!");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage("This command is only for development!");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}

