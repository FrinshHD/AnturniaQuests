package de.frinshhd.anturniaquests.listener;

import de.frinshhd.anturniaquests.Main;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerKillEntityListener implements Listener {

    @EventHandler
    public void onPlayerKillEntity(EntityDeathEvent event) {
        //check if entity was killed by another entity
        if (event.getEntity().getKiller() == null) {
            return;
        }

        //check if entity was killed by a player
        if (!event.getEntity().getKiller().getType().equals(EntityType.PLAYER)) {
            return;
        }

        Player player = event.getEntity().getKiller();

        Main.getQuestsManager().addKilledEntity(player, event.getEntity().getType());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Main.getQuestsManager().playerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Main.getQuestsManager().playerQuit(player);
    }
}
