package de.frinshhd.anturniaquests.requirements.mobbreed;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MobBreedRequirement extends BasicRequirement implements Listener {

    public MobBreedRequirement(boolean notGenerated) {
        super("mobBreed", false);

        //register listener
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public void init(QuestsManager questsManager) {
        super.init(questsManager);
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<MobBreedModel> interactions = new ArrayList<>();
        for (Object object : objects) {
            interactions.add((MobBreedModel) object);
        }

        interactions.forEach(interaction -> {
            if (getPlayerMobBreed(player.getUniqueId(), interaction.getEntity()) >= interaction.getAmount()) {
                lore.add(Translator.build("lore.requirements.mobBreed.fulfilled", new TranslatorPlaceholder("entityName", interaction.getName()), new TranslatorPlaceholder("amountBred", String.valueOf(getPlayerMobBreed(player.getUniqueId(), interaction.getEntity()))), new TranslatorPlaceholder("amount", String.valueOf(interaction.getAmount()))));
            } else {
                lore.add(Translator.build("lore.requirements.mobBreed.notFulfilled", new TranslatorPlaceholder("entityName", interaction.getName()), new TranslatorPlaceholder("amountBred", String.valueOf(getPlayerMobBreed(player.getUniqueId(), interaction.getEntity()))), new TranslatorPlaceholder("amount", String.valueOf(interaction.getAmount()))));
            }
        });
        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return MobBreedModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        MobBreedModel interaction = (MobBreedModel) requirementModel;

        if (getPlayerMobBreed(player.getUniqueId(), interaction.getEntity()) < interaction.getAmount()) {
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.mobBreed",
                    new TranslatorPlaceholder("entityName", interaction.getName()),
                    new TranslatorPlaceholder("amountBred", String.valueOf(getPlayerMobBreed(player.getUniqueId(), interaction.getEntity()))),
                    new TranslatorPlaceholder("amount", String.valueOf(interaction.getAmount()))));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Object rawRequirementModell : quest.getRequirement(getId())) {
            MobBreedModel mobBreedModel = (MobBreedModel) rawRequirementModell;

            if (getPlayerMobBreed(player.getUniqueId(), mobBreedModel.getEntity()) < mobBreedModel.getAmount()) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player player)) {
            return;
        }

        Mob mob = (Mob) event.getEntity();

        addPlayerMobBreed(player.getUniqueId(), mob.getType());
    }

    public void addPlayerMobBreed(UUID playerUUID, EntityType entityType) {
        Gson gson = Main.getGson();
        JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

        HashMap<String, Integer> entities;

        Type mapType = new TypeToken<HashMap<String, Integer>>() {
        }.getType();

        if (requirementsData.isEmpty()) {
            entities = new HashMap<>();
        } else {
            entities = gson.fromJson(requirementsData.toString(), mapType);
        }

        if (!entities.containsKey(entityType.toString())) {
            entities.put(entityType.toString(), 1);
            Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), new JSONObject(entities));
            return;
        }

        entities.put(entityType.toString(), entities.get(entityType.toString()) + 1);

        Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), new JSONObject(entities));
    }

    public int getPlayerMobBreed(UUID playerUUID, EntityType entityType) {
        Gson gson = Main.getGson();
        JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

        HashMap<String, Integer> entities;

        Type mapType = new TypeToken<HashMap<String, Integer>>() {
        }.getType();

        if (requirementsData.isEmpty()) {
            entities = new HashMap<>();
        } else {
            entities = gson.fromJson(requirementsData.toString(), mapType);
        }

        if (!entities.containsKey(entityType.toString())) {
            return 0;
        }

        return entities.get(entityType.toString());
    }

    @Override
    public void complete(Player player, BasicRequirementModel requirementModel) {
        MobBreedModel mobBreedModel = (MobBreedModel) requirementModel;
        UUID playerUUID = player.getUniqueId();

        switch (mobBreedModel.getResetType()) {
            case NONE -> {
                break;
            }
            case ONLY_AMOUNT -> {
                Gson gson = Main.getGson();
                JSONObject requirementsData = Main.getRequirementManager().getPlayerRequirementData(playerUUID, getId());

                HashMap<String, Integer> bredMobs;
                Type mapType = new TypeToken<HashMap<String, Integer>>() {
                }.getType();

                if (requirementsData.isEmpty()) {
                    return;
                } else {
                    bredMobs = gson.fromJson(requirementsData.toString(), mapType);
                }

                String entityKey = mobBreedModel.getEntity().toString();

                if (bredMobs.containsKey(entityKey)) {
                    int currentCount = bredMobs.get(entityKey);
                    int newCount = currentCount - mobBreedModel.getAmount();

                    if (newCount > 0) {
                        bredMobs.put(entityKey, newCount);
                    } else {
                        bredMobs.remove(entityKey);
                    }

                    Main.getRequirementManager().putPlayerRequirement(playerUUID, getId(), new JSONObject(bredMobs));
                }
            }
            case COMPLETE -> {
                Main.getRequirementManager().putPlayerRequirement(player.getUniqueId(), getId(), new JSONObject());
            }
        }
    }
}
