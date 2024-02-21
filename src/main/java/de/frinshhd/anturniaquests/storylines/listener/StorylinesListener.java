package de.frinshhd.anturniaquests.storylines.listener;

import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.Storylines;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class StorylinesListener implements Listener {

    public StorylinesListener(boolean notGenerated) {
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        JSONObject object = Objects.requireNonNull(MysqlManager.getStorylinesPlayer(uuid)).getStoryline();

        Main.getStorylinesManager().playerStats.put(uuid, object);
        System.out.println("6");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Dao<Storylines, Long> storylinesDao = null;
        try {
            storylinesDao = MysqlManager.getStorylinesDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Storylines storylines = null;
        try {
            storylines = storylinesDao.queryForEq("uuid", player.getUniqueId()).stream().toList().get(0);

            JSONObject object = Main.getStorylinesManager().playerStats.get(player.getUniqueId());

            storylines.putStorylines(object);

            storylinesDao.update(storylines);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("5");
    }

}
