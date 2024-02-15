package de.frinshhd.anturniaquests.mysql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MysqlManager implements Listener {

    public static JdbcConnectionSource connectionSource;

    public static Dao<Quests, Long> getQuestDao() throws SQLException {
        return DaoManager.createDao(connectionSource, Quests.class);
    }

    public static void connect(String url) {
        connect(url, null, null);
    }

    public static void connect(String url, String userName, String password) {
        if (userName == null && password == null) {
            try {
                connectionSource = new JdbcConnectionSource(url);
            } catch (SQLException e) {
                createNewDatabase();
                connect(url, userName, password);
            }
        } else {
            try {
                connectionSource = new JdbcConnectionSource(url, userName, password);
            } catch (SQLException e) {
                //Todo: logging
            }
        }

        try {
            TableUtils.createTableIfNotExists(connectionSource, Quests.class);
        } catch (SQLException e) {
            //Todo logging
        }
    }

    public static Quests getQuestPlayer(UUID uuid) {
        Dao<Quests, Long> questsDao = null;
        try {
            questsDao = getQuestDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Quests> quests = null;
        try {
            quests = questsDao.queryForEq("uuid", uuid);
        } catch (SQLException e) {
            return null;
        }

        if (quests.isEmpty()) {
            return null;
        }

        return quests.get(0);
    }

    public static void disconnect() throws Exception {
        connectionSource.close();
    }

    /*public static void checkConnection() throws Exception {
        if (connectionSource.isOpen("Quests")) {
            return;
        }

        disconnect();
        connect();
    } */

    public static void createNewDatabase() {

        String url = "jdbc:sqlite:plugins/AnturniaQuests/sqlite.db";

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                /*System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created."); */
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPLayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (MysqlManager.getQuestPlayer(player.getUniqueId()) == null) {
            Dao<Quests, Long> questDao = null;
            try {
                questDao = getQuestDao();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Quests quest = new Quests();
            quest.create(player.getUniqueId());
            try {
                questDao.create(quest);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
