package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand extends BasicSubCommand {
    public HelpCommand() {
        super("quest", "anturniaquests.command.help", new String[]{"help"});
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        StringBuilder message = new StringBuilder();

        message.append(
                        Translator.build("help.title",
                                new TranslatorPlaceholder("commandName", Main.getCommandManager().getCommand(getMainCommand()).getCommand())))
                .append("\n");

        message.append(
                        Translator.build("help.command",
                                new TranslatorPlaceholder("command", "/" + getMainCommand()),
                                new TranslatorPlaceholder("description", Main.getCommandManager().getCommand(getMainCommand()).getDescription())))
                .append("\n");

        List<BasicSubCommand> subCommands = Main.getCommandManager().getSubCommands(Main.getCommandManager().getCommand(getMainCommand()));

        subCommands.forEach(subCommand -> {
            if (subCommand.getPermission() == null || sender.hasPermission(subCommand.getPermission())) {
                message.append(
                                Translator.build("help.command",
                                        new TranslatorPlaceholder("command", "/" + subCommand.getCommandFull()),
                                        new TranslatorPlaceholder("description", subCommand.getDescription())))
                        .append("\n");
            }
        });

        ChatManager.sendMessage(sender, message.toString());
        return true;
    }
}
