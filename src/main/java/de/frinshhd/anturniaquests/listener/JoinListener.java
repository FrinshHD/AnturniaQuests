package de.frinshhd.anturniaquests.listener;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("anturniaquests.admin.updateNotify") && Main.version != null) {
            if (!Main.version.equals(Main.getInstance().getDescription().getVersion())) {
                ChatManager.sendMessage(player, Translator.build("updateAvailable", new TranslatorPlaceholder("newVersion", Main.version), new TranslatorPlaceholder("currentVersion", Main.getInstance().getDescription().getVersion())));
            }
        }

        Main.getQuestsManager().playerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Main.getQuestsManager().playerQuit(player);
    }
}
