package de.frinshhd.anturniaquests.commands.questcommand.subcommands.storylines;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.PlayerHashMap;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorylinesResetCommand extends BasicSubCommand {

    private final PlayerHashMap<UUID, Long> lastExecution = new PlayerHashMap<>();

    public StorylinesResetCommand() {
        super("quests", "anturniaquests.command.admin.storylines.reset", new String[]{"storylines", "reset"});
        setDescription("Resets a player's storyline progress.");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player target = null;

        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
        }

        String storylineID = null;

        if (args.length >= 4) {
            storylineID = args[3];
        }

        if (target == null) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, new String[]{"help", "storylines", "reset"});
            return true;
        }

        if (storylineID == null || Main.getStorylinesManager().getStoryline(storylineID) == null) {
            if (canFullyReset(sender)) {
                Main.getStorylinesManager().resetPlayerStorylines(target);
                ChatManager.sendMessage(sender, Translator.build("storyline.command.reset.all",
                        new TranslatorPlaceholder("playerName", target.getName())));

                putLastExecution(sender, -1L);
            } else {
                putLastExecution(sender, System.currentTimeMillis());
                ChatManager.sendMessage(sender, Translator.build("quest.command.reset.confirm", new TranslatorPlaceholder("delay", "10")));
            }
        } else {
            Main.getStorylinesManager().removePlayerStoryline(target, storylineID);
            ChatManager.sendMessage(sender, Translator.build("storyline.command.reset",
                    new TranslatorPlaceholder("playerName", target.getName()),
                    new TranslatorPlaceholder("storylineName", Main.getStorylinesManager().getStoryline(storylineID).getName())));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 3) {
            Main.getInstance().getServer().getOnlinePlayers().forEach(player -> {
                if (player.getName().toLowerCase().startsWith(args[2])) {
                    completions.add(player.getName());
                }
            });
        }

        if (args.length == 4) {
            ArrayList<String> storylines = new ArrayList<>(Main.getStorylinesManager().storylines.keySet());

            storylines.forEach(storyline -> {
                if (storyline.toLowerCase().startsWith(args[3])) {
                    completions.add(storyline);
                }
            });
        }

        return completions;
    }

    private boolean canFullyReset(CommandSender sender) {
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
