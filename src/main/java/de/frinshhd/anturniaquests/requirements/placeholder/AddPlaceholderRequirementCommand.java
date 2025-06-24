package de.frinshhd.anturniaquests.requirements.placeholder;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.translations.Translatable;
import de.frinshhd.anturniaquests.utils.translations.TranslationManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AddPlaceholderRequirementCommand extends BasicSubCommand {

    public AddPlaceholderRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement",
                new String[]{
                        "edit",
                        "<questID>",
                        "add",
                        "requirement",
                        "placeholder",
                        "<placeholder>",
                        "<comparisonType>",
                        "<comparisonOperator>",
                        "<comparison>",
                        "[name]"
                });
        setDescription("Adds a placeholder requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 9) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "requirement", "placeholder"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quests.dontExists", new Translatable("questID", questID)));
            return true;
        }

        String placeholder = args[5];
        String comparisonTypeRaw = args[6];
        String comparisonOperatorRaw = args[7];
        String comparison = args[8];

        String name = null;
        if (args.length >= 10) {
            name = args[9];
        }

        ComparisonType comparisonType = null;

        try {
            comparisonType = ComparisonType.valueOf(comparisonTypeRaw);
        } catch (IllegalArgumentException e) {
            ChatManager.sendMessage(sender, "Illegal comparison type: " + comparisonTypeRaw);
            return true;
        }

        ComparionOperator comparisonOperator = null;

        try {
            comparisonOperator = ComparionOperator.valueOf(comparisonOperatorRaw);
        } catch (IllegalArgumentException e) {
            ChatManager.sendMessage(sender, "Illegal comparison operator: " + comparisonOperatorRaw);
            return true;
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        PlaceholderModel placeholderModel = new PlaceholderModel(new LinkedHashMap<>());
        placeholderModel.setPlaceholder(placeholder);
        placeholderModel.setComparisonType(comparisonType);
        placeholderModel.setComparisonOperator(comparisonOperator);
        placeholderModel.setComparison(comparison);
        if (name != null) {
            placeholderModel.setName(name);
        }

        quest.addRequirement("placeholders", placeholderModel);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, TranslationManager.getInstance().build("quests.command.edit.add.requirement.placeholder", new Translatable("questID", questID), new Translatable("name", placeholderModel.getName())));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("placeholder");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });

        if (args.length == 7) {
            for (ComparisonType comparisonType : ComparisonType.values()) {
                String comparisonTypeName = comparisonType.name();
                if (comparisonTypeName.toLowerCase().startsWith(args[5].toLowerCase())) {
                    completions.add(comparisonTypeName.toLowerCase());
                }
            }
        }

        if (args.length == 8) {
            for (ComparionOperator comparisonOperator : ComparionOperator.values()) {
                String comparisonOperatorName = comparisonOperator.name();
                if (comparisonOperatorName.toLowerCase().startsWith(args[6].toLowerCase())) {
                    completions.add(comparisonOperatorName.toLowerCase());
                }
            }
        }

        return completions;
    }
}
