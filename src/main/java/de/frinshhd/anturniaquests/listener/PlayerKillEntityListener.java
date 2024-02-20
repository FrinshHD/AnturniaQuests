package de.frinshhd.anturniaquests.listener;

import com.google.common.util.concurrent.Service;
import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.KilledEntities;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

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

        KilledEntities killedEntities = MysqlManager.getKilledEntitiesPlayer(player.getUniqueId());
        assert killedEntities != null;

        Main.getQuestsManager().playerKilledEntities.put(player.getUniqueId(), killedEntities.getKilledEntities());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Dao<KilledEntities, Long> killedEntitiesDao = null;
        try {
            killedEntitiesDao = MysqlManager.getKilledEntityDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        KilledEntities killedEntities = null;
        try {
            killedEntities = killedEntitiesDao.queryForEq("uuid", player.getUniqueId()).stream().toList().get(0);

            Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).forEach(killedEntities::addKilledEntity);

            killedEntitiesDao.update(killedEntities);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
