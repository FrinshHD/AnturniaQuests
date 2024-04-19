package de.frinshhd.anturniaquests.commands.questcommand.subcommands.edit.set;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SetMaterialCommand extends BasicSubCommand {

    public SetMaterialCommand() {
        super("quests", "anturniaquests.command.admin.quests.set.material", new String[]{"edit", "<questID>", "set", "material", "<material>"});
        setDescription("Set the material of a quest's item.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length <= 4) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"help", "edit", "<questID>", "set", "material" });
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        String material = args[4];

        if (Material.getMaterial(material.toUpperCase()) == null) {
            //Todo make nicer message
            sender.sendMessage("material doesn't exist");
            return true;
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        quest.setMaterial(material.toUpperCase());

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.set.material", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("material", material.toUpperCase())));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("material");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 3) {
                completions.add(completion);
                return;
            }

            if (args.length == 4 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
                return;
            }
        });

        if (args.length == 5) {
            for (Material material : Material.values()) {
                String materialName = material.name();
                if (materialName.toLowerCase().startsWith(args[4].toLowerCase())) {
                    completions.add(materialName.toLowerCase());
                }
            }
        }

        return completions;
    }
}
