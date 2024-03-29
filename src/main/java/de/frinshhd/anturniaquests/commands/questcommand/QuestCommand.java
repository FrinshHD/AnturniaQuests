package de.frinshhd.anturniaquests.commands.questcommand;

import de.frinshhd.anturniaquests.commands.BasicCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class QuestCommand extends BasicCommand {
    public QuestCommand() {
        super("quest");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }
}
