package de.frinshhd.anturniaquests.requirements.permission;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AddPermissionRequirementCommand extends BasicSubCommand {

    public AddPermissionRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement", new String[]{"edit", "<questID>", "add", "requirement", "permission", "<permission>"});
        setDescription("Adds a permission requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!super.execute(sender, commandLabel, args)) {
            return false;
        }

        if (args.length < 6) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "requirement", "permission"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }


        String permission = args[5];

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        PermissionModel permissionModel = new PermissionModel(new LinkedHashMap<>());
        permissionModel.setPermission(permission);

        quest.addRequirement("permissions", permissionModel);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.add.requirement.permission", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("permission", permission)));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("permission");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });

        return completions;
    }
}
