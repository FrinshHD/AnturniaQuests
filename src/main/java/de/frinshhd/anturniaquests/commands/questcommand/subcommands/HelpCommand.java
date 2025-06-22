package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCommand extends BasicSubCommand {
    public HelpCommand() {
        super("quests", "anturniaquests.command.help", new String[]{"help"});
        setDescription("Shows all available commands and their descriptions.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        StringBuilder message = new StringBuilder();

        String arg1;

        if (args.length >= 2) {
            arg1 = args[1];
        } else {
            arg1 = null;
        }

        if (arg1 != null) {
            List<BasicSubCommand> subCommands = Main.getCommandManager().getSubCommands(Main.getCommandManager().getCommand(getMainCommand()));
            List<BasicSubCommand> filteredSubCommands = new ArrayList<>();

            subCommands.forEach(subCommand -> {
                if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
                    return;
                }

                if (Arrays.toString(subCommand.getPath()).contains(arg1)) {
                    filteredSubCommands.add(subCommand);
                }
            });

            if (!filteredSubCommands.isEmpty()) {
                message.append("\n");
                message.append(
                                TranslationManager.getInstance().build("help.title",
                                        new Translatable("commandName", "/" + commandLabel + " " + arg1)))
                        .append("\n");

                filteredSubCommands.forEach(subCommand -> {
                    if (subCommand.isHidden()) {
                        return;
                    }

                    if (subCommand.getDescription() == null || subCommand.getDescription().isEmpty()) {
                        message.append(
                                        TranslationManager.getInstance().build("help.command.noDescription",
                                                new Translatable("command", "/" + commandLabel + " " + subCommand.getCommand())))
                                .append("\n");
                    } else {
                        message.append(
                                        TranslationManager.getInstance().build("help.command.withDescription",
                                                new Translatable("command", "/" + commandLabel + " " + subCommand.getCommand()),
                                                new Translatable("description", subCommand.getDescription())))
                                .append("\n");
                    }
                });

                ChatManager.sendMessage(sender, message.toString());
                return true;
            }
        }

        message.append("\n");
        message.append(
                        TranslationManager.getInstance().build("help.title",
                                new Translatable("commandName", "/" + commandLabel)))
                .append("\n");

        if (Main.getCommandManager().getCommand(getMainCommand()).getDescription().isEmpty()) {
            message.append(
                            TranslationManager.getInstance().build("help.command.noDescription",
                                    new Translatable("command", "/" + commandLabel)))
                    .append("\n");
        } else {
            message.append(
                            TranslationManager.getInstance().build("help.command.withDescription",
                                    new Translatable("command", "/" + commandLabel),
                                    new Translatable("description", Main.getCommandManager().getCommand(getMainCommand()).getDescription())))
                    .append("\n");
        }

        List<BasicSubCommand> subCommands = Main.getCommandManager().getSubCommands(Main.getCommandManager().getCommand(getMainCommand()));

        subCommands.forEach(subCommand -> {
            if (subCommand.isHidden()) {
                return;
            }

            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                if (subCommand.getDescription() == null || subCommand.getDescription().isEmpty()) {
                    message.append(
                                    TranslationManager.getInstance().build("help.command.noDescription",
                                            new Translatable("command", "/" + commandLabel + " " + subCommand.getCommand())))
                            .append("\n");
                } else {
                    message.append(
                                    TranslationManager.getInstance().build("help.command.withDescription",
                                            new Translatable("command", "/" + commandLabel + " " + subCommand.getCommand()),
                                            new Translatable("description", subCommand.getDescription())))
                            .append("\n");
                }
            }
        });

        ChatManager.sendMessage(sender, message.toString());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("help");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 0) {
                completions.add(completion);
                return;
            }

            if (args.length == 1 && completion.toLowerCase().startsWith(args[0])) {
                completions.add(completion);
            }
        });

        return completions;
    }
}
