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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class StorylinesManager {

    public LinkedHashMap<String, Storyline> storylines;
    public PlayerHashMap<UUID, JSONObject> playerStats = new PlayerHashMap<>();

    //key: storylineID, value: list of current players completing this storyline
    public HashMap<String, PlayerArrayList<UUID>> storylineCurrentPlayers = new HashMap<>();

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
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading storylines.yml. AnturniaQuests will be disabled!\nError " + e.getMessage());
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
        }

        String folderPath = "plugins/AnturniaQuests/storylines";
        File folder = new File(folderPath);

        if (folder.exists()) {
            ArrayList<File> filesToLoad = new ArrayList<>();
            LinkedHashMap<String, Storyline> folderStorylines = new LinkedHashMap<>();

            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (!file.isFile()) {
                    continue;
                }

                if (!file.getName().endsWith(".yml")) {
                    continue;
                }

                if (file.length() == 0) {
                    continue;
                }

                filesToLoad.add(file);
            }

            for (File file : filesToLoad) {
                try {
                    LinkedHashMap<String, Storyline> storylines = mapper.readValue(file, mapTypeQuests);
                    folderStorylines.putAll(storylines);
                } catch (IOException e) {
                    Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading " + file.getName() + ". AnturniaQuests will be disabled!\nError " + e.getMessage());
                    Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
                    return;
                }
            }

            this.storylines.putAll(folderStorylines);
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

    public ArrayList<Player> getStorylineCurrentPlayers(String storylineID) {
        if (!this.storylineCurrentPlayers.containsKey(storylineID)) {
            return new ArrayList<>();
        }

        ArrayList<Player> players = new ArrayList<>();

        this.storylineCurrentPlayers.get(storylineID).forEach(uuid -> {
            Player player = Main.getInstance().getServer().getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                return;
            }

            players.add(player);
        });

        return players;
    }

    public void addPlayerCurrentStoryline(String storylineID, Player player) {
        PlayerArrayList<UUID> players = new PlayerArrayList<>();

        if (this.storylineCurrentPlayers.containsKey(storylineID)) {
            players = this.storylineCurrentPlayers.get(storylineID);
        }

        players.add(player.getUniqueId());
    }

    public void removePlayerCurrentStoryline(String storylineID, Player player) {
        PlayerArrayList<UUID> players;

        if (this.storylineCurrentPlayers.containsKey(storylineID)) {
            players = this.storylineCurrentPlayers.get(storylineID);
            players.remove(player.getUniqueId());
            this.storylineCurrentPlayers.put(storylineID, players);
        }
    }

    public void npcClick(Player player, String npcID) {
        String storylineID = null;

        for (Map.Entry<String, Storyline> stringStorylineEntry : storylines.entrySet()) {
            Storyline storyline = stringStorylineEntry.getValue();

            if (storyline.getNPC(npcID) != null) {
                storylineID = stringStorylineEntry.getKey();
                break;
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

        //check for stages

        ArrayList<Integer> playerCompletedStages = getPlayerCompletedStages(player, storylineID);
        int playerCurrentActionID = playerStorylineStats.getInt("currentAction");

        int playerStageID = playerStorylineStats.getInt("currentStage");
        //NPC realNpc = storyline.getNPC(npcID);
        NPC npc = null;

        for (NPC storylineNpc : storyline.getNpcs()) {
            if (!storylineNpc.getNpcID().equals(npcID)) {
                continue;
            }

            if (getPlayerCompletedStages(player, storylineID).contains(getNpcStageID(storylineID, storylineNpc))) {
                continue;
            }

            npc = storylineNpc;
            break;
        }

        if (npc == null) {
            ChatManager.sendMessage(player, Translator.build("storyline.falseStageNPC.alreadyCompleted"));
            return;
        }

        NPC npcClicked = storyline.getNPCStageID(playerStageID);

        //check if currentStage of the player is the same as the one of the npc
        if (!npcClicked.getNpcID().equals(npcID)) {
            if (npc.getGroup() == null) {
                ChatManager.sendMessage(player, Translator.build("storyline.falseStageNPC"));
                return;
            }

            if (getPlayerCurrentNpcStageID(player, storylineID) != -1 && getPlayerCurrentNpcStageID(player, storylineID) != getNpcStageID(storylineID, npc)) {
                ChatManager.sendMessage(player, Translator.build("storyline.falseStageNPC.finishCurrent", new TranslatorPlaceholder("npcName", storyline.getNPCStageID(getPlayerCurrentNpcStageID(player, storylineID)).getName())));
                return;
            }

            //if the first npc has no group
            if (getPlayerCompletedStages(player, storylineID).isEmpty()) {
                if (storyline.getNPCStageID(0).getGroup() == null || !storyline.getNPCStageID(0).getGroup().equals(npc.getGroup())) {
                    ChatManager.sendMessage(player, Translator.build("storyline.falseStageNPC"));
                    return;
                }
            } else {
                if (getPlayerCompletedStages(player, storylineID).contains(getNpcStageID(storylineID, npc))) {
                    ChatManager.sendMessage(player, Translator.build("storyline.falseStageNPC"));
                    return;
                }

                NPC npcBefore = storyline.getNPCStageID(getPlayerCompletedStages(player, storylineID).get(getPlayerCompletedStages(player, storylineID).size() - 1));

                if (npcBefore.getGroup() == null ||
                        !npcBefore.getGroup().equals(npc.getGroup())) {
                    if (storyline.getNPCStageID(getNpcStageID(storylineID, npcBefore) + 1).getGroup() == null ||
                            !storyline.getNPCStageID(getNpcStageID(storylineID, npcBefore) + 1).getGroup().equals(npc.getGroup())) {
                        ChatManager.sendMessage(player, Translator.build("storyline.falseStageNPC"));
                        return;
                    }
                }
            }

        }


        //check if counter has to start //Todo
        if (storyline.getTimeToComplete() > -1) {
            if (playerStageID == 0 && playerCurrentActionID == 0) {
                putPlayerStartTime(player, storylineID, -1);
            }

            if (getPlayerStartTime(player, storylineID) == -1) {
                putPlayerStartTime(player, storylineID, System.currentTimeMillis());
            }
        }

        //check if counter for the stage has to start //Todo
        if (npc.getTimeToComplete() > -1) {
            if (playerCurrentActionID == 0) {
                putPlayerStageStartTime(player, storylineID, -1);
            }

            if (getPlayerStageStartTime(player, storylineID) == -1) {
                putPlayerStageStartTime(player, storylineID, System.currentTimeMillis());
            }
        }

        //check if player needs to be added to the list of current players completing this quest
        if (storyline.getMaxCurrentPlayers() > -1) {
            //already too much players completing
            if (getStorylineCurrentPlayers(storylineID).size() >= storyline.getMaxCurrentPlayers()) {
                ChatManager.sendMessage(player, Translator.build("storyline.tooManyCurrentPlayers", new TranslatorPlaceholder("storylineName", storyline.getName())));
                return;
            }

            //add player to current players
            addPlayerCurrentStoryline(storylineID, player);
        }

        ArrayList<NPCAction> actions = npc.getActions();

        NPCAction playerCurrentAction = actions.get(playerCurrentActionID);

        boolean executed = playerCurrentAction.execute(player);

        if (!executed) {
            return;
        }

        if (getPlayerCurrentNpcStageID(player, storylineID) == -1) {
            putPlayerCurrentNpcStageID(player, storylineID, getNpcStageID(storylineID, npc));
        }

        playerCurrentActionID += 1;

        putPlayerCurrentAction(player, storylineID, playerCurrentActionID);

        //check if player has completed this npc
        if (actions.size() - 1 < playerCurrentActionID) {
            playerStageID += 1;

            //check if player has Completed the storyline
            checkPlayerCompletedStoryline(player, storylineID, storyline, playerCompletions, playerStageID);
        }
    }

    private void checkPlayerCompletedStoryline(Player player, String storylineID, Storyline storyline, int playerCompletions, int playerStageID) {
        if (storyline.getNpcs().size() - 1 < playerStageID) {
            //if player already completed
            putPlayerCurrentAction(player, storylineID, 0);
            putPlayerCurrentStage(player, storylineID, 0);
            putPlayerCompletions(player, storylineID, playerCompletions + 1);
            putPlayerLastCompletion(player, storylineID, System.currentTimeMillis());
            putPlayerStartTime(player, storylineID, -1);
            putPlayerStageStartTime(player, storylineID, -1);
            putPlayerCurrentNpcStageID(player, storylineID, -1);
            resetPlayerCompletedStages(player, storylineID);

            removePlayerCurrentStoryline(storylineID, player);
        } else {
            //else set player to next stageID
            putPlayerCurrentAction(player, storylineID, 0);
            putPlayerCurrentStage(player, storylineID, playerStageID);
            addPlayerCompletedStage(player, storylineID, getPlayerCurrentNpcStageID(player, storylineID));
            putPlayerCurrentNpcStageID(player, storylineID, -1);
        }
    }

    private void resetPlayerStoryline(Player player, String storylineID) {
        putPlayerCurrentStage(player, storylineID, 0);
        putPlayerCurrentAction(player, storylineID, 0);
        putPlayerStartTime(player, storylineID, -1);
        putPlayerStageStartTime(player, storylineID, -1);
        putPlayerLastCompletion(player, storylineID, System.currentTimeMillis());
        resetPlayerCompletedStages(player, storylineID);
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

    public JSONObject getEmptyStoryline() {
        JSONObject object = new JSONObject();
        object.put("completions", 0);
        object.put("lastCompletion", 0);
        object.put("currentStage", 0);
        object.put("currentAction", 0);
        object.put("currentStartTime", -1);
        object.put("currentStageStartTime", -1);
        object.put("currentNpcStageID", -1);
        object.put("completedStages", arrayListToJsonArray(new ArrayList<Integer>()));

        return object;
    }

    public JSONObject getPlayerStoryline(Player player, String storylineID) {
        if (!getPlayerStats(player).has(storylineID)) {
            return getEmptyStoryline();
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
        return getStorylineStats(player, storylineID, "currentStage", 0);
    }

    public int getPlayerCurrentNpcStageID(Player player, String storylineID) {
        int defaultValue = -1;
        return getStorylineStats(player, storylineID, "currentNpcStageID", -1);
    }

    public long getStorylineStats(Player player, String storylineID, String key, long defaultLong) {
        Object defaultValue = defaultLong;
        return Long.parseLong(getStorylineStats(player, storylineID, key, defaultValue).toString());
    }

    public int getStorylineStats(Player player, String storylineID, String key, int defaultInt) {
        Object defaultValue = defaultInt;
        return (Integer) getStorylineStats(player, storylineID, key, defaultValue);
    }

    public JSONArray getStorylineStats(Player player, String storylineID, String key, JSONArray defaultArray) {
        Object defaultValue = defaultArray;
        return (JSONArray) getStorylineStats(player, storylineID, key, defaultValue);
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

    public void putPlayerCurrentNpcStageID(Player player, String storylineID, int npcStageID) {
        putStorylineStats(player, storylineID, "currentNpcStageID", npcStageID);
    }

    public void addPlayerCompletedStage(Player player, String storylineID, int stageID) {
        ArrayList<Integer> completedStages = getPlayerCompletedStages(player, storylineID);
        completedStages.add(stageID);

        JSONArray array = arrayListToJsonArray(completedStages);

        putStorylineStats(player, storylineID, "completedStages", array);
    }

    public JSONArray arrayListToJsonArray(ArrayList<Integer> list) {
        JSONArray jsonArray = new JSONArray();
        for (Integer i : list) {
            jsonArray.put(i);
        }
        return jsonArray;
    }

    public void resetPlayerCompletedStages(Player player, String storylineID) {
        putStorylineStats(player, storylineID, "completedStages", arrayListToJsonArray(new ArrayList<Integer>()));
    }

    public ArrayList<Integer> getPlayerCompletedStages(Player player, String storylineID) {
        JSONArray array = getStorylineStats(player, storylineID, "completedStages", new JSONArray());

        return jsonArrayToArrayList(array);
    }

    public ArrayList<Integer> jsonArrayToArrayList(JSONArray jsonArray) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i = 0; i < len; i++) {
                list.add((Integer) jsonArray.get(i));
            }
        }
        return list;
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
            storylines = MysqlManager.getStorylinesPlayer(player.getUniqueId());

            if (storylines == null) {
                //Todo: throw error message because player has no storylines object registered in db
                return;
            }

            JSONObject object = Main.getStorylinesManager().playerStats.get(player.getUniqueId());

            storylines.putStorylines(object);

            storylinesDao.update(storylines);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removePlayerStoryline(Player player, String storylineID) {
        removePlayerCurrentStoryline(storylineID, player);

        UUID uuid = player.getUniqueId();

        JSONObject object = playerStats.get(uuid);
        JSONObject storylineObject = getEmptyStoryline();

        object.put(storylineID, storylineObject);
        playerStats.put(uuid, object);
    }

    public ArrayList<Integer> getPlayerMissingNPCsFromGroup(Player player, String storylineID, String groupID) {
        ArrayList<Integer> missingStages = new ArrayList<>();
        ArrayList<Integer> completedStages = getPlayerCompletedStages(player, storylineID);

        int index = 0;
        for (NPC npc : Main.getStorylinesManager().getStoryline(storylineID).getNpcs()) {
            if (npc.getGroup().equals(groupID) && !completedStages.contains(index)) {
                missingStages.add(index);
            }

            index++;
        }

        return missingStages;
    }

    public void resetPlayerStorylines(Player player) {
        for (Map.Entry<String, PlayerArrayList<UUID>> stringPlayerArrayListEntry : storylineCurrentPlayers.entrySet()) {
            String storylineID = stringPlayerArrayListEntry.getKey();
            PlayerArrayList<UUID> players = stringPlayerArrayListEntry.getValue();

            players.remove(player.getUniqueId());

            storylineCurrentPlayers.put(storylineID, players);
        }

        playerStats.put(player.getUniqueId(), new JSONObject());
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

                        if (getPlayerStartTime(player, storylineID) > -1) {
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

                        if (getPlayerStageStartTime(player, storylineID) > -1) {
                            long playerStageStartTime = getPlayerStageStartTime(player, storylineID);

                            int playerStageID = getPlayerStageID(player, storylineID);

                            if (getPlayerStageID(player, storylineID) > 0 && getPlayerStoryline(player, storylineID).getInt("currentAction") == 0) {
                                playerStageID = getPlayerStageID(player, storylineID) - 1;
                            }

                            NPC npc = storyline.getNPCStageID(playerStageID);
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

    public int getNpcStageID(String storylineID, NPC npc) {
        int index = 0;
        for (NPC npc1 : getStoryline(storylineID).getNpcs()) {
            if (npc1.equals(npc)) {
                return index;
            }

            index++;
        }

        return -1;
    }

}
