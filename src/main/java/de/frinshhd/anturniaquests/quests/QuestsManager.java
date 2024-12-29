package de.frinshhd.anturniaquests.quests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.KilledEntities;
import de.frinshhd.anturniaquests.mysql.entities.Quests;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.MapConverter;
import de.frinshhd.anturniaquests.utils.PlayerHashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class QuestsManager {

    public LinkedHashMap<String, Quest> questsRaw;
    public LinkedHashMap<String, Quest> quests;
    public PlayerHashMap<UUID, HashMap<String, Integer>> playerKilledEntities;

    public QuestsManager() {
        quests = new LinkedHashMap<>();
        questsRaw = new LinkedHashMap<>();
        playerKilledEntities = new PlayerHashMap<>();

        this.load();
    }

    public static boolean addItem(Player player, ItemStack item, int amount) {
        int index = 0;
        while (amount > index) {
            player.getInventory().addItem(item);
            index++;
        }

        return true;
    }

    /**
     * Search and register quests
     */
    public void load() {
        Yaml yaml = Main.getYaml();
        Gson gson = Main.getGson();

        try (FileInputStream inputStream = new FileInputStream("plugins/AnturniaQuests/quests.yml")) {
            LinkedHashMap<String, Object> yamlData = yaml.load(inputStream);
            String jsonString = gson.toJson(yamlData);
            Map<String, Quest> tempMap = gson.fromJson(jsonString, new TypeToken<Map<String, Quest>>() {
            }.getType());
            this.questsRaw = new LinkedHashMap<>(tempMap);
        } catch (IOException e) {
            Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading quests.yml. AnturniaQuests will be disabled!\nError " + e.getMessage());
            Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
            return;
        }

        String folderPath = "plugins/AnturniaQuests/quests";
        File folder = new File(folderPath);

        if (folder.exists()) {
            ArrayList<File> filesToLoad = new ArrayList<>();
            LinkedHashMap<String, Quest> folderQuestsRaw = new LinkedHashMap<>();

            for (File file : folder.listFiles()) {
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
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    LinkedHashMap<String, Object> fileYamlData = yaml.load(fileInputStream);
                    String fileJsonString = gson.toJson(fileYamlData);
                    Map<String, Quest> tempFileMap = gson.fromJson(fileJsonString, new TypeToken<Map<String, Quest>>() {
                    }.getType());
                    folderQuestsRaw.putAll(tempFileMap);
                } catch (IOException e) {
                    Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while reading " + file.getName() + ". AnturniaQuests will be disabled!\nError " + e.getMessage());
                    Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
                    return;
                }
            }

            this.questsRaw.putAll(folderQuestsRaw);
        }

        this.quests = new LinkedHashMap<>(this.questsRaw);

        quests.values().forEach(quest -> {
            quest.getRequirements().forEach((id, modelList) -> {
                ArrayList<Object> models = new ArrayList<>();
                for (Object requirement : modelList) {

                    if (Main.getRequirementManager().getRequirement(id) == null) {
                        return;
                    }

                    LinkedTreeMap<String, Object> treeMap = (LinkedTreeMap<String, Object>) requirement;
                    LinkedHashMap<String, Object> map = MapConverter.convertLinkedTreeMapToLinkedHashMap(treeMap);


                    Class<?> cls = Main.getRequirementManager().getRequirement(id).getModellClass();
                    if (cls == null) {
                        return;
                    }

                    Main.getInstance().getLogger().info(map.toString());

                    try {
                        Constructor<?> constructor = cls.getConstructor(LinkedHashMap.class);

                        Object[] parameters = {map};

                        Main.getInstance().getLogger().info("Requirement " + id + " loaded");
                        Main.getInstance().getLogger().info(Arrays.toString(parameters));

                        System.out.println(Arrays.toString(parameters));

                        BasicRequirementModel basicRequirementModel = (BasicRequirementModel) constructor.newInstance(parameters);

                        models.add(basicRequirementModel);

                    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                             IllegalAccessException | NullPointerException e) {
                        Main.getInstance().getLogger().severe(ChatColor.RED + "An error occurred while loading the requirements. AnturniaQuests will be disabled!\nError " + e.getMessage());
                        e.printStackTrace();
                        Main.getInstance().getServer().getPluginManager().disablePlugin(Main.getInstance());
                    }


                    if (id.equals("money")) {
                        break;
                    }
                }

                quest.setRequirement(id, models);
            });
        });

        for (BasicRequirement requirement : Main.getRequirementManager().getRequirements()) {
            requirement.init(this);
        }
    }

    public Quest getQuest(String questID) {
        return this.quests.get(questID);
    }

    public String getQuestID(Quest quest) {

        for (Map.Entry<String, Quest> stringQuestEntry : quests.entrySet()) {
            if (stringQuestEntry.getValue().equals(quest)) {
                return stringQuestEntry.getKey();
            }
        }

        return null;
    }

    public void addKilledEntity(Player player, EntityType killedEntity) {
        UUID uuid = player.getUniqueId();

        if (!playerKilledEntities.containsKey(uuid)) {
            playerKilledEntities.put(uuid, new HashMap<>());
        }

        HashMap<String, Integer> killedEntities = playerKilledEntities.get(uuid);
        assert killedEntities != null;

        if (!killedEntities.containsKey(killedEntity.toString())) {
            killedEntities.put(killedEntity.toString(), 1);
        } else {
            killedEntities.put(killedEntity.toString(), killedEntities.get(killedEntity.toString()) + 1);
        }
        playerKilledEntities.put(uuid, killedEntities);
    }

    public int getKilledEntityAmount(Player player, EntityType entityType) {
        HashMap<String, Integer> map = playerKilledEntities.get(player.getUniqueId());

        if (!map.containsKey(entityType.toString())) {
            return 0;
        }

        return map.get(entityType.toString());
    }

    public void playerJoin(Player player) {
        putPlayerKilledEntitiesToMap(player);
    }

    public void playerQuit(Player player) {
        savePlayerKilledEntitiesToDB(player);
    }

    public void putPlayerKilledEntitiesToMap(Player player) {
        KilledEntities killedEntities = MysqlManager.getKilledEntitiesPlayer(player.getUniqueId());
        assert killedEntities != null;

        Main.getQuestsManager().playerKilledEntities.put(player.getUniqueId(), killedEntities.getKilledEntities());
    }

    public void savePlayerKilledEntitiesToDB(Player player) {
        Dao<KilledEntities, Long> killedEntitiesDao = null;
        try {
            killedEntitiesDao = MysqlManager.getKilledEntityDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        KilledEntities killedEntities = null;
        try {
            killedEntities = killedEntitiesDao.queryForEq("uuid", player.getUniqueId()).stream().toList().get(0);

            HashMap<String, Integer> map = Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId());

            for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
                killedEntities.putKilledEntity(stringIntegerEntry.getKey(), stringIntegerEntry.getValue());
            }


            killedEntitiesDao.update(killedEntities);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPlayerQuestCompletions(Player player, String questID) {
        return getPlayerQuestCompletions(player.getUniqueId(), questID);
    }

    public int getPlayerQuestCompletions(UUID playerUUID, String questID) {
        Quests playerQuest = MysqlManager.getQuestPlayer(playerUUID);

        if (playerQuest == null) {
            return 0;
        }

        if (!playerQuest.getFinishedQuests().containsKey(questID)) {
            return 0;
        }

        return playerQuest.getFinishedQuests().get(questID);
    }

    public int getQuestsCompletedCounter(UUID playerUUID) {
        Quests playerQuest = MysqlManager.getQuestPlayer(playerUUID);

        if (playerQuest == null) {
            return 0;
        }

        int counter = 0;
        for (Integer value : playerQuest.getFinishedQuests().values()) {
            counter += value;
        }

        return counter;
    }

    public int getKilledEntitesCounter(UUID playerUUID) {
        KilledEntities killedEntities = MysqlManager.getKilledEntitiesPlayer(playerUUID);

        if (killedEntities == null) {
            return 0;
        }

        int counter = 0;
        for (Integer value : killedEntities.getKilledEntities().values()) {
            counter += value;
        }

        return counter;
    }

    public void deleteQuest(String questID) {
        questsRaw.remove(questID);

        Gson gson = Main.getGson();


        Yaml yaml = Main.getYaml();

        try {
            String jsonString = gson.toJson(questsRaw);
            LinkedHashMap<String, Object> yamlData = gson.fromJson(jsonString, new TypeToken<LinkedHashMap<String, Object>>() {
            }.getType());
            String yamlString = yaml.dump(yamlData);

            File file = new File("plugins/AnturniaQuests/quests.yml");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(yamlString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        load();
    }

    public void saveQuestToYml(String questID, Quest quest) {
        questsRaw.put(questID, quest);

        Gson gson = Main.getGson();
        Yaml yaml = Main.getYaml();

        try {
            String jsonString = gson.toJson(questsRaw);
            LinkedHashMap<String, Object> yamlData = gson.fromJson(jsonString, new TypeToken<LinkedHashMap<String, Object>>() {
            }.getType());
            String yamlString = yaml.dump(yamlData);

            File file = new File("plugins/AnturniaQuests/quests.yml");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(yamlString);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        load();
    }

    public Quest getEditableQuest(String questID) {
        return this.questsRaw.get(questID);
    }
}


