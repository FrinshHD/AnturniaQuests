package de.frinshhd.anturniaquests.commands.questcommand;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.QuestMenu;
import de.frinshhd.anturniaquests.commands.BasicCommand;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestCommand extends BasicCommand {
    public QuestCommand() {
        super("quests", "anturniaquests.command.quests");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (super.execute(sender, commandLabel, args)) {
            return true;
        }

        if (args.length == 0) {

            if (!(sender instanceof Player player)) {
                //send help message
                Main.getCommandManager().getSubCommand(this, "help").execute(sender, new String[]{});
                return false;
            }

            if (!Main.getConfigManager().getConfig().questMenuEnabled) {
                //send message that quest menu is disabled
                ChatManager.sendMessage(sender, Translator.build("quests.menuDisabled"));
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

        //send help message
        Main.getCommandManager().getSubCommand(this, "help").execute(sender, new String[]{});
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }
}
