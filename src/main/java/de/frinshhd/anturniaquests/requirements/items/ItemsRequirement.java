package de.frinshhd.anturniaquests.requirements.items;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ItemsRequirement extends BasicRequirement {

    public ItemsRequirement(boolean notGenerated) {
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
                lore.add(TranslationManager.getInstance().build("lore.requirements.items.inInventory", new Translatable("amountInInv", String.valueOf(amountInInv)), new Translatable("amount", String.valueOf(item.getAmount())), new Translatable("itemName", item.getDisplayName())));
            } else {
                lore.add(TranslationManager.getInstance().build("lore.requirements.items.notInInventory", new Translatable("amountInInv", String.valueOf(amountInInv)), new Translatable("amount", String.valueOf(item.getAmount())), new Translatable("itemName", item.getDisplayName())));
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
            ChatManager.sendMessage(player, TranslationManager.getInstance().build("quest.missingRequirements.item", new Translatable("amountInInv", String.valueOf(amountInInv)), new Translatable("amount", String.valueOf(itemModel.getAmount())), new Translatable("itemName", itemModel.getDisplayName())));
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

    @Override
    public void complete(Player player, BasicRequirementModel requirementModel) {
    }
}
