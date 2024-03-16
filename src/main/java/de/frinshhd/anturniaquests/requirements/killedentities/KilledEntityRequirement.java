package de.frinshhd.anturniaquests.requirements.killedentities;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.requirements.items.ItemModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;

public class KilledEntityRequirement extends BasicRequirement implements Listener {
    public KilledEntityRequirement() {
        super("killedEntities", false);

        //register listener
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<KilledEntityModell> killedEntities = new ArrayList<>();
        for (Object object : objects) {
            killedEntities.add((KilledEntityModell) object);
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
        return KilledEntityModell.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        KilledEntityModell killedEntityModell  = (KilledEntityModell) requirementModel;

        int amount;

        if (Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntityModell.getEntity().toString()) == null) {
            amount = 0;
        } else {
            amount = Main.getQuestsManager().playerKilledEntities.get(player.getUniqueId()).get(killedEntityModell.getEntity().toString());
        }

        if (amount < killedEntityModell.getAmount()) {
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.killedEntity", new TranslatorPlaceholder("amountKilled", String.valueOf(amount)), new TranslatorPlaceholder("amount", String.valueOf(killedEntityModell.getAmount())), new TranslatorPlaceholder("entityName", killedEntityModell.getName())));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Object rawRequirementModell : quest.getRequirement(getId())) {
            KilledEntityModell killedEntityModell = (KilledEntityModell) rawRequirementModell;

            if (Main.getQuestsManager().getKilledEntityAmount(player, killedEntityModell.getEntity()) < killedEntityModell.getAmount()) {
                return false;
            }
        }


        return true;
    }
}
