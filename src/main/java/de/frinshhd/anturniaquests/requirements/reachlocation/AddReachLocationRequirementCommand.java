package de.frinshhd.anturniaquests.requirements.reachlocation;

import de.frinshhd.anturniaquests.Main;
import de.frinshhd.anturniaquests.commands.BasicSubCommand;
import de.frinshhd.anturniaquests.quests.models.Quest;
import de.frinshhd.anturniaquests.requirements.blockinteractions.BlockInteractionsModel;
import de.frinshhd.anturniaquests.utils.ChatManager;
import de.frinshhd.anturniaquests.utils.Translator;
import de.frinshhd.anturniaquests.utils.TranslatorPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AddReachLocationRequirementCommand extends BasicSubCommand {

    public AddReachLocationRequirementCommand() {
        super("quests", "anturniaquests.command.admin.quests.add.requirement", new String[]{"edit", "<questID>", "add", "requirement", "reachLocation", "<x1>", "<y1>", "<z1>", "<x2>",
                "<y2>", "<z2>", "<world>", "[friendlyName]"});
        setDescription("Adds a reachLocation requirement to a quest.");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 12) {
            Main.getCommandManager().getSubCommand(Main.getCommandManager().getCommand(getMainCommand()), "help").execute(sender, commandLabel, new String[]{"edit", "<questID>", "add", "requirement", "blockInteraction"});
            return true;
        }

            String questID = args[1];

        if (Main.getQuestsManager().getEditableQuest(questID) == null) {
            ChatManager.sendMessage(sender, Translator.build("quest.dontExists", new TranslatorPlaceholder("questID", questID)));
            return true;
        }

        List<String> locations1Raw = new ArrayList<>(List.of(args[5], args[6], args[7]));
        List<Integer> locations1 = new ArrayList<>();

        for (String locationRaw : locations1Raw) {
            if (!isInteger(locationRaw)) {
                ChatManager.sendMessage(sender, "false value");
                return true;
            }

            locations1.add(Integer.parseInt(locationRaw));
        }

        List<String> locations2Raw = new ArrayList<>(List.of(args[8], args[9], args[10]));
        List<Integer> locations2 = new ArrayList<>();

        for (String locationRaw : locations2Raw) {
            if (!isInteger(locationRaw)) {
                ChatManager.sendMessage(sender, "false value");
                return true;
            }

            locations2.add(Integer.parseInt(locationRaw));
        }

        String world = args[11];

        if (Bukkit.getWorld(world) == null) {
            ChatManager.sendMessage(sender, "world does not exist");
            return true;
        }


        String friendlyName = null;

        if (args.length > 12) {
            friendlyName = args[12];
        }

        Quest quest = Main.getQuestsManager().getEditableQuest(questID);

        ReachLocationModel reachLocationModel = new ReachLocationModel(new LinkedHashMap<>());
        reachLocationModel.setLocation1(new ArrayList<>(locations1));
        reachLocationModel.setLocation2(new ArrayList<>(locations2));
        reachLocationModel.setWorld(world);

        if (friendlyName != null) {
            reachLocationModel.setFriendlyName(friendlyName);
        }

        quest.addRequirement("reachLocation", reachLocationModel);

        Main.getQuestsManager().saveQuestToYml(questID, quest);

        ChatManager.sendMessage(sender, Translator.build("quest.command.edit.add.requirement.reachLocation", new TranslatorPlaceholder("questID", questID), new TranslatorPlaceholder("location1", reachLocationModel.getLocation1().toString().substring(1, reachLocationModel.getLocation1().toString().length() - 1)), new TranslatorPlaceholder("location2", reachLocationModel.getLocation2().toString().substring(1, reachLocationModel.getLocation2().toString().length() - 1))));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (super.tabComplete(sender, args) == null) {
            return new ArrayList<>();
        }
        List<String> possibleCompletions = new ArrayList<>();
        List<String> completions = new ArrayList<>();

        possibleCompletions.add("reachLocation");

        // Filter
        possibleCompletions.forEach(completion -> {
            if (args.length == 5 && completion.toLowerCase().startsWith(args[3])) {
                completions.add(completion);
            }
        });

        if (args.length == 12) {
            Bukkit.getWorlds().forEach(world -> {
                String worldName = world.getName();
                if (worldName.toLowerCase().startsWith(args[11].toLowerCase())) {
                    completions.add(worldName.toLowerCase());
                }
            });
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
