package de.frinshhd.anturniaquests.commands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.QuestMenu;
import de.frinshhd.anturniaquests.utils.Translator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuestCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length >= 1) {
                if (args[0].equals("reload")) {
                    if (!sender.hasPermission("quests.reload")) {
                        sender.sendMessage(Translator.build("noPermission"));
                        return false;
                    }

                    Main.reload();
                    sender.sendMessage(Translator.build("quests.reload"));
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            if (player.hasPermission("quests.open")) {
                new QuestMenu(Main.getPlayerMenuUtility(player)).open(player);
            } else {
                player.sendMessage(Translator.build("noPermission"));
            }
            return true;
        }

        if (args.length >= 1) {
            if (args[0].equals("help")) {
                if (player.hasPermission("quests.help")) {
                    sendHelpMessage(player);
                    return true;
                } else {
                    return false;
                }
            }
            if (args[0].equals("reload")) {
                if (!player.hasPermission("quests.reload")) {
                    player.sendMessage(Translator.build("noPermission"));
                    return false;
                }

                Main.reload();
                player.sendMessage(Translator.build("quests.reload"));
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public void sendHelpMessage(Player player) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("§2Quests Help\n");
        stringBuilder.append("§7- §2/quests §7- Open the quests menu\n");
        stringBuilder.append("§7- §2/quests help §7- Take a look at this message\n");

        if (player.hasPermission("quests.reload")) {
            stringBuilder.append("§7- §2/quests reload §7- Reload the plugin's configurations\n");
        }

        stringBuilder.append("§7If you need more help join our discord at §2https://discord.gg/89Dv8rqkpC");

        player.sendMessage(stringBuilder.toString());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        List<String> completions = new ArrayList<>();

        // Possible completions
        List<String> commands = new ArrayList<>(List.of(new String[]{}));

        if (sender.hasPermission("quests.help")) {
            commands.add("help");
        }

        if (sender.hasPermission("quests.reload")) {
            commands.add("reload");
        }

        // Filter
        commands.forEach(completion -> {
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
