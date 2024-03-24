package de.frinshhd.anturniaquests.requirements;

import com.j256.ormlite.dao.Dao;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.Requirements;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.items.ItemModel;
import de.frinshhd.anturniaquests.utils.PlayerHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class RequirementManager implements Listener {

    public PlayerHashMap<UUID, JSONObject> playerRequirements = new PlayerHashMap<>();
    private HashMap<String, BasicRequirement> requirements = new HashMap<>();

    public RequirementManager(boolean notGenerated) {
        init();

        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void init() {
        load();
    }

    public void load() {
        String fullCanonicalName = Main.class.getCanonicalName();
        String canonicalName = fullCanonicalName.substring(0, fullCanonicalName.lastIndexOf("."));

        Reflections reflections = new Reflections(canonicalName, new SubTypesScanner(false));
        Set<String> classNames = reflections.getAll(new SubTypesScanner(false));

        for (String className : classNames) {
            if (className.contains(canonicalName)) {
                try {
                    Class<?> cls = Class.forName(className);

                    Class<BasicRequirement> requiremmentClass = BasicRequirement.class;

                    if (requiremmentClass.isAssignableFrom(cls)) {
                        Constructor<?> constructor = cls.getConstructor(boolean.class);
                        Object[] parameters = {true};

                        BasicRequirement requirement = (BasicRequirement) constructor.newInstance(parameters);

                        requirements.put(requirement.getId(), requirement);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         InvocationTargetException | IllegalArgumentException | NoClassDefFoundError |
                         NoSuchMethodException e) {
                    Main.getInstance().getLogger().warning("Error loading listeners in class " + className + " " + e);
                }
            }
        }
    }

    public BasicRequirement getRequirement(String id) {
        return requirements.get(id);
    }

    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Map.Entry<String, ArrayList<Object>> stringArrayListEntry : quest.getRequirements().entrySet()) {

            if (!Main.getRequirementManager().getRequirement(stringArrayListEntry.getKey()).check(player, questID)) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<BasicRequirement> getRequirements() {
        return new ArrayList<>(requirements.values());
    }


    public void sendPlayerMissing(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        quest.getRequirements().forEach((id, requirements) -> {
            requirements.forEach(requirement -> {
                BasicRequirementModel requirementModel = (BasicRequirementModel) requirement;
                requirementModel.getBasicRequirement().sendPlayerMissing(player, requirementModel);
            });
        });
    }

    public void complete(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        if (quest.getRequirement("items") != null) {
            quest.getRequirement("items").forEach(requirement -> {
                ItemModel itemModel = (ItemModel) requirement;

                itemModel.removeItemFromInventory(player);
            });
        }
    }

    public void playerJoin(Player player) {
        putPlayerPlayerRequirementsMap(player);
    }

    public void playerQuit(Player player) {
        savePlayerRequirementsToDB(player);
    }

    private void savePlayerRequirementsToDB(Player player) {
        Dao<Requirements, Long> requirementsDao;
        try {
            requirementsDao = MysqlManager.getRequirementsDao();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Requirements requirements;
        try {
            requirements = MysqlManager.getRequirementsPlayer(player.getUniqueId());

            if (requirements == null) {
                //Todo: throw error message because player has no requirements object registered in db
                return;
            }

            JSONObject object = Main.getRequirementManager().playerRequirements.get(player.getUniqueId());

            requirements.putStorylines(object);

            requirementsDao.update(requirements);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void putPlayerRequirement(UUID playerUUID, String requirementID, JSONObject requirementData) {
        JSONObject object = playerRequirements.get(playerUUID);
        object.put(requirementID, requirementData);

        playerRequirements.put(playerUUID, object);
    }

    public void putPlayerPlayerRequirementsMap(Player player) {
        UUID uuid = player.getUniqueId();

        JSONObject object = Objects.requireNonNull(MysqlManager.getRequirementsPlayer(uuid)).getStoryline();

        playerRequirements.put(uuid, object);
    }

    public JSONObject getPlayerRequirementData(UUID playerUUID, String requirementID) {
        if (!playerRequirements.get(playerUUID).has(requirementID)) {
            return new JSONObject();
        }

        return playerRequirements.get(playerUUID).getJSONObject(requirementID);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Main.getRequirementManager().playerJoin(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Main.getRequirementManager().playerQuit(player);
    }


}
