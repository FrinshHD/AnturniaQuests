package de.frinshhd.anturniaquests.requirements.placeholder;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.quests.QuestsManager;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.BasicRequirement;
import de.frinshhd.anturniaquests.requirements.BasicRequirementModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlaceholderRequirement extends BasicRequirement {

    public PlaceholderRequirement(boolean notGenerated) {
        super("placeholders", false);
    }

    @Override
    public void init(QuestsManager questsManager) {
        super.init(questsManager);
    }

    @Override
    public ArrayList<String> getLore(Player player, ArrayList<Object> objects) {
        ArrayList<String> lore = new ArrayList<>();

        ArrayList<PlaceholderModel> placeholderModels = new ArrayList<>();
        for (Object object : objects) {
            placeholderModels.add((PlaceholderModel) object);
        }

        placeholderModels.forEach(placeholderModel -> {
            if (compare(player, placeholderModel)) {
                lore.add(TranslationManager.getInstance().build("lore.requirements.placeholders.fulfilled", new Translatable("name", String.valueOf(placeholderModel.getName()))));
            } else {
                lore.add(TranslationManager.getInstance().build("lore.requirements.placeholders.notFulfilled", new Translatable("name", String.valueOf(placeholderModel.getName()))));
            }
        });
        return lore;
    }

    @Override
    public Class<?> getModellClass() {
        return PlaceholderModel.class;
    }

    @Override
    public void sendPlayerMissing(Player player, BasicRequirementModel requirementModel) {
        PlaceholderModel placeholderModel = (PlaceholderModel) requirementModel;

        if (compare(player, placeholderModel)) {
            ChatManager.sendMessage(player, TranslationManager.getInstance().build("quests.missingRequirements.placeholders", new Translatable("name", String.valueOf(placeholderModel.getName()))));
        }
    }

    @Override
    public boolean check(Player player, String questID) {
        Quest quest = Main.getQuestsManager().getQuest(questID);
        boolean hasCompleted = true;

        for (Object rawRequirementModel : quest.getRequirement(getId())) {
            PlaceholderModel placeholderModel = (PlaceholderModel) rawRequirementModel;

            boolean compare = compare(player, placeholderModel);

            if (!compare) {
                hasCompleted = false;
                break;
            }
        }

        return hasCompleted;
    }

    private boolean compare(Player player, PlaceholderModel placeholderModel) {
        ComparisonType comparisonType = placeholderModel.getComparisonType();
        ComparionOperator comparisonOperator = placeholderModel.getComparisonOperator();
        String comparison = placeholderModel.getComparison();

        String value = PlaceholderAPI.setPlaceholders(player, placeholderModel.getPlaceholder());

        switch (comparisonType) {
            case INT:
                try {
                    int intValue = Integer.parseInt(value);
                    int intCompareValue = Integer.parseInt(comparison);

                    return switch (comparisonOperator) {
                        case EQUAL -> intValue == intCompareValue;
                        case NOT_EQUAL -> intValue != intCompareValue;
                        case GREATER_THAN -> intValue > intCompareValue;
                        case GREATER_THAN_OR_EQUAL -> intValue >= intCompareValue;
                        case LESS_THAN -> intValue < intCompareValue;
                        case LESS_THAN_OR_EQUAL -> intValue <= intCompareValue;
                    };
                } catch (NumberFormatException e) {
                    Main.getInstance().getLogger().warning("Invalid number format for comparison: " + value + " or " + comparison + ". Please check your quest's configuration.");
                    return false;
                }
            case DOUBLE:
                try {
                    double doubleValue = Double.parseDouble(value);
                    double doubleCompareValue = Double.parseDouble(comparison);

                    return switch (comparisonOperator) {
                        case EQUAL -> doubleValue == doubleCompareValue;
                        case NOT_EQUAL -> doubleValue != doubleCompareValue;
                        case GREATER_THAN -> doubleValue > doubleCompareValue;
                        case GREATER_THAN_OR_EQUAL -> doubleValue >= doubleCompareValue;
                        case LESS_THAN -> doubleValue < doubleCompareValue;
                        case LESS_THAN_OR_EQUAL -> doubleValue <= doubleCompareValue;
                    };
                } catch (NumberFormatException e) {
                    Main.getInstance().getLogger().warning("Invalid number format for comparison: " + value + " or " + comparison + ". Please check your quest's configuration.");
                    return false;
                }
            case LONG:
                try {
                    long longValue = Long.parseLong(value);
                    long longCompareValue = Long.parseLong(comparison);

                    return switch (comparisonOperator) {
                        case EQUAL -> longValue == longCompareValue;
                        case NOT_EQUAL -> longValue != longCompareValue;
                        case GREATER_THAN -> longValue > longCompareValue;
                        case GREATER_THAN_OR_EQUAL -> longValue >= longCompareValue;
                        case LESS_THAN -> longValue < longCompareValue;
                        case LESS_THAN_OR_EQUAL -> longValue <= longCompareValue;
                    };
                } catch (NumberFormatException e) {
                    Main.getInstance().getLogger().warning("Invalid number format for comparison: " + value + " or " + comparison + ". Please check your quest's configuration.");
                    return false;
                }
            case STRING:

                return switch (comparisonOperator) {
                    case EQUAL -> value.equals(comparison);
                    case NOT_EQUAL -> !value.equals(comparison);
                    case GREATER_THAN -> value.compareTo(comparison) > 0;
                    case GREATER_THAN_OR_EQUAL -> value.compareTo(comparison) >= 0;
                    case LESS_THAN -> value.compareTo(comparison) < 0;
                    case LESS_THAN_OR_EQUAL -> value.compareTo(comparison) <= 0;
                };
            default:
                return false;
        }
    }

    @Override
    public void complete(Player player, BasicRequirementModel requirementModel) {
    }

}
