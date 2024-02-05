package de.frinshhd.anturniaquests.listener;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("quests.admin.updateNotify") && Main.version != null) {
            if (!Main.version.equals(Main.getInstance().getDescription().getVersion())) {
                player.sendMessage(Translator.build("updateAvailable", new TranslatorPlaceholder("newVersion", Main.version), new TranslatorPlaceholder("currentVersion", Main.getInstance().getDescription().getVersion())));
            }
        }
    }
}
