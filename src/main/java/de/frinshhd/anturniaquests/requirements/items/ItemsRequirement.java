package de.frinshhd.anturniaquests.requirements.items;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ItemsRequirement extends BasicRequirement {

    public ItemsRequirement() {
        super("items", false);
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<ItemModel> items = new ArrayList<>();
        for (Object object : objects) {
            items.add((ItemModel) object);
        }

        items.forEach(item -> {
            int amountInInv = item.getAmountInInventory(player);

            if (amountInInv >= item.getAmount()) {
                lore.add(Translator.build("lore.requirements.items.inInventory", new TranslatorPlaceholder("amountInInv", String.valueOf(amountInInv)), new TranslatorPlaceholder("amount", String.valueOf(item.getAmount())), new TranslatorPlaceholder("itemName", item.getDisplayName())));
            } else {
                lore.add(Translator.build("lore.requirements.items.notInInventory", new TranslatorPlaceholder("amountInInv", String.valueOf(amountInInv)), new TranslatorPlaceholder("amount", String.valueOf(item.getAmount())), new TranslatorPlaceholder("itemName", item.getDisplayName())));
            }
        });
        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return ItemModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        ItemModel itemModel = (ItemModel) requirementModel;
        int amountInInv = itemModel.getAmountInInventory(player);

        if (amountInInv < itemModel.getAmount()) {
            ChatManager.sendMessage(player, Translator.build("quest.missingRequirements.item", new TranslatorPlaceholder("amountInInv", String.valueOf(amountInInv)), new TranslatorPlaceholder("amount", String.valueOf(itemModel.getAmount())), new TranslatorPlaceholder("itemName", itemModel.getDisplayName())));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);

        for (Object rawRequirementModell : quest.getRequirement(getId())) {
            ItemModel itemModel = (ItemModel) rawRequirementModell;

            if (itemModel.getAmountInInventory(player) < itemModel.getAmount()) {
                return false;
            }
        }


        return true;
    }
}
