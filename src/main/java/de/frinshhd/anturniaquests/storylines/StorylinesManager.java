package de.frinshhd.anturniaquests.storylines;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.Storylines;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.storylines.listener.CitizensNpcsListener;
import de.frinshhd.anturniaquests.storylines.listener.FancyNpcsListener;
import de.frinshhd.anturniaquests.storylines.listener.StorylinesListener;
import de.frinshhd.anturniaquests.storylines.models.NPC;
import de.frinshhd.anturniaquests.storylines.models.Storyline;
import de.frinshhd.anturniaquests.utils.MessageFormat;
import de.frinshhd.anturniaquests.utils.PlayerHashMap;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class StorylinesManager {

    public LinkedHashMap<String, Storyline> storylines;
    public PlayerHashMap<UUID, JSONObject> playerStats = new PlayerHashMap<>();


    public StorylinesManager() {
        if (Main.getInstance().getServer().getPluginManager().getPlugin("Citizens") != null && Main.getInstance().getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {

            new CitizensNpcsListener(true);
        }

        if (Main.getInstance().getServer().getPluginManager().getPlugin("FancyNpcs") != null && Main.getInstance().getServer().getPluginManager().getPlugin("FancyNpcs").isEnabled()) {
            new FancyNpcsListener(true);
        }

        new StorylinesListener(true);
        load();
    }

    public void load() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        TypeFactory typeFactory = mapper.getTypeFactory();
        MapType mapTypeQuests = typeFactory.constructMapType(LinkedHashMap.class, String.class, Storyline.class);

        try {
            this.storylines = mapper.readValue(new FileInputStream("plugins/AnturniaQuests/storylines.yml"), mapTypeQuests);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Storyline getStoryline(String storylineID) {
        return this.storylines.get(storylineID);
    }

    public String getStorylineID(Storyline storyline) {

        for (Map.Entry<String, Storyline> stringStorylineEntry : storylines.entrySet()) {
            if (stringStorylineEntry.getValue().equals(storyline)) {
                return stringStorylineEntry.getKey();
            }
        }

        return null;
    }

    public void npcClick(Player player, String npcID) {
        String storylineID = null;

        for (Map.Entry<String, Storyline> stringStorylineEntry : storylines.entrySet()) {
            Storyline storyline = stringStorylineEntry.getValue();

            if (storyline.getNPC(npcID) != null) {
                storylineID = stringStorylineEntry.getKey();
            }
        }

        if (storylineID == null) {
            return;
        }

        Storyline storyline = getStoryline(storylineID);
        JSONObject playerStorylineStats = getPlayerStoryline(player, storylineID);

        int playerCompletions = playerStorylineStats.getInt("completions");
        int storylineMaxCompletions = storyline.getMaxCompletions();

        //check if player already completed the quest for the max amount
        if (storylineMaxCompletions > -1 && playerCompletions >= storylineMaxCompletions) {
            //Todo: tell player that he can't complete the quests any longer
            return;
        }

        long playerLastCompletion = playerStorylineStats.getLong("lastCompletion");
        long storylineCooldown = storyline.getCooldown();

        //check if a cooldown for this quest is active
        if (playerLastCompletion + storylineCooldown > System.currentTimeMillis()) {
            //Todo: tell player that he needs to wait until he can start the quest next
            return;
        }


        int playerStageID = playerStorylineStats.getInt("currentStage");
        NPC npc = storyline.getNPCStageID(playerStageID);

        //check if currentStage of the player is the same as the one of the npc
        if (!npc.getNpcID().equals(npcID)) {
            //Todo: tell player that he can't access the current npc because he needs to talk to other npcs before
            return;
        }

        //check if counter has to start //Todo
        if (storyline.timeToComplete() > -1) {
            if (getPlayerStartTime(player, storylineID) == -1) {
                putPlayerStartTime(player, storylineID, System.currentTimeMillis());
            }
        }

        int playerCurrentMessage = playerStorylineStats.getInt("currentMessage");
        ArrayList<String> messages = npc.getMessages();

        if (messages.size() - 1 < playerCurrentMessage && npc.getQuest() != null) {
            //Todo: tell player the quest he has to do
            Quest quest = npc.getQuest();

            boolean completedQuest;
            try {
                completedQuest = quest.playerClick(player, true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (!completedQuest) {
                return;
            }

            //check if player has Completed the storyline
            playerStageID += 1;
            checkPlayerCompletedStoryline(player, storylineID, storyline, playerCompletions, playerStageID);
            return;
        }

        player.sendMessage(MessageFormat.build(messages.get(playerCurrentMessage)));
        playerCurrentMessage += 1;

        //check if player has completed this npc
        if (messages.size() - 1 < playerCurrentMessage && npc.getQuest() == null) {
            playerStageID += 1;

            //check if player has Completed the storyline
            checkPlayerCompletedStoryline(player, storylineID, storyline, playerCompletions, playerStageID);
        } else {
            putPlayerCurrentMessage(player, storylineID, playerCurrentMessage);
        }
    }

    private void checkPlayerCompletedStoryline(Player player, String storylineID, Storyline storyline, int playerCompletions, int playerStageID) {
        if (storyline.getNpcs().size() - 1 < playerStageID) {
            putPlayerCurrentMessage(player, storylineID, 0);
            putPlayerCurrentStage(player, storylineID, 0);
            putPlayerCompletions(player, storylineID, playerCompletions + 1);
            putPlayerLastCompletion(player, storylineID, System.currentTimeMillis());
            putPlayerStartTime(player, storylineID, -1);
        } else {
            putPlayerCurrentMessage(player, storylineID, 0);
            putPlayerCurrentStage(player, storylineID, playerStageID);
        }
    }

    public JSONObject getPlayerStats(Player player) {
        return playerStats.get(player.getUniqueId());
    }

    public JSONObject getPlayerStoryline(Player player, String storylineID) {
        if (!getPlayerStats(player).has(storylineID)) {
            JSONObject object = new JSONObject();
            object.put("completions", 0);
            object.put("lastCompletion", 0);
            object.put("currentStage", 0);
            object.put("currentMessage", 0);
            object.put("currentStartTime", -1);
            return object;
        }

        return getPlayerStats(player).getJSONObject(storylineID);
    }

    public long getPlayerStartTime(Player player, String storylineID) {
        return (long) getStorylineStats(player, storylineID, "currentStartTime", -1L);
    }

    public Object getStorylineStats(Player player, String storylineID, String key, Object defaultValue) {
        JSONObject object = getPlayerStoryline(player, storylineID);
        if (!object.has(key)) {
            return defaultValue;
        }

        return object.get(key);
    }

    public void putPlayerCurrentMessage(Player player, String storylineID, int currentMessage) {
        putStorylineStats(player, storylineID, "currentMessage", currentMessage);
    }

    public void putPlayerCurrentStage(Player player, String storylineID, int currentStage) {
        putStorylineStats(player, storylineID, "currentStage", currentStage);
    }

    public void putPlayerCompletions(Player player, String storylineID, int completions) {
        putStorylineStats(player, storylineID, "completions", completions);
    }

    public void putPlayerLastCompletion(Player player, String storylineID, long lastCompletion) {
        putStorylineStats(player, storylineID, "lastCompletion", lastCompletion);
    }

    public void putPlayerStartTime(Player player, String storylineID, long startTime) {
        putStorylineStats(player, storylineID, "currentStartTime", startTime);
    }

    public void putStorylineStats(Player player, String storylineID, String key, Object value) {
        UUID uuid = player.getUniqueId();

        JSONObject object = playerStats.get(uuid);
        JSONObject storylineObject = getPlayerStoryline(player, storylineID);

        storylineObject.put(key, value);

        object.put(storylineID, storylineObject);
        playerStats.put(uuid, object);
    }

    public void playerJoin(Player player) {
        putPlayerPlayerStatsMap(player);
    }

    public void playerQuit(Player player) {
        savePlayerStatsToDB(player);
    }

    public void putPlayerPlayerStatsMap(Player player) {
        UUID uuid = player.getUniqueId();

        JSONObject object = Objects.requireNonNull(MysqlManager.getStorylinesPlayer(uuid)).getStoryline();

        Main.getStorylinesManager().playerStats.put(uuid, object);
    }

    public void savePlayerStatsToDB(Player player) {
        Dao<Storylines, Long> storylinesDao;
        try {
            storylinesDao = MysqlManager.getStorylinesDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Storylines storylines;
        try {
            storylines = storylinesDao.queryForEq("uuid", player.getUniqueId()).stream().toList().get(0);

            JSONObject object = Main.getStorylinesManager().playerStats.get(player.getUniqueId());

            storylines.putStorylines(object);

            storylinesDao.update(storylines);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
