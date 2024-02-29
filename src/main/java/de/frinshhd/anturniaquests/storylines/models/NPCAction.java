package de.frinshhd.anturniaquests.storylines.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.ChatManager;import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCAction {

    @JsonProperty
    private String message = null;

    @JsonProperty
    private String command = null;

    @JsonProperty
    private Sound sound = null;

    @JsonProperty
    private Long delay = null;

    public String getMessage() {
        return this.message;
    }

    public String getCommand() {
        return this.command;
    }

    public Sound getSound() {
        return this.sound;
    }

    public void execute(Player player) {
        if (delay == null || delay <= 0L) {
            sendMessage(player);
            executeCommand(player);
            playSound(player);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                sendMessage(player);
                executeCommand(player);
                playSound(player);
                cancel();
            }
        }.runTaskLater(Main.getInstance(), delay);
    }

    public void sendMessage(Player player) {
        if (getMessage() == null) {
            return;
        }

        ChatManager.sendMessage(player, getMessage());
    }

    public void executeCommand(Player player) {
        if (getCommand() == null) {
            return;
        }

        String commandString = getCommand();

        if (commandString.charAt(0) == '/') {
            commandString = commandString.substring(1);
        }

        commandString = commandString.replace("%player%", player.getName());

        Main.getInstance().getServer().dispatchCommand(Main.getInstance().getServer().getConsoleSender(), commandString);
    }

    public void playSound(Player player) {
        if (getSound() == null) {
            return;
        }

        player.playSound(player.getLocation(), getSound(), 50, 1);
    }

}
