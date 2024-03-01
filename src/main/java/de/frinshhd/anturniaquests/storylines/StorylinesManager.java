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
import de.frinshhd.anturniaquests.storylines.models.NPCAction;
import de.frinshhd.anturniaquests.storylines.models.Storyline;
import de.frinshhd.anturniaquests.utils.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class StorylinesManager {

    public LinkedHashMap<String, Storyline> storylines;
    public PlayerHashMap<UUID, JSONObject> playerStats = new PlayerHashMap<>();

    public BukkitTask runnable = null;


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
            ChatManager.sendMessage(player, Translator.build("storyline.alreadyCompleted", new TranslatorPlaceholder("storylineName", storyline.getName())));
            return;
        }

        long playerLastCompletion = playerStorylineStats.getLong("lastCompletion");
        long storylineCooldown = storyline.getCooldown();

        //check if a cooldown for this quest is active
        if (playerLastCompletion + storylineCooldown > System.currentTimeMillis()) {
            ChatManager.sendMessage(player, Translator.build("storyline.cooldownActive", new TranslatorPlaceholder("cooldown", String.valueOf((playerLastCompletion + storylineCooldown - System.currentTimeMillis()) / 1000))));
            return;
        }


        int playerStageID = playerStorylineStats.getInt("currentStage");
        NPC npc = storyline.getNPCStageID(playerStageID);

        //check if currentStage of the player is the same as the one of the npc
        if (!npc.getNpcID().equals(npcID)) {
            ChatManager.sendMessage(player, Translator.build("storyline.falseStageNPC"));
            return;
        }

        //check if counter has to start //Todo
        if (storyline.getTimeToComplete() > -1) {
            if (getPlayerStartTime(player, storylineID) == -1) {
                putPlayerStartTime(player, storylineID, System.currentTimeMillis());
            }
        }

        //check if counter for the stage has to start //Todo
        if (npc.getTimeToComplete() > -1) {
            if (getPlayerStageStartTime(player, storylineID) == -1) {
                putPlayerStageStartTime(player, storylineID, System.currentTimeMillis());
            }
        }

        int playerCurrentActionID = playerStorylineStats.getInt("currentAction");
        ArrayList<NPCAction> actions = npc.getActions();

        if (actions.size() - 1 < playerCurrentActionID && npc.getQuest() != null) {
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

        NPCAction playerCurrentAction = actions.get(playerCurrentActionID);

        playerCurrentAction.execute(player);

        playerCurrentActionID += 1;

        //check if player has completed this npc
        if (actions.size() - 1 < playerCurrentActionID && npc.getQuest() == null) {
            playerStageID += 1;

            //check if player has Completed the storyline
            checkPlayerCompletedStoryline(player, storylineID, storyline, playerCompletions, playerStageID);
        } else {
            putPlayerCurrentAction(player, storylineID, playerCurrentActionID);
        }
    }

    private void checkPlayerCompletedStoryline(Player player, String storylineID, Storyline storyline, int playerCompletions, int playerStageID) {
        if (storyline.getNpcs().size() - 1 < playerStageID) {
            putPlayerCurrentAction(player, storylineID, 0);
            putPlayerCurrentStage(player, storylineID, 0);
            putPlayerCompletions(player, storylineID, playerCompletions + 1);
            putPlayerLastCompletion(player, storylineID, System.currentTimeMillis());
            putPlayerStartTime(player, storylineID, -1);
        } else {
            putPlayerCurrentAction(player, storylineID, 0);
            putPlayerCurrentStage(player, storylineID, playerStageID);
        }
    }

    private void resetPlayerStoryline(Player player, String storylineID) {
        putPlayerCurrentStage(player, storylineID, 0);
        putPlayerCurrentAction(player, storylineID, 0);
        putPlayerStartTime(player, storylineID, -1);
        putPlayerStageStartTime(player, storylineID, -1);
        putPlayerLastCompletion(player, storylineID, System.currentTimeMillis());
    }

    public JSONObject getPlayerStats(Player player) {
        return playerStats.get(player.getUniqueId());
    }

    public List<JSONObject> getPlayerAllStorylines(Player player) {
        List<JSONObject> object = new ArrayList<>();

        for (String storylineID : this.playerStats.get(player.getUniqueId()).keySet()) {
            object.add(getPlayerStoryline(player, storylineID));
        }

        return object;
    }

    public JSONObject getPlayerStoryline(Player player, String storylineID) {
        if (!getPlayerStats(player).has(storylineID)) {
            JSONObject object = new JSONObject();
            object.put("completions", 0);
            object.put("lastCompletion", 0);
            object.put("currentStage", 0);
            object.put("currentAction", 0);
            object.put("currentStartTime", -1);
            object.put("currentStageStartTime", -1);
            return object;
        }

        return getPlayerStats(player).getJSONObject(storylineID);
    }

    public long getPlayerStartTime(Player player, String storylineID) {
        return getStorylineStats(player, storylineID, "currentStartTime", -1L);
    }

    public long getPlayerStageStartTime(Player player, String storylineID) {
        return getStorylineStats(player, storylineID, "currentStageStartTime", -1L);
    }

    public int getPlayerStageID(Player player, String storylineID) {
        int defaultValue = 0;
        return (int) getStorylineStats(player, storylineID, "currentStage", 0);
    }

    public long getStorylineStats(Player player, String storylineID, String key, long defaultLong) {
        Object defaultValue = defaultLong;
        return Long.parseLong(getStorylineStats(player, storylineID, key, defaultValue).toString());
    }

    public int getStorylineStats(Player player, String storylineID, String key, int defaultInt) {
        Object defaultValue = defaultInt;
        return (Integer) getStorylineStats(player, storylineID, key, defaultValue);
    }

    public Object getStorylineStats(Player player, String storylineID, String key, Object defaultValue) {
        JSONObject object = getPlayerStoryline(player, storylineID);
        if (!object.has(key)) {
            return defaultValue;
        }

        if (object.get(key) == null) {
            return defaultValue;
        }

        return object.get(key);
    }

    public void putPlayerCurrentAction(Player player, String storylineID, int currentAction) {
        putStorylineStats(player, storylineID, "currentAction", currentAction);
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

    public void putPlayerStageStartTime(Player player, String storylineID, long startTime) {
        putStorylineStats(player, storylineID, "currentStageStartTime", startTime);
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

        if (Main.getStorylinesManager().runnable == null) {
            Main.getStorylinesManager().startRunnable();
        }
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

    //Todo: start runnable logic
    public void startRunnable() {
        this.runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.getInstance().getServer().getOnlinePlayers().isEmpty()) {
                    cancel();
                    runnable = null;
                    return;
                }

                for (UUID uuid : playerStats.keySet()) {
                    Player player = Main.getInstance().getServer().getPlayer(uuid);

                    assert player != null;
                    for (String storylineID : playerStats.get(player.getUniqueId()).keySet()) {
                        Storyline storyline = getStoryline(storylineID);

                        if (getPlayerStartTime(player, storylineID) == -1) {
                            continue;
                        } else {
                            long playerStartTime = getPlayerStartTime(player, storylineID);
                            long storylineTimeToComplete = storyline.getTimeToComplete();

                            if (storylineTimeToComplete > -1) {
                                if (playerStartTime + storylineTimeToComplete < System.currentTimeMillis()) {
                                    //Todo: tell player that he didn't completed the storyline in the required time; cancel / reset the storyline
                                    ChatManager.sendMessage(player, Translator.build("storyline.timeOut", new TranslatorPlaceholder("storylineName", storyline.getName())));
                                    resetPlayerStoryline(player, storylineID);
                                }
                            }
                        }

                        if (getPlayerStageStartTime(player, storylineID) == -1) {
                            continue;
                        } else {
                            long playerStageStartTime = getPlayerStartTime(player, storylineID);
                            NPC npc = storyline.getNPCStageID(getPlayerStageID(player, storylineID));
                            long stageTimeToComplete = npc.getTimeToComplete();

                            if (stageTimeToComplete > -1) {
                                if (playerStageStartTime + stageTimeToComplete < System.currentTimeMillis()) {
                                    //Todo: tell player that he didn't completed the stage in the required time; cancel / reset the storyline
                                    ChatManager.sendMessage(player, Translator.build("storyline.timeOut.npc", new TranslatorPlaceholder("npcName", npc.getName()), new TranslatorPlaceholder("storylineName", storylineID)));
                                    resetPlayerStoryline(player, storylineID);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }

}
