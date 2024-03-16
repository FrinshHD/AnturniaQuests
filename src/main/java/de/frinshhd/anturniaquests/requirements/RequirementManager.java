package de.frinshhd.anturniaquests.requirements;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.items.ItemModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.bukkit.Bukkit.getServer;

public class RequirementManager {

    private HashMap<String, BasicRequirement> requirements = new HashMap<>();

    public RequirementManager() {
        init();
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
                        Constructor<?> constructor = cls.getConstructors()[0];
                        BasicRequirement requirement = (BasicRequirement) constructor.newInstance();

                        requirements.put(requirement.getId(), requirement);
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                         InvocationTargetException | IllegalArgumentException | NoClassDefFoundError e) {
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

            if (! Main.getRequirementManager().getRequirement(stringArrayListEntry.getKey()).check(player, questID)) {
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

        quest.getRequirement("items").forEach(requirement -> {
            ItemModel itemModel = (ItemModel) requirement;

            itemModel.removeItemFromInventory(player);
        });
    }
}
