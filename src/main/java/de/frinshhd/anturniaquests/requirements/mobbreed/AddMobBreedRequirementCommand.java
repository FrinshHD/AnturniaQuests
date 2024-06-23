package de.frinshhd.anturniaquests.requirements.mobbreed;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AddMobBreedRequirementCommand extends BasicSubCommand {

    public AddMobBreedRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement", new String[]{"edit", "<questID>", "add", "requirement", "mobBreed", "<entity>", "[amount]"});
        setDescription("Add a mob breed requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 6) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "requirement", "mobBreed"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        String entityRaw = args[5];

        if (entityRaw == null || EntityType.fromName(entityRaw.toUpperCase()) == null) {
            ChatManager.sendMessage(sender, "entity does not exist");
            return true;
        }

        String amount = null;

        if (args.length > 7) {
            amount = args[6];

            if (isInteger(amount)) {
                ChatManager.sendMessage(sender, "false value");
                return true;
            }
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        MobBreedModel mobBreedModel = new MobBreedModel(new LinkedHashMap<>());
        mobBreedModel.setEntity(EntityType.valueOf(entityRaw.toUpperCase()));

        if (amount != null) {
            mobBreedModel.setAmount(Integer.parseInt(amount));
        }

        quest.addRequirement("mobBreed", mobBreedModel);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.add.requirement.mobBreed", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("entityName", mobBreedModel.getName()), new TranslatorPlaceholder("amount", String.valueOf(mobBreedModel.getAmount()))));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("mobBreed");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });

        if (args.length == 6) {
            for (EntityType entityType : EntityType.values()) {
                String entityName = entityType.name();
                if (entityName.toLowerCase().startsWith(args[5].toLowerCase())) {
                    completions.add(entityName);
                }
            }
        }

        return completions;
    }

    private boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
