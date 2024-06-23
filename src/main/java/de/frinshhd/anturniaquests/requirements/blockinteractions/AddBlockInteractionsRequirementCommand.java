package de.frinshhd.anturniaquests.requirements.blockinteractions;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AddBlockInteractionsRequirementCommand extends BasicSubCommand {

    public AddBlockInteractionsRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement", new String[]{"edit", "<questID>", "add", "requirement", "blockInteraction", "<x>", "<y>", "<z>", "<world>", "[interactAction]"});
        setDescription("Adds a block interaction requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 9) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "requirement", "blockInteraction"});
            return true;
        }

        String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        List<String> locationsRaw = new ArrayList<>(List.of(args[5], args[6], args[7]));
        List<Integer> locations = new ArrayList<>();

        for (String locationRaw : locationsRaw) {
            if (!isInteger(locationRaw)) {
                ChatManager.sendMessage(sender, "false value");
                return true;
            }

            locations.add(Integer.parseInt(locationRaw));
        }

        String world = args[8];

        if (Bukkit.getWorld(world) == null) {
            ChatManager.sendMessage(sender, "world does not exist");
            return true;
        }


        String interactAction = null;

        if (args.length > 9) {
            interactAction = args[9];
        }

        if (interactAction != null) {
            if (!interactAction.equalsIgnoreCase("LEFT_CLICK") && !interactAction.equalsIgnoreCase("RIGHT_CLICK")) {
                ChatManager.sendMessage(sender, "false value for interactActions");
                return true;
            }
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        BlockInteractionsModel blockInteractionsModel = new BlockInteractionsModel(new LinkedHashMap<>());
        blockInteractionsModel.setLocation(new ArrayList<>(locations));
        blockInteractionsModel.setWorld(world);

        if (interactAction != null) {
            blockInteractionsModel.setInteractActions(new ArrayList<>(List.of(interactAction)));
        }

        quest.addRequirement("blockInteractions", blockInteractionsModel);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.add.requirement.blockInteraction", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("location", blockInteractionsModel.getLocation().toString().substring(1, blockInteractionsModel.getLocation().toString().length() - 1))));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("blockInteraction");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });

        if (args.length == 9) {
            Bukkit.getWorlds().forEach(world -> {
                String worldName = world.getName();
                if (worldName.toLowerCase().startsWith(args[8].toLowerCase())) {
                    completions.add(worldName.toLowerCase());
                }
            });
        }

        if (args.length == 10) {
            ArrayList<String> possible = new ArrayList<>(List.of("LEFT_CLICK", "RIGHT_CLICK"));
            possible.forEach(completion -> {
                if (completion.startsWith(args[9].toLowerCase())) {
                    completions.add(completion);
                }
            });
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
