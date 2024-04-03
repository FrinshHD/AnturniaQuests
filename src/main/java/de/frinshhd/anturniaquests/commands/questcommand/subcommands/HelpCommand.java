package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends BasicSubCommand {
    public HelpCommand() {
        super("quests", "anturniaquests.command.help", new String[]{"help"});
        setDescription("Shows all available commands and their descriptions.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        StringBuilder message = new StringBuilder();

        message.append(
                        Translator.build("help.title",
                                new TranslatorPlaceholder("commandName", "/" + Main.getCommandManager().getCommand(getMainCommand()).getName())))
                .append("\n");

        if (Main.getCommandManager().getCommand(getMainCommand()).getDescription().isEmpty()) {
            message.append(
                    Translator.build("help.command.noDescription",
                            new TranslatorPlaceholder("command", "/" + getMainCommand())))
                    .append("\n");
        } else {
            message.append(
                            Translator.build("help.command",
                                    new TranslatorPlaceholder("command", "/" + getMainCommand()),
                                    new TranslatorPlaceholder("description", Main.getCommandManager().getCommand(getMainCommand()).getDescription())))
                    .append("\n");
        }

        List<BasicSubCommand> subCommands = Main.getCommandManager().getSubCommands(Main.getCommandManager().getCommand(getMainCommand()));

        subCommands.forEach(subCommand -> {
            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                if (subCommand.getDescription() == null || subCommand.getDescription().isEmpty()) {
                    message.append(
                                    Translator.build("help.command.noDescription",
                                            new TranslatorPlaceholder("command", "/" + subCommand.getCommandFull())))
                            .append("\n");
                } else {
                    message.append(
                                    Translator.build("help.command",
                                            new TranslatorPlaceholder("command", "/" + subCommand.getCommandFull()),
                                            new TranslatorPlaceholder("description", subCommand.getDescription())))
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
                return;
            }
        });

        return completions;
    }
}
