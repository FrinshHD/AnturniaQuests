package de.frinshhd.anturniaquests.commands;

import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.QuestMenu;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class QuestCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length >= 1) {
            if (args[0].equals("help")) {
                if (sender.hasPermission("anturniaquests.command.help")) {
                    sendHelpMessage(sender);
                    return true;
                } else {
                    ChatManager.sendMessage(sender, Translator.build("noPermission"));
                    return false;
                }
            }

            if (args[0].equals("reload")) {
                if (!sender.hasPermission("anturniaquests.command.admin.reload")) {
                    ChatManager.sendMessage(sender, Translator.build("noPermission"));
                    return false;
                }

                Main.reload();
                ChatManager.sendMessage(sender, Translator.build("quests.reload"));
                return true;
            }

            if (args[0].equals("version")) {
                if (sender.hasPermission("anturniaquests.command.version")) {
                    ChatManager.sendMessage(sender, Translator.build("quests.currentVersion", new TranslatorPlaceholder("version", Main.getInstance().getDescription().getVersion())));
                    return true;
                }
            }

            if (args.length >= 2) {
                if (args[0].equals("reset")) {
                    if (sender.hasPermission("anturniaquests.command.admin.reset")) {
                        Player target = Bukkit.getPlayer(args[1]);
                        String questID = args[2];

                        if (Main.getQuestsManager().getQuest(questID) == null) {
                            sendHelpMessage(sender);
                            return false;
                        }

                        if (target == null) {
                            sendHelpMessage(sender);
                            return false;
                        }

                        UUID targetUUID = target.getUniqueId();

                        Dao<Quests, Long> questsDao;
                        try {
                            questsDao = MysqlManager.getQuestDao();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        Quests quest = null;
                        try {
                            quest = questsDao.queryForEq("uuid", targetUUID).stream().toList().get(0);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }


                        quest.setQuest(questID, 0);

                        try {
                            questsDao.update(quest);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                        ChatManager.sendMessage(sender, Translator.build("quest.command.reset",
                                new TranslatorPlaceholder("playerName", target.getName()),
                                new TranslatorPlaceholder("questName", Main.getQuestsManager().getQuest(questID).getFriendlyName())));

                        return true;
                    }
                    ChatManager.sendMessage(sender, Translator.build("noPermission"));
                    return false;
                }
            }

            if (args.length >= 3) {
                if (args[0].equals("storylines")) {
                    if (args[1].equals("reset")) {
                        if (sender.hasPermission("anturniaquests.command.admin.storylines.reset")) {
                            Player target = Bukkit.getPlayer(args[2]);
                            String storylineID = args[3];

                            if (Main.getStorylinesManager().getStoryline(storylineID) == null) {
                                sendHelpMessage(sender);
                                return false;
                            }

                            if (target == null) {
                                sendHelpMessage(sender);
                                return false;
                            }

                            Main.getStorylinesManager().removePlayerStoryline(target, storylineID);
                            ChatManager.sendMessage(sender, Translator.build("storyline.command.reset",
                                    new TranslatorPlaceholder("playerName", target.getName()),
                                    new TranslatorPlaceholder("storylineName", Main.getStorylinesManager().getStoryline(storylineID).getName())));
                            return true;
                        }

                        ChatManager.sendMessage(sender, Translator.build("noPermission"));
                        return false;
                    }
                }
            }
        }

        if (!(sender instanceof Player player)) {
            sendHelpMessage(sender);
            return false;
        }

        if (args.length == 0) {
            if (!Main.getConfigManager().getConfig().questMenuEnabled) {
                sendHelpMessage(player);
                return true;
            }

            if (player.hasPermission("anturniaquests.command.open")) {
                new QuestMenu(Main.getPlayerMenuUtility(player)).open(player);
                return true;
            } else {
                ChatManager.sendMessage(player, Translator.build("noPermission"));
                return false;
            }
        }

        if (args.length >= 1) {
            if (args[0].toLowerCase().equals("iteminfo")) {
                if (player.hasPermission("anturniaquests.command.admin.itemInfo")) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item == null || item.getType().equals(Material.AIR)) {
                        player.sendMessage("§cYour holding no valid item!");
                        return false;
                    }

                    if (!item.hasItemMeta()) {
                        player.sendMessage("§cYour item has no custom attributes!");
                        return false;
                    }

                    ItemMeta itemMeta = item.getItemMeta();

                    player.sendMessage("§2material: §r" + item.getType());
                    player.sendMessage("§2amount: §r" + item.getAmount());

                    assert itemMeta != null;
                    if (itemMeta.hasDisplayName()) {
                        String displayName = itemMeta.getDisplayName();
                        displayName = displayName.replace('§', '&');
                        player.sendMessage("§2displayName: §r" + displayName);
                    }

                    if (itemMeta.hasLore()) {
                        ArrayList<String> loreRaw = new ArrayList<>(Objects.requireNonNull(itemMeta.getLore()));
                        ArrayList<String> lore = new ArrayList<>();
                        loreRaw.forEach(string -> {
                            string = string.replace('§', '&');
                            lore.add(string);
                        });

                        player.sendMessage("§2lore:");

                        lore.forEach(string -> {
                            player.sendMessage("§7- §r" + string);
                        });
                    }

                    return true;
                }

                ChatManager.sendMessage(sender, Translator.build("noPermission"));
                return false;
            }
        }

        sendHelpMessage(player);
        return false;
    }

    public void sendHelpMessage(CommandSender sender) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("§2Quests Help\n");
        stringBuilder.append("§7- §2/quests §7- Open the quests menu\n");

        if (sender.hasPermission("anturniaquests.command.version")) {
            stringBuilder.append("§7- §2/quests version §7- Get the current installed version of AnturniaQuests\n");
        }

        if (sender.hasPermission("anturniaquests.command.admin.reload")) {
            stringBuilder.append("§7- §2/quests reload §7- Reload the plugin's configurations\n");
        }

        if (sender.hasPermission("anturniaquests.command.admin.reset")) {
            stringBuilder.append("§7- §2/quests reset <playerName> <questID> §7- Resets the completion count of the player for a quest\n");
        }

        if (sender.hasPermission("anturniaquests.command.help")) {
            stringBuilder.append("§7- §2/quests help §7- Take a look at this message\n");
        }

        stringBuilder.append("§7If you need more help join our discord at §2https://logic.frinshy.me/discord");

        ChatManager.sendMessage(sender, stringBuilder.toString());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // Possible completions
        List<String> commands = new ArrayList<>(List.of(new String[]{}));

        if (sender.hasPermission("anturniaquests.command.version")) {
            commands.add("version");
        }

        if (sender.hasPermission("anturniaquests.command.help")) {
            commands.add("help");
        }

        if (sender.hasPermission("anturniaquests.command.admin.reload")) {
            commands.add("reload");
        }

        if (sender.hasPermission("anturniaquests.command.admin.itemInfo")) {
            commands.add("iteminfo");
        }

        if (sender.hasPermission("anturniaquests.command.admin.reset")) {
            commands.add("reset");
        }

        if (sender.hasPermission("anturniaquests.command.admin.storylines")) {
            commands.add("storylines");
        }

        // Filter
        commands.forEach(completion -> {
            if (args.length == 0) {
                completions.add(completion);
                return;
            }

            if (args.length == 1 && completion.toLowerCase().startsWith(args[0])) {
                completions.add(completion);
                return;
            }
        });

        String arg0 = args[0];

        //2. arg
        if (args.length == 2) {
            if (arg0.equals("reset")) {
                if (sender.hasPermission("anturniaquests.command.admin.reset")) {
                    Main.getInstance().getServer().getOnlinePlayers().forEach(player -> {
                        if (player.getName().toLowerCase().startsWith(args[1])) {
                            completions.add(player.getName());
                        }
                    });
                }
            }

            if (arg0.equals("storylines")) {
                ArrayList<String> possibleSubCommands = new ArrayList<>();

                if (sender.hasPermission("anturniaquests.command.admin.storylines.reset")) {
                    possibleSubCommands.add("reset");
                }

                possibleSubCommands.forEach(possibleSubCommand -> {
                    if (possibleSubCommand.toLowerCase().startsWith(args[1])) {
                        completions.add(possibleSubCommand);
                    }
                });
            }
        }


        //3. arg
        if (args.length == 3) {
            if (arg0.equals("reset")) {
                if (sender.hasPermission("anturniaquests.command.admin.reset")) {
                    ArrayList<String> quests = new ArrayList<>(Main.getQuestsManager().quests.keySet());

                    quests.forEach(quest -> {
                        if (quest.toLowerCase().startsWith(args[2])) {
                            completions.add(quest);
                        }
                    });
                }
            }

            if (arg0.equals("storylines")) {
                if (args[1].equals("reset")) {
                    if (sender.hasPermission("anturniaquests.command.admin.storylines.reset")) {
                        Main.getInstance().getServer().getOnlinePlayers().forEach(player -> {
                            if (player.getName().toLowerCase().startsWith(args[2])) {
                                completions.add(player.getName());
                            }
                        });
                    }
                }
            }
        }

        //4. arg
        if (args.length == 4) {
            if (arg0.equals("storylines")) {
                if (args[1].equals("reset")) {
                    if (sender.hasPermission("anturniaquests.command.admin.storylines.reset")) {
                        ArrayList<String> storylines = new ArrayList<>(Main.getStorylinesManager().storylines.keySet());

                        storylines.forEach(storyline -> {
                            if (storyline.toLowerCase().startsWith(args[3])) {
                                completions.add(storyline);
                            }
                        });
                    }
                }
            }
        }

        return completions;
    }

}
