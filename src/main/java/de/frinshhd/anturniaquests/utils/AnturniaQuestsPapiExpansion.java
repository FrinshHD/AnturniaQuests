package de.frinshhd.anturniaquests.utils;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.mysql.MysqlManager;
import de.frinshhd.anturniaquests.mysql.entities.KilledEntities;
import de.frinshhd.anturniaquests.quests.models.Quest;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class AnturniaQuestsPapiExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return Main.getInstance().getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return Main.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return Main.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        params = params.toLowerCase();

        if (params.equals("questscompleted")) {
            return String.valueOf(Main.getQuestsManager().getQuestsCompletedCounter(player.getUniqueId()));
        }

        if (params.equals("killedentities")) {
            return String.valueOf(Main.getQuestsManager().getKilledEntitesCounter(player.getUniqueId()));
        }

        if (params.startsWith("killedentities_")) {
            String entityRaw = params.substring(15);
            entityRaw = entityRaw.toUpperCase();

            EntityType entity;

            try {
                entity = EntityType.valueOf(entityRaw);
            } catch (IllegalArgumentException e) {
                return null;
            }

            KilledEntities killedEntitiesSQL = MysqlManager.getKilledEntitiesPlayer(player.getUniqueId());
            if (killedEntitiesSQL == null) {
                return String.valueOf(0);
            }

            HashMap<String, Integer> killedEntities = killedEntitiesSQL.getKilledEntities();

            if (!killedEntities.containsKey(entity.toString())) {
                return String.valueOf(0);
            }

            return killedEntities.get(entity.toString()).toString();
        }

        if (params.startsWith("quest_")) {
            String questID = params.substring(6);
            Quest quest = Main.getQuestsManager().getQuest(questID);

            if (quest == null) {
                return null;
            }

            return String.valueOf(Main.getQuestsManager().getPlayerQuestCompletions(player.getUniqueId(), questID));
        }

        return null;
    }
}
