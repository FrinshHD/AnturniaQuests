package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.add;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AddCommand extends BasicSubCommand {

    public AddCommand() {
        super("quests", "anturniaquests.command.admin.quests.add", new String[]{"edit", "<questID>", "add"});
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


        /*BasicSubCommand subCommand = Main.getCommandManager().getSubCommand(this, args);

        System.out.println(subCommand);

        if (subCommand != null) {
            completions.addAll(subCommand.tabComplete(sender, args));
        } */

        // first layer logic comes here

        return completions;
    }
}
