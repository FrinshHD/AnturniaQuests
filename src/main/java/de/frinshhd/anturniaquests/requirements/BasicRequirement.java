package de.frinshhd.anturniaquests.requirements;

import de.frinshhd.anturniaquests.quests.QuestsManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicRequirement {

    private final List<BasicRequirementModel> loadedRequirementModels = new ArrayList<>();

    private final String id;
    protected boolean indexing;

    public BasicRequirement(String id, boolean indexing) {
        this.id = id;
        this.indexing = indexing;
    }

    public void init(QuestsManager questsManager) {
        if (indexing) {
            questsManager.quests.forEach((questID, quest) -> {
                if (!quest.getRequirements().containsKey(getId())) {
                    return;
                }

                quest.getRequirement(getId()).forEach(rawRequirementModel -> {
                    loadedRequirementModels.add((BasicRequirementModel) rawRequirementModel);
                });
            });
        }
    }


    public String getId() {
        return this.id;
    }

    public abstract ArrayList<String> getLore(Player player, ArrayList<Object> objects);

    public abstract Class<?> getModellClass();

    public abstract void sendPlayerMissing(Player player, BasicRequirementModel requirementModel);

    public abstract boolean check(Player player, String questID);

    public List<BasicRequirementModel> getLoadedRequirementModels() {
        return loadedRequirementModels;
    }
}
