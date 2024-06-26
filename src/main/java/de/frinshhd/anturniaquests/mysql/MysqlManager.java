package de.frinshhd.anturniaquests.mysql;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.entities.KilledEntities;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import de.frinshhd.anturniaquests.mysql.entities.Requirements;
import de.frinshhd.anturniaquests.mysql.entities.Storylines;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    public static Dao<KilledEntities, Long> getKilledEntityDao() throws SQLException {
        return DaoManager.createDao(connectionSource, KilledEntities.class);
    }

    public static Dao<Storylines, Long> getStorylinesDao() throws SQLException {
        return DaoManager.createDao(connectionSource, Storylines.class);
    }

    public static Dao<Requirements, Long> getRequirementsDao() throws SQLException {
        return DaoManager.createDao(connectionSource, Requirements.class);
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
            TableUtils.createTableIfNotExists(connectionSource, KilledEntities.class);
            TableUtils.createTableIfNotExists(connectionSource, Requirements.class);

            if (Main.isStorylinesEnabled()) {
                TableUtils.createTableIfNotExists(connectionSource, Storylines.class);
            }
        } catch (SQLException e) {
            //Todo logging
        }
    }

    public static Quests getQuestPlayer(UUID uuid) {
        Dao<Quests, Long> questsDao;
        try {
            questsDao = getQuestDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Quests> quests;
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

    public static KilledEntities getKilledEntitiesPlayer(UUID uuid) {
        Dao<KilledEntities, Long> killedEntitiesDao;
        try {
            killedEntitiesDao = getKilledEntityDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<KilledEntities> killedEntities;
        try {
            killedEntities = killedEntitiesDao.queryForEq("uuid", uuid);
        } catch (SQLException e) {
            return null;
        }

        if (killedEntities.isEmpty()) {
            return null;
        }

        return killedEntities.get(0);
    }

    public static Storylines getStorylinesPlayer(UUID uuid) {
        Dao<Storylines, Long> storylinesDao;
        try {
            storylinesDao = getStorylinesDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Storylines> storylines;
        try {
            storylines = storylinesDao.queryForEq("uuid", uuid);
        } catch (SQLException e) {
            return null;
        }

        if (storylines.isEmpty()) {
            return null;
        }

        return storylines.get(0);
    }

    public static Requirements getRequirementsPlayer(UUID uuid) {
        Dao<Requirements, Long> requirementsDao;
        try {
            requirementsDao = getRequirementsDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<Requirements> requirements;
        try {
            requirements = requirementsDao.queryForEq("uuid", uuid);
        } catch (SQLException e) {
            return null;
        }

        if (requirements.isEmpty()) {
            return null;
        }

        return requirements.get(0);
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPLayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //create quests
        if (MysqlManager.getQuestPlayer(player.getUniqueId()) == null) {
            Dao<Quests, Long> questDao;
            try {
                questDao = MysqlManager.getQuestDao();
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

        //create killedEntities
        if (MysqlManager.getKilledEntitiesPlayer(player.getUniqueId()) == null) {
            Dao<KilledEntities, Long> killedEntitiesDao;
            try {
                killedEntitiesDao = MysqlManager.getKilledEntityDao();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            KilledEntities killedEntities = new KilledEntities();
            killedEntities.create(player.getUniqueId());
            try {
                killedEntitiesDao.create(killedEntities);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        //create requirements
        if (MysqlManager.getRequirementsPlayer(player.getUniqueId()) == null) {
            Dao<Requirements, Long> requirementsDao;
            try {
                requirementsDao = MysqlManager.getRequirementsDao();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Requirements requirements = new Requirements();
            requirements.create(player.getUniqueId());
            try {
                requirementsDao.create(requirements);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        //create storylines
        if (Main.isStorylinesEnabled()) {
            if (MysqlManager.getStorylinesPlayer(player.getUniqueId()) == null) {
                Dao<Storylines, Long> storylinesDao;
                try {
                    storylinesDao = MysqlManager.getStorylinesDao();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Storylines storylines = new Storylines();
                storylines.create(player.getUniqueId());
                try {
                    storylinesDao.create(storylines);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
