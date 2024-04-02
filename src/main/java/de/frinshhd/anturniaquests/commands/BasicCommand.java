package de.frinshhd.anturniaquests.commands;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class BasicCommand implements CommandExecutor {

    private final String command;

    private final String permission;

    private String description = null;

    public BasicCommand(String command, String permission) {
        this.command = command;
        this.permission = permission;
    }

    public String getCommand() {
        return command;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    /**
     * Do not use this method, use execute instead!
     *
     * @param sender  The sender of the command
     * @param command The command
     * @param label   The label
     * @param args    The arguments
     * @return true if the command was executed successfully
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            ChatManager.sendMessage(sender, Translator.build("noPermission"));
            return false;
        }

        return execute(sender, command, args);
    }


    /**
     * Executes the command.
     * <p>
     * This method is called when a command is executed. It first retrieves the subcommand associated with this command and the provided arguments.
     * If a subcommand is found, it is executed with the provided sender and arguments.
     * If no subcommand is found, the method returns false, indicating that the command was not executed successfully.
     *
     * @param sender  The sender of the command.
     * @param command The command to be executed.
     * @param args    The arguments of the command.
     * @return true if the command or subcommand was executed successfully, false otherwise.
     */
    public boolean execute(CommandSender sender, Command command, String[] args) {
        BasicSubCommand subCommand = Main.getCommandManager().getSubCommand(this, args);

        if (subCommand != null) {
            return subCommand.execute(sender, args);
        }

        // first layer logic comes here

        return false;
    }
}