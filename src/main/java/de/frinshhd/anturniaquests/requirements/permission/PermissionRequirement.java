package de.frinshhd.anturniaquests.requirements.permission;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PermissionRequirement extends BasicRequirement {

    public PermissionRequirement(boolean notGenerated) {
        super("permissions", false);
    }

    @Override
    public void init(QuestsManager questsManager) {
        super.init(questsManager);
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<PermissionModel> permissionModels = new ArrayList<>();
        for (Object object : objects) {
            permissionModels.add((PermissionModel) object);
        }

        permissionModels.forEach(permissionModel -> {
            if (player.hasPermission(permissionModel.getPermission())) {
                lore.add(Translator.build("lore.requirements.permissions.fulfilled", new TranslatorPlaceholder("permission", String.valueOf(permissionModel.getPermission()))));
            } else {
                lore.add(Translator.build("lore.requirements.permissions.notFulfilled", new TranslatorPlaceholder("permission", String.valueOf(permissionModel.getPermission()))));
            }
        });
        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return PermissionModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        PermissionModel permissionModel = (PermissionModel) requirementModel;

        if (!player.hasPermission(permissionModel.getPermission())) {
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.permission", new TranslatorPlaceholder("permission", String.valueOf(permissionModel.getPermission()))));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        boolean hasPermission = true;

        for (Object rawRequirementModel : quest.getRequirement(getId())) {
            PermissionModel permissionModel = (PermissionModel) rawRequirementModel;

            if (!player.hasPermission(permissionModel.getPermission())) {
                hasPermission = false;
                break;
            }
        }

        return hasPermission;
    }

    @Override
    public void complete(Player player, BasicRequirementModel requirementModel) {}

}
