package de.frinshhd.anturniaquests.requirements.money;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class MoneyRequirement extends BasicRequirement {

    public MoneyRequirement(boolean notGenerated) {
        super("money", false);
    }

    @Override
    public void init(QuestsManager questsManager) {
        super.init(questsManager);
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<MoneyModel> moneyModels = new ArrayList<>();
        for (Object object : objects) {
            moneyModels.add((MoneyModel) object);
        }

        moneyModels.forEach(moneyModel -> {
            if (hasPlayerMoney(player.getUniqueId(), moneyModel.getAmount())) {
                lore.add(TranslationManager.getInstance().build("lore.requirements.money.fulfilled", new Translatable("amount", String.valueOf(moneyModel.getAmount()))));
            } else {
                lore.add(TranslationManager.getInstance().build("lore.requirements.money.notFulfilled", new Translatable("amount", String.valueOf(moneyModel.getAmount()))));
            }
        });
        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return MoneyModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        MoneyModel moneyModel = (MoneyModel) requirementModel;

        if (!hasPlayerMoney(player.getUniqueId(), moneyModel.getAmount())) {
            ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.missingRequirements.money", new Translatable("amount", String.valueOf(moneyModel.getAmount()))));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        double amount = 0.0;

        for (Object rawRequirementModel : quest.getRequirement(getId())) {
            MoneyModel moneyModel = (MoneyModel) rawRequirementModel;

            amount += moneyModel.getAmount();

            boolean hasMoney = hasPlayerMoney(player.getUniqueId(), amount);

            if (!hasMoney) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void complete(Player player, BasicRequirementModel requirementModel) {
    }

    public boolean hasPlayerMoney(UUID uuid, double amount) {
        if (Main.getEconomy() != null) {
            return Main.getEconomy().getBalance(Bukkit.getOfflinePlayer(uuid)) >= amount;
        }
        return false;
    }

}
