package de.frinshhd.anturniaquests.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BasicSubCommand {

    private final String mainCommand;
    private final String permission;
    private final String[] path;
    private boolean hidden = false;

    private String description = null;

    public BasicSubCommand(String mainCommand, String permission, String[] path) {
        this.mainCommand = mainCommand;
        this.permission = permission;
        this.path = path;
    }

    public String getMainCommand() {
        return mainCommand;
    }

    public String[] getPath() {
        return path;
    }

    /**
     * Returns the command path as a string.
     * <p>
     * This method retrieves the command path, which is an array of strings, and converts it into a single string.
     * The resulting string is a concatenation of all the elements in the path array, separated by spaces.
     * The leading and trailing square brackets from the array's string representation are removed, and commas are replaced with spaces.
     *
     * @return The command path as a string.
     */
    public String getCommand() {
        String path = Arrays.toString(getPath());

        return path.substring(1, path.length() - 1).replace(",", "");
    }

    public String getCommandFull() {
        return getMainCommand() + " " + getCommand();
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

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public abstract boolean execute(CommandSender sender, String commandLabel, String[] args);

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (getPermission() != null && !sender.hasPermission(getPermission())) {
            return null;
        }

        return new ArrayList<>();
    }
}
