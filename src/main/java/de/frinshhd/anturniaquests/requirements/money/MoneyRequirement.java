package de.frinshhd.anturniaquests.requirements.money;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.requirements.killedentities.KilledEntityModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.*;

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
                lore.add(Translator.build("lore.requirements.money.fulfilled", new TranslatorPlaceholder("amount", String.valueOf(moneyModel.getAmount()))));
            } else {
                lore.add(Translator.build("lore.requirements.money.notFulfilled", new TranslatorPlaceholder("amount", String.valueOf(moneyModel.getAmount()))));
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
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.money", new TranslatorPlaceholder("amount", String.valueOf(moneyModel.getAmount()))));
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
    public void complete(Player player, BasicRequirementModel requirementModel) { }

    public boolean hasPlayerMoney(UUID uuid, double amount) {
        if (Main.getEconomy() != null) {
            return Main.getEconomy().getBalance(Bukkit.getOfflinePlayer(uuid)) >= amount;
        }
        return false;
    }

}
