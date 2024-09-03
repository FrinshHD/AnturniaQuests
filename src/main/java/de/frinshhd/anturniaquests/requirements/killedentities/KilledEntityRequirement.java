package de.frinshhd.anturniaquests.requirements.killedentities;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.KilledEntities;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class KilledEntityRequirement extends BasicRequirement implements Listener {
    public KilledEntityRequirement(boolean notGenerated) {
        super("killedEntities", false);

        //register listener
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<KilledEntityModel> killedEntities = new ArrayList<>();
        for (Object object : objects) {
            killedEntities.add((KilledEntityModel) object);
        }

        killedEntities.forEach(killedEntity -> {

            int amount;

            if (Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()) == null) {
                amount = 0;
            } else if (Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntity.getEntity().toString()) == null) {
                amount = 0;
            } else {
                amount = Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntity.getEntity().toString());
            }

            if (amount >= killedEntity.getAmount()) {
                lore.add(Translator.build("lore.requirements.killedEntities.fulfilled", new TranslatorPlaceholder("amountKilled", String.valueOf(amount)), new TranslatorPlaceholder("amount", String.valueOf(killedEntity.getAmount())), new TranslatorPlaceholder("entityName", killedEntity.getName())));
            } else {
                lore.add(Translator.build("lore.requirements.killedEntities.notFulfilled", new TranslatorPlaceholder("amountKilled", String.valueOf(amount)), new TranslatorPlaceholder("amount", String.valueOf(killedEntity.getAmount())), new TranslatorPlaceholder("entityName", killedEntity.getName())));
            }
        });

        return lore;
    }

    @EventHandler
    public void onPlayerKillEntity(EntityDeathEvent event) {
        //check if entity was killed by another entity
        if (event.getEntity().getKiller() == null) {
            return;
        }

        //check if entity was killed by a player
        if (!event.getEntity().getKiller().getType().equals(EntityType.PLAYER)) {
            return;
        }

        Player player = event.getEntity().getKiller();

        Main.getQuestsManager().addKilledEntity(player, event.getEntity().getType());
    }

    @Override
    public Class<?> getModellClass() {
        return KilledEntityModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        KilledEntityModel killedEntityModel = (KilledEntityModel) requirementModel;

        int amount;

        if (Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntityModel.getEntity().toString()) == null) {
            amount = 0;
        } else {
            amount = Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntityModel.getEntity().toString());
        }

        if (amount < killedEntityModel.getAmount()) {
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.killedEntity", new TranslatorPlaceholder("amountKilled", String.valueOf(amount)), new TranslatorPlaceholder("amount", String.valueOf(killedEntityModel.getAmount())), new TranslatorPlaceholder("entityName", killedEntityModel.getName())));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Object rawRequirementModell : quest.getRequirement(getId())) {
            KilledEntityModel killedEntityModel = (KilledEntityModel) rawRequirementModell;

            if (Main.getQuestsManager().getKilledEntityAmount(player, killedEntityModel.getEntity()) < killedEntityModel.getAmount()) {
                return false;
            }
        }


        return true;
    }

    @Override
    public void complete(Player player, BasicRequirementModel requirementModel) {
        KilledEntityModel killedEntityModel = (KilledEntityModel) requirementModel;
        UUID playerUUID = player.getUniqueId();

        switch (killedEntityModel.getResetType()) {
            case NONE -> {
                break;
            }
            case ONLY_AMOUNT -> {
                KilledEntities dbKilledEntities = MysqlManager.getKilledEntitiesPlayer(playerUUID);
                if (dbKilledEntities == null) {
                    return;
                }

                if (!Main.getQuestsManager().playerKilledEntities.containsKey(playerUUID)) {
                    break;
                }

                HashMap<String, Integer> killedEntities = Main.getQuestsManager().playerKilledEntities.get(playerUUID);
                String entityKey = killedEntityModel.getEntity().toString();

                if (killedEntities.containsKey(entityKey)) {
                    int currentCount = killedEntities.get(entityKey);
                    int newCount = currentCount - killedEntityModel.getAmount();

                    if (newCount > 0) {
                        killedEntities.put(entityKey, newCount);
                    } else {
                        killedEntities.remove(entityKey);
                    }

                    Main.getQuestsManager().playerKilledEntities.put(playerUUID, killedEntities);
                }
            }
            case COMPLETE -> {
                if (!Main.getQuestsManager().playerKilledEntities.containsKey(playerUUID)) {
                    break;
                }

                Main.getQuestsManager().playerKilledEntities.put(playerUUID, new HashMap<>());
            }
        }
    }
}
