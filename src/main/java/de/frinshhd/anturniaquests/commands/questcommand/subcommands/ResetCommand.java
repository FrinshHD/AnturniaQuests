package de.frinshhd.anturniaquests.commands.questcommand.subcommands;

import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.PlayerHashMap;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ResetCommand extends BasicSubCommand {

    private final PlayerHashMap<UUID, Long> lastExecution = new PlayerHashMap<>();

    public ResetCommand() {
        super("quests", "anturniaquests.command.admin.reset", new String[]{"reset", "<player>", "[questID}"});
        setDescription("Resets a player's quest progress.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        Player target;

        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
        } else {
            if (sender instanceof Player player) {
                target = player;
            } else {
                Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "reset"});
                return false;
            }
        }

        String questID = null;

        if (args.length >= 3) {
            questID = args[2];
        }

        if (target == null) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "reset"});
            return false;
        }

        UUID targetUUID = target.getUniqueId();


        if (questID == null || Main.getQuestsManager().getQuest(questID) == null) {
            if (canFullyReset(sender)) {
                //reset logic
                resetQuest(targetUUID, null);

                ChatManager.sendMessage(sender, Translator.build("quest.command.reset.all",
                        new TranslatorPlaceholder("playerName", target.getName())));
                putLastExecution(sender, -1L);
            } else {
                putLastExecution(sender, System.currentTimeMillis());
                ChatManager.sendMessage(sender, Translator.build("quest.command.confirm", new TranslatorPlaceholder("delay", "10")));
            }
        } else {
            //reset quest with id
            resetQuest(targetUUID, questID);

            ChatManager.sendMessage(sender, Translator.build("quest.command.reset",
                    new TranslatorPlaceholder("playerName", target.getName()),
                    new TranslatorPlaceholder("questName", Main.getQuestsManager().getQuest(questID).getFriendlyName())));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }

        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("reset");

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

        if (args.length == 2) {
            Main.getInstance().getServer().getOnlinePlayers().forEach(player -> {
                if (player.getName().toLowerCase().startsWith(args[1])) {
                    completions.add(player.getName());
                }
            });
        }

        if (args.length == 3) {
            ArrayList<String> quests = new ArrayList<>(Main.getQuestsManager().quests.keySet());

            quests.forEach(quest -> {
                if (quest.toLowerCase().startsWith(args[2])) {
                    completions.add(quest);
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

    private void resetQuest(UUID playerUUID, String questID) {
        Dao<Quests, Long> questsDao;
        try {
            questsDao = MysqlManager.getQuestDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Quests quest = null;
        try {
            quest = questsDao.queryForEq("uuid", playerUUID).stream().toList().get(0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        if (questID == null || Main.getQuestsManager().getQuest(questID) == null) {
            quest.resetQuests();
        } else {
            quest.setQuest(questID, 0);
        }

        try {
            questsDao.update(quest);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
