package de.frinshhd.anturniaquests.commands.questcommand.subcommands.storylines;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.QuestMenu;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorylinesCommand extends BasicSubCommand {

    public StorylinesCommand() {
        super("quests", "anturniaquests.command.admin.storylines", new String[]{"storylines"});
        setDescription("Manage storylines.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, new String[]{"help", "storylines"});
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length <= 2) {

            List<BasicSubCommand> basicSubCommands = Main.getCommandManager().getSubCommands(Main.getCommandManager().getCommand(getMainCommand()));

            basicSubCommands.forEach(basicSubCommand -> {
                if (basicSubCommand.getPath().length >= args.length && basicSubCommand.getPath()[1].startsWith(args[1])) {
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
