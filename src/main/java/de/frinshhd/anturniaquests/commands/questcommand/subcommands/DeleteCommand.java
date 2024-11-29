package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.PlayerHashMap;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeleteCommand extends BasicSubCommand {

    private final PlayerHashMap<UUID, Long> lastExecution = new PlayerHashMap<>();

    public DeleteCommand() {
        super("quests", "anturniaquests.command.admin.delete", new String[]{"delete", "<questID>"});
        setDescription("Delete a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 2) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "delete"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        if (canFullyDelete(sender)) {
            //delete logic
            Main.getQuestsManager().deleteQuest(questID);

            ChatManager.sendMessage(sender, Translator.build("quest.command.delete", new TranslatorPlaceholder("questID", questID)));

            putLastExecution(sender, -1L);

            return true;
        }

        putLastExecution(sender, System.currentTimeMillis());
        ChatManager.sendMessage(sender, Translator.build("quest.command.confirm", new TranslatorPlaceholder("delay", "10")));


        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("create");

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

    private boolean canFullyDelete(CommandSender sender) {
        UUID uuid;

        if (sender instanceof Player player) {
            uuid = player.getUniqueId();
        } else {
            uuid = null;
        }

        long lastExecution = this.lastExecution.getOrDefault(uuid, -2L);

        if (lastExecution == -2L) {
            return false;
        }


        return lastExecution + (10 * 1000L) > System.currentTimeMillis();
    }

    private void putLastExecution(CommandSender sender, long time) {
        if (sender instanceof Player player) {
            this.lastExecution.put(player.getUniqueId(), time);
        } else {
            this.lastExecution.put(null, time);
        }
    }
}
