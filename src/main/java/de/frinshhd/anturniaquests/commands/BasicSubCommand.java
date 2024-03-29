package de.frinshhd.anturniaquests.commands;

import org.bukkit.command.CommandSender;

public abstract class BasicSubCommand {

    private String mainCommand;

    private String[] path;

    public BasicSubCommand(String mainCommand, String[] path) {
        this.mainCommand = mainCommand;
        this.path = path;
    }

    public String getMainCommand() {
        return mainCommand;
    }

    public String[] getPath() {
        return path;
    }

    public abstract boolean execute(CommandSender sender, String[] args);
}
