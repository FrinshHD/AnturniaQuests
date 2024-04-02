package de.frinshhd.anturniaquests.commands.questcommand;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.QuestMenu;
import de.frinshhd.anturniaquests.commands.BasicCommand;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestCommand extends BasicCommand {
    public QuestCommand() {
        super("quest", "anturniaquests.command.quests");
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (super.execute(sender, command, args)) {
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
                ChatManager.sendMessage(sender, Translator.build("quest.menuDisabled"));
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
}
