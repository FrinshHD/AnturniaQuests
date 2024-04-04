package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class VersionCommand extends BasicSubCommand {

    public VersionCommand() {
        super("quests", "anturniaquests.command.version", new String[]{"version"});
        setDescription("Shows the current plugin version.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        ChatManager.sendMessage(sender, Translator.build("quests.currentVersion", new TranslatorPlaceholder("version", Main.getInstance().getDescription().getVersion())));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("version");

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
