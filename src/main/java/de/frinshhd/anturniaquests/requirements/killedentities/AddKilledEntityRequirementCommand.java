package de.frinshhd.anturniaquests.requirements.killedentities;

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

public class AddKilledEntityRequirementCommand extends BasicSubCommand {

    public AddKilledEntityRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement", new String[]{"edit", "<questID>", "add", "requirement", "killedEntity", "<entity>", "<amount>"});
        setDescription("Add a killed entity requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 7) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "requirement", "killedEntity"});
            return true;
        }

            String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        String entity = args[5];

        if (EntityType.fromName(entity.toUpperCase()) == null) {
            ChatManager.sendMessage(sender, "entity" + entity + " does not exist");
            return true;
        }

        String amount = args[6];

        if (!isInteger(amount)) {
            ChatManager.sendMessage(sender, "false value");
            return true;
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        KilledEntityModel killedEntityModel = new KilledEntityModel(new LinkedHashMap<>());
        killedEntityModel.setEntity(EntityType.valueOf(entity.toUpperCase()));
        killedEntityModel.setAmount(Integer.parseInt(amount));

        quest.addRequirement("killedEntities", killedEntityModel);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.add.requirement.killedEntity", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("entityName", killedEntityModel.getName())));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("killedEntity");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
                return;
            }
        });

        if (args.length == 6) {
            for (EntityType entityType : EntityType.values()) {
                String entityName = entityType.name();
                if (entityName.toLowerCase().startsWith(args[5].toLowerCase())) {
                    completions.add(entityName.toLowerCase());
                }
            }
        }


        return completions;
    }

    private boolean isInteger( String input ) {
        try {
            Integer.parseInt( input );
            return true;
        }
        catch( Exception e ) {
            return false;
        }
    }
}
