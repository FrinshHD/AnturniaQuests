package de.frinshhd.anturniaquests.commands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicCommand extends Command {

    public BasicCommand(String name, String permission) {
        super(name);

        this.setPermission(permission);
    }


    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            ChatManager.sendMessage(sender, Translator.build("noPermission"));
            return false;
        }

        BasicSubCommand subCommand = Main.getCommandManager().getSubCommand(this, args);

        if (subCommand != null) {
            return subCommand.execute(sender, commandLabel, args);
        }

        // first layer logic comes here

        return false;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        super.tabComplete(sender, alias, args);
        List<String> completions = new ArrayList<>();

        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            return new ArrayList<>();
        }

        //Todo: add support for layered subCommandss

        if (args.length <= 1) {

            List<BasicSubCommand> basicSubCommands = Main.getCommandManager().getSubCommands(this);

            basicSubCommands.forEach(basicSubCommand -> {
                if (basicSubCommand.isHidden()) {
                    return;
                }

                if (basicSubCommand.getPath().length >= args.length && basicSubCommand.getPath()[0].startsWith(args[0])) {
                    completions.add(basicSubCommand.getPath()[args.length - 1]);
                }
            });
        } else {
            BasicSubCommand subCommand = Main.getCommandManager().getSubCommand(this, args);

            if (subCommand != null) {
                if (!subCommand.isHidden()) {
                    completions.addAll(subCommand.tabComplete(sender, args));
                }
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