package de.frinshhd.anturniaquests.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SurvivalQuestSounds {

    public static void questComplete(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50.0F, 1.0F);
    }

    public static void questError(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 50.0F, 1.0F);
    }

}
